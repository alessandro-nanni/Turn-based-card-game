package com.aln.cardturngame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.entity.Entity
import com.aln.cardturngame.entity.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Battleground(val leftTeam: Team, val rightTeam: Team) {

  private data class DragState(
    val source: Entity,
    val start: Offset,
    val current: Offset
  )

  private inner class BattleScreenState(private val scope: CoroutineScope) {
    var dragState by mutableStateOf<DragState?>(null)
    var hoveredTarget by mutableStateOf<Entity?>(null)
    var showInfoDialog by mutableStateOf(false)
    var selectedEntity by mutableStateOf<Entity?>(null)

    // Turn & Game State
    var isLeftTeamTurn by mutableStateOf(true)
    var isActionPlaying by mutableStateOf(false)
    val actionsTaken = mutableStateListOf<Entity>()
    val cardBounds = mutableStateMapOf<Entity, Rect>()

    fun canEntityAct(entity: Entity): Boolean {
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

    private fun executeInteraction(source: Entity, target: Entity) {
      if (isActionPlaying) return

      scope.launch {
        isActionPlaying = true
        // handleCardInteraction is accessed from the outer Battleground class
        handleCardInteraction(source, target, rightTeam.entities)

        if (!actionsTaken.contains(source)) {
          actionsTaken.add(source)
        }

        val activeTeamEntities = if (isLeftTeamTurn) leftTeam.entities else rightTeam.entities
        if (actionsTaken.containsAll(activeTeamEntities.filter { it.isAlive })) {
          actionsTaken.clear()
          isLeftTeamTurn = !isLeftTeamTurn
        }

        isActionPlaying = false
      }
    }
  }

  @Composable
  fun BattleScreen() {
    val scope = rememberCoroutineScope()
    val state = remember(scope) { BattleScreenState(scope) }

    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF1E1E1E))
    ) {
      val finalCardHeight = min(
        this.maxHeight / 3.4f,
        (this.maxWidth / 2.5f) / 0.7f
      )
      val finalCardWidth = finalCardHeight * 0.7f

      // Line Drawing
      state.dragState?.let { dragState ->
        val lineEnd = if (state.hoveredTarget != null && state.cardBounds.contains(state.hoveredTarget)) {
          state.cardBounds[state.hoveredTarget]!!.center
        } else {
          dragState.current
        }
        LineCanvas(dragState.start, lineEnd)
      }

      BattleLayout(
        finalCardHeight = finalCardHeight,
        finalCardWidth = finalCardWidth,
        leftTeam = leftTeam,
        rightTeam = rightTeam,
        canAct = state::canEntityAct,
        onCardPositioned = { char, rect -> state.cardBounds[char] = rect },
        onDragStart = state::onDragStart,
        onDrag = state::onDrag,
        onDragEnd = state::onDragEnd,
        onDoubleTap = { entity -> println("Double tapped ${entity.name}") },
        onPressStatus = { entity, isPressed ->
          if (isPressed) {
            state.selectedEntity = entity
            state.showInfoDialog = true
          } else {
            state.showInfoDialog = false
            state.selectedEntity = null
          }
        },
        getHighlightColor = { entity ->
          val draggingState = state.dragState
          if (draggingState != null && entity == state.hoveredTarget) {
            val sourceLeft = leftTeam.entities.contains(draggingState.source)
            val targetLeft = leftTeam.entities.contains(entity)
            if (sourceLeft == targetLeft) Color.Green else Color.Red
          } else {
            Color.Transparent
          }
        }
      )

      if (state.showInfoDialog && state.selectedEntity != null) {
        state.selectedEntity!!.InfoCard()
      }
    }
  }


  @Composable
  fun LineCanvas(dragStart: Offset, dragCurrent: Offset) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawLine(
        color = Color.White,
        start = dragStart,
        end = dragCurrent,
        strokeWidth = 8f
      )
    }
  }

  private suspend fun handleCardInteraction(
    source: Entity,
    target: Entity,
    ownTeam: List<Entity>,
  ) {
    val sourceIsPlayer = ownTeam.contains(source)
    val targetIsPlayer = ownTeam.contains(target)

    if (sourceIsPlayer == targetIsPlayer) {
      source.passiveAbility.effect(source, target)
    } else {
      source.activeAbility.effect(source, target)
    }
  }

  @Composable
  fun BattleLayout(
    finalCardHeight: Dp,
    finalCardWidth: Dp,
    leftTeam: Team,
    rightTeam: Team,
    canAct: (Entity) -> Boolean,
    onCardPositioned: (Entity, Rect) -> Unit,
    onDragStart: (Entity, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: (Entity) -> Unit,
    onPressStatus: (Entity, Boolean) -> Unit,
    getHighlightColor: (Entity) -> Color
  ) {

    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(start = 40.dp, end = 40.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      leftTeam.TeamColumn(
        alignment = Alignment.Start,
        cardWidth = finalCardWidth,
        cardHeight = finalCardHeight,
        canAct = canAct,
        onCardPositioned = onCardPositioned,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        onDoubleTap = onDoubleTap,
        onPressStatus = onPressStatus,
        getHighlightColor = getHighlightColor
      )

      Text(
        text = "VS",
        color = Color.Gray,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.alpha(0.5f)
      )

      rightTeam.TeamColumn(
        alignment = Alignment.End,
        cardWidth = finalCardWidth,
        cardHeight = finalCardHeight,
        canAct = canAct,
        onCardPositioned = onCardPositioned,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        onDoubleTap = onDoubleTap,
        onPressStatus = onPressStatus,
        getHighlightColor = getHighlightColor
      )
    }
  }
}