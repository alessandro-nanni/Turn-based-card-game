package com.aln.cardturngame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.aln.cardturngame.entity.Team
import com.aln.cardturngame.ui.InfoCard
import com.aln.cardturngame.viewModel.BattleViewModel
import com.aln.cardturngame.viewModel.EntityViewModel

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

    // Line Drawing
    viewModel.dragState?.let { dragState ->
      val lineEnd =
        if (viewModel.hoveredTarget != null && viewModel.cardBounds.contains(viewModel.hoveredTarget)) {
          viewModel.cardBounds[viewModel.hoveredTarget]!!.center
        } else {
          dragState.current
        }
      LineCanvas(dragState.start, lineEnd)
    }

    BattleLayout(
      finalCardHeight = finalCardHeight,
      finalCardWidth = finalCardWidth,
      leftTeam = viewModel.leftTeam,
      rightTeam = viewModel.rightTeam,
      canAct = viewModel::canEntityAct,
      onCardPositioned = viewModel::onCardPositioned,
      onDragStart = viewModel::onDragStart,
      onDrag = viewModel::onDrag,
      onDragEnd = viewModel::onDragEnd,
      onDoubleTap = viewModel::onDoubleTap,
      onPressStatus = viewModel::onPressStatus,
      getHighlightColor = viewModel::getHighlightColor
    )

    if (viewModel.showInfoDialog && viewModel.selectedEntity != null) {
      InfoCard(viewModel.selectedEntity!!)
    }

    // Winner Overlay
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

@Composable
fun BattleLayout(
  finalCardHeight: Dp,
  finalCardWidth: Dp,
  leftTeam: Team,
  rightTeam: Team,
  canAct: (EntityViewModel) -> Boolean,
  onCardPositioned: (EntityViewModel, Rect) -> Unit,
  onDragStart: (EntityViewModel, Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit,
  onDoubleTap: (EntityViewModel) -> Unit,
  onPressStatus: (EntityViewModel, Boolean) -> Unit,
  getHighlightColor: (EntityViewModel) -> Color
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