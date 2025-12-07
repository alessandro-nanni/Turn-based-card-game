package com.aln.cardturngame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.ui.InfoCard
import com.aln.cardturngame.viewModel.BattleViewModel

@Composable
fun BattleScreen(viewModel: BattleViewModel) {
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

    // --- Layer 1: The Teams (Centered) ---
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // LEFT TEAM
      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        viewModel.leftTeam.TeamColumn(
          alignment = Alignment.Start,
          cardWidth = finalCardWidth,
          cardHeight = finalCardHeight,
          canAct = viewModel::canEntityAct,
          onCardPositioned = viewModel::onCardPositioned,
          onDragStart = viewModel::onDragStart,
          onDrag = viewModel::onDrag,
          onDragEnd = viewModel::onDragEnd,
          onDoubleTap = viewModel::onDoubleTap,
          onPressStatus = viewModel::onPressStatus,
          getHighlightColor = viewModel::getHighlightColor
        )
      }

      // VS Separator
      Text(
        text = "VS",
        color = Color.Gray,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.alpha(0.5f)
      )

      // RIGHT TEAM
      Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
        viewModel.rightTeam.TeamColumn(
          alignment = Alignment.End,
          cardWidth = finalCardWidth,
          cardHeight = finalCardHeight,
          canAct = viewModel::canEntityAct,
          onCardPositioned = viewModel::onCardPositioned,
          onDragStart = viewModel::onDragStart,
          onDrag = viewModel::onDrag,
          onDragEnd = viewModel::onDragEnd,
          onDoubleTap = viewModel::onDoubleTap,
          onPressStatus = viewModel::onPressStatus,
          getHighlightColor = viewModel::getHighlightColor
        )
      }
    }

    // --- Layer 2: Rage Bars (Corners) ---

    // Left Team Rage Bar -> Bottom Left
    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(24.dp)
    ) {
      RageBar(
        rage = viewModel.leftTeam.rage,
        maxRage = viewModel.leftTeam.maxRage,
        isTurn = viewModel.isLeftTeamTurn,
        onDragStart = { offset -> viewModel.onUltimateDragStart(viewModel.leftTeam, offset) },
        onDrag = viewModel::onUltimateDrag,
        onDragEnd = viewModel::onUltimateDragEnd
      )
    }

    // Right Team Rage Bar -> Top Right
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(24.dp)
    ) {
      RageBar(
        rage = viewModel.rightTeam.rage,
        maxRage = viewModel.rightTeam.maxRage,
        isTurn = !viewModel.isLeftTeamTurn,
        onDragStart = { offset -> viewModel.onUltimateDragStart(viewModel.rightTeam, offset) },
        onDrag = viewModel::onUltimateDrag,
        onDragEnd = viewModel::onUltimateDragEnd
      )
    }

    // --- Layer 3: Drag Lines ---
    viewModel.dragState?.let { dragState ->
      val lineEnd =
        if (viewModel.hoveredTarget != null && viewModel.cardBounds.contains(viewModel.hoveredTarget)) {
          viewModel.cardBounds[viewModel.hoveredTarget]!!.center
        } else {
          dragState.current
        }
      LineCanvas(dragState.start, lineEnd, Color.White)
    }

    viewModel.ultimateDragState?.let { ultState ->
      val lineEnd = if (viewModel.hoveredTarget != null && viewModel.cardBounds.contains(viewModel.hoveredTarget)) {
        viewModel.cardBounds[viewModel.hoveredTarget]!!.center
      } else {
        ultState.current
      }
      LineCanvas(ultState.start, lineEnd, Color(0xFFFF5722)) // Orange/Red line
    }

    // --- Layer 4: UI Overlays ---
    if (viewModel.showInfoDialog && viewModel.selectedEntity != null) {
      InfoCard(viewModel.selectedEntity!!)
    }

    if (viewModel.winner != null) {
      Winner(viewModel)
    }
  }
}

@Composable
fun Winner(viewModel: BattleViewModel){
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.85f))
      .clickable(enabled = true) {},
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = viewModel.winner!!,
      color = Color.Yellow,
      fontSize = 48.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun LineCanvas(dragStart: Offset, dragCurrent: Offset, color: Color) {
  Canvas(modifier = Modifier.fillMaxSize()) {
    drawLine(
      color = color,
      start = dragStart,
      end = dragCurrent,
      strokeWidth = 8f
    )
  }
}

@Composable
fun RageBar(
  rage: Float,
  maxRage: Float,
  isTurn: Boolean,
  onDragStart: (Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit
) {
  // Horizontal Dimensions
  val barWidth = 300.dp
  val barHeight = 24.dp
  val progress = (rage / maxRage).coerceIn(0f, 1f)
  val isFull = progress >= 1f

  // Store global position for accurate drag start
  var iconCenterGlobal by remember { mutableStateOf(Offset.Zero) }

  Box(
    contentAlignment = Alignment.CenterStart, // Align contents to start for horizontal fill
    modifier = Modifier
      .width(barWidth)
      .height(barHeight)
    // Note: We do NOT clip here. Clipping the parent hides the overflowing icon.
  ) {
    // 1. Background/Track (Clipped)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(12.dp))
        .background(Color.DarkGray)
        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
    ) {
      // 2. Fill
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(progress) // Fill horizontally
          .background(
            brush = Brush.horizontalGradient( // Horizontal gradient
              colors = listOf(Color(0xFFFF5722), Color(0xFFFFC107))
            )
          )
      )
    }

    // 3. Draggable Fire Icon
    // Only appears when full and it's the team's turn
    if (isFull && isTurn) {
      Box(
        modifier = Modifier
          .align(Alignment.CenterEnd) // Position at the end of the bar
          .offset(x = 15.dp) // Pop out to the right slightly
          .size(48.dp) // Slightly larger touch target
          .clip(CircleShape)
          .background(Color.Red)
          .border(2.dp, Color.Yellow, CircleShape)
          .onGloballyPositioned { coordinates ->
            // Capture the center of the icon in global coordinates
            val size = coordinates.size
            val position = coordinates.positionInRoot()
            iconCenterGlobal = Offset(
              x = position.x + size.width / 2f,
              y = position.y + size.height / 2f
            )
          }
          .pointerInput(Unit) {
            detectDragGestures(
              onDragStart = { _ ->
                // Use the global position we captured, NOT the local offset
                onDragStart(iconCenterGlobal)
              },
              onDrag = { change, dragAmount ->
                change.consume()
                onDrag(dragAmount)
              },
              onDragEnd = { onDragEnd() }
            )
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = R.drawable.ultimate),
          contentDescription = "Fire Icon",
          tint = Color.Yellow,
          modifier = Modifier.size(28.dp)
        )
      }
    }
  }
}