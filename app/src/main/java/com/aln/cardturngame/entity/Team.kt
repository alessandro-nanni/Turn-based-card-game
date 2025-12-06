package com.aln.cardturngame.entity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp

class Team(
  val entities: List<Entity>
) {

  @Composable
  fun TeamColumn(
    alignment: Alignment.Horizontal,
    cardWidth: Dp,
    cardHeight: Dp,
    onCardPositioned: (Entity, Rect) -> Unit,
    onDragStart: (Entity, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: (Entity) -> Unit,
    onPressStatus: (Entity, Boolean) -> Unit
  ) {
    Column(
      modifier = Modifier.fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceEvenly,
      horizontalAlignment = alignment
    ) {
      entities.forEach { character ->
        character.CharacterCard(
          width = cardWidth,
          height = cardHeight,
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
}