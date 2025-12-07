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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.ui.InfoCard
import com.aln.cardturngame.viewModel.BattleViewModel
import kotlin.collections.get
import kotlin.math.roundToInt

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

    BattleLayout(viewModel, finalCardHeight, finalCardWidth)

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
      val iconSize = 48.dp
      val density = LocalDensity.current
      val iconSizePx = with(density) { iconSize.toPx() }

      Box(
        modifier = Modifier
          .offset {
            IntOffset(
              x = (ultState.current.x - iconSizePx / 2).roundToInt(),
              y = (ultState.current.y - iconSizePx / 2).roundToInt()
            )
          }
          .size(iconSize)
          .clip(CircleShape)
          .background(Color.Red.copy(alpha = 0.8f)) // Maintained Red background
          .border(2.dp, Color.Red, CircleShape), // CHANGED: Yellow -> Red border
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = R.drawable.ultimate),
          contentDescription = "Dragging Ultimate",
          tint = Color.Black, // CHANGED: Yellow -> Black
          modifier = Modifier.size(28.dp)
        )
      }
    }

    // --- UI Overlays ---
    if (viewModel.showInfoDialog && viewModel.selectedEntity != null) {
      InfoCard(viewModel.selectedEntity!!)
    }

    if (viewModel.winner != null) {
      Winner(viewModel)
    }
  }
}

@Composable
fun BattleLayout(
  viewModel: BattleViewModel,
  finalCardHeight: Dp,
  finalCardWidth: Dp
) {
  Row(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 40.dp, vertical = 15.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {


    Row(
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

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

      RageBar(
        rage = viewModel.leftTeam.rage,
        maxRage = viewModel.leftTeam.maxRage,
        isTurn = viewModel.isLeftTeamTurn,
        isDragging = viewModel.ultimateDragState?.team == viewModel.leftTeam, // CHANGED: Pass drag state
        onDragStart = { offset -> viewModel.onUltimateDragStart(viewModel.leftTeam, offset) },
        onDrag = viewModel::onUltimateDrag,
        onDragEnd = viewModel::onUltimateDragEnd
      )

    }

    Text(
      text = "VS",
      color = Color.Gray,
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.alpha(0.5f)
    )

    Row(
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      RageBar(
        rage = viewModel.rightTeam.rage,
        maxRage = viewModel.rightTeam.maxRage,
        isTurn = !viewModel.isLeftTeamTurn,
        isDragging = viewModel.ultimateDragState?.team == viewModel.rightTeam, // CHANGED: Pass drag state
        onDragStart = { offset -> viewModel.onUltimateDragStart(viewModel.rightTeam, offset) },
        onDrag = viewModel::onUltimateDrag,
        onDragEnd = viewModel::onUltimateDragEnd
      )

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
}

@Composable
fun Winner(viewModel: BattleViewModel) {
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
  isDragging: Boolean, // CHANGED: New parameter
  onDragStart: (Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit
) {
  val barWidth = 200.dp
  val barHeight = 24.dp
  val progress = (rage / maxRage).coerceIn(0f, 1f)
  val isFull = progress >= 1f

  // Global position state for accurate drag start
  var iconCenterGlobal by remember { mutableStateOf(Offset.Zero) }

  Box(
    contentAlignment = Alignment.CenterStart,
    modifier = Modifier
      .width(barWidth)
      .height(barHeight)
  ) {
    // 1. Background Track (Clipped)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(12.dp))
        .background(Color.DarkGray)
        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
    ) {
      // 2. Filled Progress (Horizontal)
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(progress)
          .background(
            brush = Brush.horizontalGradient(
              colors = listOf(Color(0xFFFF5722), Color(0xFFFFC107))
            )
          )
      )
    }

    // 3. Static Anchor Icon (Draggable Source)
    if (isFull && isTurn) {
      Box(
        modifier = Modifier
          .align(Alignment.CenterEnd)
          .offset(x = 12.dp) // Pop out slightly to the right
          .size(48.dp) // Touch target
          .onGloballyPositioned { coordinates ->
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
                // Start drag from this icon's global center
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
        // Visual Circle
        if (!isDragging) { // CHANGED: Hide visual when dragging
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color.Red)
              .border(2.dp, Color.Red, CircleShape), // CHANGED: Yellow -> Red
            contentAlignment = Alignment.Center
          ) {
            Icon(
              painter = painterResource(id = R.drawable.ultimate),
              contentDescription = "Ready Ultimate",
              tint = Color.Black, // CHANGED: Yellow -> Black
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }
  }
}