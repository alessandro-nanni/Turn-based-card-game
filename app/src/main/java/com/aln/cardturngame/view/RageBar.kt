package com.aln.cardturngame.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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

  val infiniteTransition = rememberInfiniteTransition(label = "effects")

  val lavaOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 300f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "lava"
  )

  val barBrush = if (isFull) {
    Brush.linearGradient(
      colors = listOf(
        Color(0xFFFF5722),
        Color(0xFFE91E63),
        Color(0xFFFFC107),
        Color(0xFFFF5722)
      ),
      start = Offset(0f, lavaOffset),
      end = Offset(0f, lavaOffset + 400f),
      tileMode = TileMode.Mirror
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(Color(0xFFFF5722), Color(0xFFFF0741))
    )
  }

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
        .background(brush = barBrush)
    )

    if (isFull && isTurn) {
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .size(64.dp)
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
              onDragStart = { _ -> onDragStart(iconCenterGlobal) },
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
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(56.dp)
          )
        }
      }
    }
  }
}