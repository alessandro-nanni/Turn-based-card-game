package com.aln.cardturngame.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aln.cardturngame.R

@Composable
fun RageBar(
  rage: Float,
  maxRage: Float,
  isTurn: Boolean,
  isDragging: Boolean,
  onDragStart: (Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit,
  modifier: Modifier = Modifier
) {
  val barWidth = 24.dp
  val progress = (rage / maxRage).coerceIn(0f, 1f)
  val isFull = progress >= 1f
  val shape = RoundedCornerShape(12.dp)

  var iconCenterGlobal by remember { mutableStateOf(Offset.Zero) }

  Box(
    contentAlignment = Alignment.BottomCenter,
    modifier = modifier
      .width(barWidth)
      .clip(shape)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.DarkGray)
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(progress)
        .align(Alignment.BottomCenter)
        .background(
          brush = Brush.linearGradient(
            0.0f to Color(0xFFFF5722),
            0.5f to Color(0xFFFF5722),
            0.5f to Color(0xFFFF0741),
            1.0f to Color(0xFFFF0741),
            start = Offset.Zero,
            end = Offset(40f, 40f),
            tileMode = TileMode.Repeated
          )
        )
    )

    if (isFull && isTurn) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .size(48.dp)
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
        if (!isDragging) {
          Icon(
            painter = painterResource(id = R.drawable.ultimate),
            contentDescription = "Ready Ultimate",
            tint = Color.Black,
            modifier = Modifier.size(30.dp)
          )
        }
      }
    }
  }
}
