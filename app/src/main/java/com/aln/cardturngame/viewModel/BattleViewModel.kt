package com.aln.cardturngame.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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

class BattleViewModel(
  val leftTeam: Team,
  val rightTeam: Team
) : ViewModel() {

  // State
  var dragState by mutableStateOf<DragState?>(null)
    private set
  var hoveredTarget by mutableStateOf<EntityViewModel?>(null)
    private set
  var showInfoDialog by mutableStateOf(false)
  var selectedEntity by mutableStateOf<EntityViewModel?>(null)

  // Turn & Game State
  var isLeftTeamTurn by mutableStateOf(Random.nextBoolean())
    private set
  var isActionPlaying by mutableStateOf(false)
    private set
  var winner by mutableStateOf<String?>(null)
    private set

  private val actionsTaken = mutableStateListOf<EntityViewModel>()
  val cardBounds = mutableStateMapOf<EntityViewModel, Rect>()

  // Logic
  fun canEntityAct(entity: EntityViewModel): Boolean {
    if (winner != null) return false
    val isLeft = leftTeam.entities.contains(entity)
    val isRight = rightTeam.entities.contains(entity)
    val isTurn = (isLeft && isLeftTeamTurn) || (isRight && !isLeftTeamTurn)

    return isTurn && !actionsTaken.contains(entity) && entity.isAlive && !isActionPlaying
  }

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

  fun onDoubleTap(entity: EntityViewModel) {
    println("Double tapped ${entity.name}")
  }

  fun onPressStatus(entity: EntityViewModel, isPressed: Boolean) {
    if (isPressed) {
      selectedEntity = entity
      showInfoDialog = true
    } else {
      showInfoDialog = false
      selectedEntity = null
    }
  }

  fun getHighlightColor(entity: EntityViewModel): androidx.compose.ui.graphics.Color {
    val draggingState = dragState
    return if (draggingState != null && entity == hoveredTarget) {
      val sourceLeft = leftTeam.entities.contains(draggingState.source)
      val targetLeft = leftTeam.entities.contains(entity)
      if (sourceLeft == targetLeft) androidx.compose.ui.graphics.Color.Green else androidx.compose.ui.graphics.Color.Red
    } else {
      androidx.compose.ui.graphics.Color.Transparent
    }
  }

  private fun checkWinCondition() {
    val isLeftAlive = leftTeam.entities.any { it.isAlive }
    val isRightAlive = rightTeam.entities.any { it.isAlive }

    if (!isLeftAlive) {
      winner = "Right Team Wins!"
    } else if (!isRightAlive) {
      winner = "Left Team Wins!"
    }
  }

  private fun executeInteraction(source: EntityViewModel, target: EntityViewModel) {
    if (isActionPlaying || winner != null) return

    viewModelScope.launch {
      isActionPlaying = true
      handleCardInteraction(source, target)

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