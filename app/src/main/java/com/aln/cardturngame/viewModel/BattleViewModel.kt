package com.aln.cardturngame.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aln.cardturngame.entity.Team
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DragState(
  val source: EntityViewModel,
  val start: Offset,
  val current: Offset
)

data class UltimateDragState(
  val team: Team,
  val start: Offset,
  val current: Offset
)

class BattleViewModel(
  val leftTeam: Team,
  val rightTeam: Team
) : ViewModel() {

  // ... [Existing State properties] ...
  var dragState by mutableStateOf<DragState?>(null)
    private set
  var ultimateDragState by mutableStateOf<UltimateDragState?>(null)
    private set

  var hoveredTarget by mutableStateOf<EntityViewModel?>(null)
    private set
  var showInfoDialog by mutableStateOf(false)
  var selectedEntity by mutableStateOf<EntityViewModel?>(null)

  var isLeftTeamTurn by mutableStateOf(Random.nextBoolean())
    private set
  var isActionPlaying by mutableStateOf(false)
    private set
  var winner by mutableStateOf<String?>(null)
    private set

  private val actionsTaken = mutableStateListOf<EntityViewModel>()
  val cardBounds = mutableStateMapOf<EntityViewModel, Rect>()

  fun canEntityAct(entity: EntityViewModel): Boolean {
    if (winner != null) return false
    val isLeft = leftTeam.entities.contains(entity)
    val isRight = rightTeam.entities.contains(entity)
    val isTurn = (isLeft && isLeftTeamTurn) || (isRight && !isLeftTeamTurn)
    return isTurn && !actionsTaken.contains(entity) && entity.isAlive && !isActionPlaying
  }

  // --- Rage & Ultimate Logic ---

  fun increaseRage(team: Team, amount: Float) {
    team.rage = (team.rage + amount).coerceAtMost(team.maxRage)
  }

  fun onUltimateDragStart(team: Team, offset: Offset) {
    // Allow drag only if it's that team's turn and rage is full
    val isLeft = (team == leftTeam)
    if ((isLeft && isLeftTeamTurn) || (!isLeft && !isLeftTeamTurn)) {
      if (team.rage >= team.maxRage && !isActionPlaying && winner == null) {
        ultimateDragState = UltimateDragState(team, offset, offset)
      }
    }
  }

  fun onUltimateDrag(change: Offset) {
    ultimateDragState?.let { current ->
      val newPos = current.current + change
      ultimateDragState = current.copy(current = newPos)

      // Check for hover over valid targets (Allies of the casting team)
      hoveredTarget = cardBounds.entries.firstOrNull { (entity, rect) ->
        entity.isAlive && rect.contains(newPos) && current.team.entities.contains(entity)
      }?.key
    }
  }

  fun onUltimateDragEnd() {
    val state = ultimateDragState
    val target = hoveredTarget

    if (state != null && target != null && state.team.entities.contains(target)) {
      executeUltimate(state.team, target)
    }

    ultimateDragState = null
    hoveredTarget = null
  }

  private fun executeUltimate(team: Team, caster: EntityViewModel) {
    if (isActionPlaying || winner != null) return

    viewModelScope.launch {
      isActionPlaying = true

      // Consume Rage
      team.rage = 0f

      // Determine targets (Simple logic: Pick a random alive enemy)
      val enemies = if (team == leftTeam) rightTeam else leftTeam
      val validTargets = enemies.entities.filter { it.isAlive }

      if (validTargets.isNotEmpty()) {
        val randomEnemy = validTargets.random()
        println("ULTIMATE! ${caster.name} attacks ${randomEnemy.name}")

        // Execute the ultimate ability
        caster.entity.ultimateAbility.effect(caster, randomEnemy)
      }

      checkWinCondition()
      isActionPlaying = false
    }
  }

  // --- Existing Drag Logic (Updated for Rage Gain) ---

  fun onDragStart(char: EntityViewModel, offset: Offset) {
    if (canEntityAct(char)) {
      val cardTopLeft = cardBounds[char]?.topLeft ?: Offset.Zero
      val globalStart = cardTopLeft + offset
      dragState = DragState(char, globalStart, globalStart)
    }
  }

  fun onDrag(change: Offset) {
    dragState?.let { currentDrag ->
      val newCurrent = currentDrag.current + change
      dragState = currentDrag.copy(current = newCurrent)

      hoveredTarget = cardBounds.entries.firstOrNull { (entity, rect) ->
        entity.isAlive && rect.contains(newCurrent)
      }?.key
    }
  }

  fun onDragEnd() {
    val state = dragState
    val target = hoveredTarget

    if (state != null && target != null && target.isAlive && canEntityAct(state.source)) {
      executeInteraction(state.source, target)
    }

    dragState = null
    hoveredTarget = null
  }

  fun onCardPositioned(entity: EntityViewModel, rect: Rect) {
    cardBounds[entity] = rect
  }

  fun onDoubleTap(entity: EntityViewModel) { println("Double tapped ${entity.name}") }

  fun onPressStatus(entity: EntityViewModel, isPressed: Boolean) {
    if (isPressed) {
      selectedEntity = entity
      showInfoDialog = true
    } else {
      showInfoDialog = false
      selectedEntity = null
    }
  }

  fun getHighlightColor(entity: EntityViewModel): Color {
    val draggingState = dragState
    val ultState = ultimateDragState

    // Highlight logic for Normal Card Drag
    if (draggingState != null && entity == hoveredTarget) {
      val sourceLeft = leftTeam.entities.contains(draggingState.source)
      val targetLeft = leftTeam.entities.contains(entity)
      return if (sourceLeft == targetLeft) Color.Green else Color.Red
    }

    // Highlight logic for Ultimate Drag (Only highlight allies)
    if (ultState != null && entity == hoveredTarget) {
      if (ultState.team.entities.contains(entity)) return Color.Cyan // Distinct color for Ult target
    }

    return Color.Transparent
  }

  private fun checkWinCondition() {
    val isLeftAlive = leftTeam.entities.any { it.isAlive }
    val isRightAlive = rightTeam.entities.any { it.isAlive }

    if (!isLeftAlive) winner = "Right Team Wins!"
    else if (!isRightAlive) winner = "Left Team Wins!"
  }

  private fun executeInteraction(source: EntityViewModel, target: EntityViewModel) {
    if (isActionPlaying || winner != null) return

    viewModelScope.launch {
      isActionPlaying = true
      handleCardInteraction(source, target)

      // Rage Gain Logic
      val sourceTeam = if(leftTeam.entities.contains(source)) leftTeam else rightTeam
      val targetTeam = if(leftTeam.entities.contains(target)) leftTeam else rightTeam

      increaseRage(sourceTeam, 50f) // Gain rage for attacking
      if (sourceTeam != targetTeam) {
        increaseRage(targetTeam, 20f) // Gain rage for taking damage
      }

      checkWinCondition()
      if (winner != null) {
        isActionPlaying = false
        return@launch
      }

      if (!actionsTaken.contains(source)) {
        actionsTaken.add(source)
      }

      val activeTeamEntities = if (isLeftTeamTurn) leftTeam.entities else rightTeam.entities
      if (actionsTaken.containsAll(activeTeamEntities.filter { it.isAlive })) {
        actionsTaken.clear()
        isLeftTeamTurn = !isLeftTeamTurn

        val nextTeam = if (isLeftTeamTurn) leftTeam else rightTeam
        processStartOfTurnEffects(nextTeam)

        // Passive rage gain per turn
        increaseRage(nextTeam, 10f)

        checkWinCondition()
      }

      isActionPlaying = false
    }
  }

  private suspend fun handleCardInteraction(source: EntityViewModel, target: EntityViewModel) {
    val sourceLeft = leftTeam.entities.contains(source)
    val targetLeft = leftTeam.entities.contains(target)

    val onSameTeam = sourceLeft == targetLeft

    if (onSameTeam) {
      source.entity.passiveAbility.effect(source, target)
    } else {
      source.entity.activeAbility.effect(source, target)
    }
  }

  private suspend fun processStartOfTurnEffects(team: Team) {
    team.entities.filter { it.isAlive }.forEach { entity ->

      entity.traits.forEach { trait ->
        trait.onStartTurn(entity)
      }

      val activeEffects = entity.statusEffects.toList()
      activeEffects.forEach { effect ->
        effect.onStartTurn(entity)
        if (effect.tick()) {
          entity.removeStatusEffect(effect)
        }
      }
    }
  }
}