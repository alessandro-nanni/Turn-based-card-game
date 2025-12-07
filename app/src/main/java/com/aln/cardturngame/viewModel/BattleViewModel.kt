package com.aln.cardturngame.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aln.cardturngame.entity.Entity
import com.aln.cardturngame.entity.Team
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DragState(
  val source: Entity,
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
  var hoveredTarget by mutableStateOf<Entity?>(null)
    private set
  var showInfoDialog by mutableStateOf(false)
  var selectedEntity by mutableStateOf<Entity?>(null)

  // Turn & Game State
  var isLeftTeamTurn by mutableStateOf(Random.nextBoolean())
    private set
  var isActionPlaying by mutableStateOf(false)
    private set
  var winner by mutableStateOf<String?>(null)
    private set

  private val actionsTaken = mutableStateListOf<Entity>()
  val cardBounds = mutableStateMapOf<Entity, Rect>()

  // Logic
  fun canEntityAct(entity: Entity): Boolean {
    if (winner != null) return false
    val isLeft = leftTeam.entities.contains(entity)
    val isRight = rightTeam.entities.contains(entity)
    val isTurn = (isLeft && isLeftTeamTurn) || (isRight && !isLeftTeamTurn)

    return isTurn && !actionsTaken.contains(entity) && entity.isAlive && !isActionPlaying
  }

  fun onDragStart(char: Entity, offset: Offset) {
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

  fun onCardPositioned(entity: Entity, rect: Rect) {
    cardBounds[entity] = rect
  }

  fun onDoubleTap(entity: Entity) {
    // Optional: Add double tap logic here
    println("Double tapped ${entity.name}")
  }

  fun onPressStatus(entity: Entity, isPressed: Boolean) {
    if (isPressed) {
      selectedEntity = entity
      showInfoDialog = true
    } else {
      showInfoDialog = false
      selectedEntity = null
    }
  }

  fun getHighlightColor(entity: Entity): androidx.compose.ui.graphics.Color {
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

  private fun executeInteraction(source: Entity, target: Entity) {
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

  private suspend fun handleCardInteraction(source: Entity, target: Entity) {
      // Determine if source and target are on the same team
      val sourceLeft = leftTeam.entities.contains(source)
      val targetLeft = leftTeam.entities.contains(target)
      
      val onSameTeam = sourceLeft == targetLeft

      if (onSameTeam) {
          source.passiveAbility.effect(source, target)
      } else {
          source.activeAbility.effect(source, target)
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

class BattleViewModelFactory(
  private val leftTeam: Team,
  private val rightTeam: Team
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(BattleViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return BattleViewModel(leftTeam, rightTeam) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}