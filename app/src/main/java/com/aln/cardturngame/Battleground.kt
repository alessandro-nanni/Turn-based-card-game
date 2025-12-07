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
import kotlinx.coroutines.launch

class Battleground(val leftTeam: Team, val rightTeam: Team) {

  @Composable
  fun BattleScreen() {
    val scope = rememberCoroutineScope()

    // Drag & Drop State
    var draggingSource by remember { mutableStateOf<Entity?>(null) }
    var dragStart by remember { mutableStateOf(Offset.Zero) }
    var dragCurrent by remember { mutableStateOf(Offset.Zero) }
    var hoveredTarget by remember { mutableStateOf<Entity?>(null) }

    // Info Dialog State
    var showInfoDialog by remember { mutableStateOf(false) }
    var selectedEntity by remember { mutableStateOf<Entity?>(null) }

    // Turn State
    var isLeftTeamTurn by remember { mutableStateOf(true) }
    val actionsTaken = remember { mutableStateListOf<Entity>() }
    // Lock state to prevent interactions during animations
    var isActionPlaying by remember { mutableStateOf(false) }

    val cardBounds = remember { mutableStateMapOf<Entity, Rect>() }

    val canEntityAct: (Entity) -> Boolean = { entity ->
      val isLeft = leftTeam.entities.contains(entity)
      val isRight = rightTeam.entities.contains(entity)

      val isTurn = (isLeft && isLeftTeamTurn) || (isRight && !isLeftTeamTurn)

      // Entity must be Alive to act (gets outline)
      // AND no action should be currently playing
      isTurn && !actionsTaken.contains(entity) && entity.isAlive && !isActionPlaying
    }

    fun onActionCompleted(source: Entity) {
      if (!actionsTaken.contains(source)) {
        actionsTaken.add(source)
      }

      val activeTeamEntities = if (isLeftTeamTurn) leftTeam.entities else rightTeam.entities

      // Filter actions check to only care about ALIVE entities
      val aliveTeamEntities = activeTeamEntities.filter { it.isAlive }

      if (actionsTaken.containsAll(aliveTeamEntities)) {
        actionsTaken.clear()
        isLeftTeamTurn = !isLeftTeamTurn
      }
    }

    // Helper to launch suspend functions with lock
    fun executeInteraction(source: Entity, target: Entity, ownTeam: List<Entity>) {
      if (isActionPlaying) return

      scope.launch {
        isActionPlaying = true // Lock input
        handleCardInteraction(source, target, ownTeam)
        onActionCompleted(source)
        isActionPlaying = false // Unlock input
      }
    }

    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF1E1E1E))
    ) {
      val heightLimit = this.maxHeight / 3.4f
      val widthLimit = this.maxWidth / 2.5f
      val aspectRatio = 0.7f
      val heightFromWidth = widthLimit / aspectRatio
      val finalCardHeight = min(heightLimit, heightFromWidth)
      val finalCardWidth = finalCardHeight * aspectRatio

      // Draw line BEHIND the cards
      val lineEnd = if (hoveredTarget != null && cardBounds.contains(hoveredTarget)) {
        cardBounds[hoveredTarget]!!.center
      } else {
        dragCurrent
      }

      if (draggingSource != null) {
        LineCanvas(dragStart, lineEnd)
      }

      BattleLayout(
        finalCardHeight = finalCardHeight,
        finalCardWidth = finalCardWidth,
        leftTeam = leftTeam,
        rightTeam = rightTeam,
        canAct = canEntityAct,
        onCardPositioned = { char, rect ->
          cardBounds[char] = rect
        },
        onDragStart = { char, offset ->
          if (canEntityAct(char)) {
            draggingSource = char
            val cardTopLeft = cardBounds[char]?.topLeft ?: Offset.Zero
            val globalStart = cardTopLeft + offset
            dragStart = globalStart
            dragCurrent = globalStart
          }
        },
        onDrag = { change ->
          dragCurrent += change
          hoveredTarget = cardBounds.entries.firstOrNull { (entity, rect) ->
            entity.isAlive && rect.contains(dragCurrent)
          }?.key
        },
        onDragEnd = {
          draggingSource?.let { source ->
            val target = hoveredTarget

            if (target != null && target.isAlive) {
              if (canEntityAct(source)) {
                executeInteraction(source, target, rightTeam.entities)
              }
            }
          }
          draggingSource = null
          hoveredTarget = null
        },
        onDoubleTap = { entity ->
          println("Double tapped ${entity.name}")
        },
        onPressStatus = { entity, isPressed ->
          if (isPressed) {
            selectedEntity = entity
            showInfoDialog = true
          } else {
            showInfoDialog = false
            selectedEntity = null
          }
        },
        getHighlightColor = { entity ->
          if (draggingSource != null && entity == hoveredTarget) {
            val source = draggingSource!!
            val isSourceLeft = leftTeam.entities.contains(source)
            val isTargetLeft = leftTeam.entities.contains(entity)
            if (isSourceLeft == isTargetLeft) Color.Green else Color.Red
          } else {
            Color.Transparent
          }
        }
      )

      // Overlay for Info (Shown while holding)
      if (showInfoDialog && selectedEntity != null) {
        selectedEntity!!.InfoCard()
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