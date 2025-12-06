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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

class Battleground(val leftTeam: Team, val rightTeam: Team) {

  @Composable
  fun BattleScreen() {

    // Drag & Drop State
    var draggingSource by remember { mutableStateOf<Entity?>(null) }
    var dragStart by remember { mutableStateOf(Offset.Zero) }
    var dragCurrent by remember { mutableStateOf(Offset.Zero) }

    // Info Dialog State
    var showInfoDialog by remember { mutableStateOf(false) }
    var selectedEntity by remember { mutableStateOf<Entity?>(null) }

    val cardBounds = remember { mutableStateMapOf<Entity, Rect>() }

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

      BattleLayout(
        finalCardHeight = finalCardHeight,
        finalCardWidth = finalCardWidth,
        leftTeam = leftTeam,
        rightTeam = rightTeam,
        onCardPositioned = { char, rect ->
          cardBounds[char] = rect
        },
        onDragStart = { char, offset ->
          draggingSource = char
          val cardTopLeft = cardBounds[char]?.topLeft ?: Offset.Zero
          val globalStart = cardTopLeft + offset
          dragStart = globalStart
          dragCurrent = globalStart
        },
        onDrag = { change ->
          dragCurrent += change
        },
        onDragEnd = {
          draggingSource?.let { source ->
            val target = cardBounds.entries.firstOrNull { (_, rect) ->
              rect.contains(dragCurrent)
            }?.key

            if (target != null && target != source) {
              handleCardInteraction(source, target, rightTeam.entities)
            }
          }
          draggingSource = null
        },
        onDoubleTap = { entity ->
          entity.passiveAbility.effect(entity, entity)
        },
        onPressStatus = { entity, isPressed ->
          if (isPressed) {
            selectedEntity = entity
            showInfoDialog = true
          } else {
            showInfoDialog = false
            selectedEntity = null
          }
        }
      )

      if (draggingSource != null) {
        LineCanvas(dragStart, dragCurrent)
      }

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
        color = Color.Red,
        start = dragStart,
        end = dragCurrent,
        strokeWidth = 8f
      )
    }
  }

  private fun handleCardInteraction(
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
    onCardPositioned: (Entity, Rect) -> Unit,
    onDragStart: (Entity, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: (Entity) -> Unit,
    onPressStatus: (Entity, Boolean) -> Unit
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
        onCardPositioned = onCardPositioned,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        onDoubleTap = onDoubleTap,
        onPressStatus = onPressStatus
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
        onCardPositioned = onCardPositioned,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd,
        onDoubleTap = onDoubleTap,
        onPressStatus = onPressStatus
      )
    }
  }

}