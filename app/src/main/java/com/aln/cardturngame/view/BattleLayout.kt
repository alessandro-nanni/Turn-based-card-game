package com.aln.cardturngame.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.viewModel.BattleViewModel

@Composable
fun BattleLayout(
  viewModel: BattleViewModel,
  finalCardHeight: Dp,
  finalCardWidth: Dp
) {
  Row(
    modifier = Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {

    // Left Rage Bar
    RageBar(
      rage = viewModel.leftTeam.rage,
      maxRage = viewModel.leftTeam.maxRage,
      isTurn = viewModel.isLeftTeamTurn,
      isDragging = viewModel.ultimateDragState?.team == viewModel.leftTeam,
      onDragStart = { offset -> viewModel.onUltimateDragStart(viewModel.leftTeam, offset) },
      onDrag = viewModel::onUltimateDrag,
      onDragEnd = viewModel::onUltimateDragEnd,
      modifier = Modifier
        .fillMaxHeight()
        .padding(start = 40.dp, top = 24.dp, bottom = 24.dp)
    )

    Row(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
        .padding(horizontal = 16.dp, vertical = 15.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
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

      Text(
        text = "VS",
        color = Color.Gray,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.alpha(0.5f)
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

    // Right Rage Bar
    RageBar(
      rage = viewModel.rightTeam.rage,
      maxRage = viewModel.rightTeam.maxRage,
      isTurn = !viewModel.isLeftTeamTurn,
      isDragging = viewModel.ultimateDragState?.team == viewModel.rightTeam,
      onDragStart = { offset -> viewModel.onUltimateDragStart(viewModel.rightTeam, offset) },
      onDrag = viewModel::onUltimateDrag,
      onDragEnd = viewModel::onUltimateDragEnd,
      modifier = Modifier
        .fillMaxHeight()
        .padding(end = 40.dp, top = 24.dp, bottom = 24.dp)
    )
  }
}
