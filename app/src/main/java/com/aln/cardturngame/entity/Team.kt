package com.aln.cardturngame.entity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.aln.cardturngame.ui.CharacterCard
import com.aln.cardturngame.viewModel.EntityViewModel

class Team(
  val name: String,
  val entities: List<EntityViewModel>
) {
  init {
    entities.forEach { it.entity.team = this }
  }
  var rage by mutableFloatStateOf(0f)
  val maxRage = 100f

  @Composable
  fun TeamColumn(
    alignment: Alignment.Horizontal,
    cardWidth: Dp,
    cardHeight: Dp,
    canAct: (EntityViewModel) -> Boolean,
    onCardPositioned: (EntityViewModel, Rect) -> Unit,
    onDragStart: (EntityViewModel, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: (EntityViewModel) -> Unit,
    onPressStatus: (EntityViewModel, Boolean) -> Unit,
    getHighlightColor: (EntityViewModel) -> Color
  ) {
    Column(
      modifier = Modifier.fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceEvenly,
      horizontalAlignment = alignment
    ) {
      entities.forEach { entityVM ->
        CharacterCard(
          viewModel = entityVM,
          width = cardWidth,
          height = cardHeight,
          canAct = canAct(entityVM),
          onCardPositioned = onCardPositioned,
          onDragStart = onDragStart,
          onDrag = onDrag,
          onDragEnd = onDragEnd,
          onDoubleTap = onDoubleTap,
          onPressStatus = onPressStatus,
          highlightColor = getHighlightColor(entityVM)
        )
      }
    }
  }
}