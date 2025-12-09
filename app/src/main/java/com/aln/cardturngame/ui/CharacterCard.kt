package com.aln.cardturngame.ui

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Popup
import com.aln.cardturngame.viewModel.EntityViewModel
import kotlinx.coroutines.launch

@Composable
fun CharacterCard(
  viewModel: EntityViewModel,
  width: Dp,
  height: Dp,
  canAct: Boolean,
  onCardPositioned: (EntityViewModel, Rect) -> Unit,
  onDragStart: (EntityViewModel, Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit,
  onDoubleTap: (EntityViewModel) -> Unit,
  onPressStatus: (EntityViewModel, Boolean) -> Unit,
  highlightColor: Color = Color.Transparent
) {
  val cardShape = RoundedCornerShape(12.dp)

  val animatedOffset by animateOffsetAsState(
    targetValue = viewModel.attackAnimOffset ?: Offset.Zero,
    animationSpec = tween(200),
    label = "AttackAnimation"
  )

  val scaleX = remember { Animatable(1f) }
  val scaleY = remember { Animatable(1f) }

  LaunchedEffect(viewModel.hitAnimTrigger) {
    if (viewModel.hitAnimTrigger > 0) {
      launch {
        scaleX.animateTo(1.2f, tween(100))
        scaleX.animateTo(1f, tween(150))
      }
      launch {
        scaleY.animateTo(0.8f, tween(100))
        scaleY.animateTo(1f, tween(150))
      }
    }
  }

  Box(
    modifier = Modifier
      .width(width)
      .height(height)
      .then(
        if (highlightColor != Color.Transparent) {
          Modifier.drawBehind {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = highlightColor.toArgb()
            frameworkPaint.maskFilter = BlurMaskFilter(
              15.dp.toPx(),
              BlurMaskFilter.Blur.NORMAL
            )

            drawIntoCanvas { canvas ->
              canvas.drawOutline(
                outline = cardShape.createOutline(size, layoutDirection, this),
                paint = paint
              )
            }
          }
        } else Modifier
      )
  ) {
    Card(
      shape = cardShape,
      modifier = Modifier
        .fillMaxSize()
        // Apply animations via graphicsLayer
        .graphicsLayer {
          this.translationX = animatedOffset.x
          this.translationY = animatedOffset.y
          this.scaleX = scaleX.value
          this.scaleY = scaleY.value
        }
        .onGloballyPositioned { coordinates ->
          onCardPositioned(viewModel, coordinates.boundsInRoot())
        }
        .then(
          if (canAct) Modifier.border(2.dp, Color.White, cardShape)
          else Modifier
        )
        .pointerInput(Unit) {
          detectTapGestures(
            onDoubleTap = {
              if (viewModel.isAlive) {
                onDoubleTap(viewModel)
              }
            },
            onLongPress = {
              onPressStatus(viewModel, true)
            },
            onPress = {
              tryAwaitRelease()
              onPressStatus(viewModel, false)
            }
          )
        }
        .pointerInput(Unit) {
          detectDragGestures(
            onDragStart = { offset ->
              onDragStart(viewModel, offset)
            },
            onDrag = { change, dragAmount ->
              change.consume()
              onDrag(dragAmount)
            },
            onDragEnd = {
              onDragEnd()
            },
            onDragCancel = {
              onDragEnd()
            }
          )
        },
      colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
      elevation = CardDefaults.cardElevation(8.dp)
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
      ) {
        Spacer(modifier = Modifier.height(8.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth(0.75f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            painter = painterResource(viewModel.iconRes),
            tint = if (viewModel.isAlive) viewModel.color else Color.Gray,
            contentDescription = viewModel.name.toString(),
            modifier = Modifier
              .fillMaxSize()
              .padding(4.dp),
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        StatsBar(viewModel)

        if (viewModel.statusEffects.isNotEmpty()) {
          ActiveEffects(viewModel)
        }
      }
    }

    viewModel.popups.forEach { popup ->
      key(popup.id) {
        PopupView(popup) {
          viewModel.popups.remove(popup)
        }
      }
    }
  }
}

@Composable
fun StatsBar(viewModel: EntityViewModel) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
  ) {
    val hp = viewModel.health
    val hpPercent = (hp / viewModel.maxHealth).coerceIn(0f, 1f)

    val barColor = when {
      hpPercent > 0.5f -> Color(0xFF4CAF50)
      hpPercent > 0.2f -> Color(0xFFFFC107)
      else -> Color(0xFFF44336)
    }

    Box(
      modifier = Modifier
        .fillMaxWidth(0.8f)
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp))
        .background(Color.DarkGray)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(hpPercent)
          .fillMaxHeight()
          .background(barColor)
      )
    }
  }
}

@Composable
fun StatsView(viewModel: EntityViewModel) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.height(IntrinsicSize.Min)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        painter = painterResource(id = R.drawable.health),
        contentDescription = "Health",
        tint = Color(0xFFEF5350),
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "${viewModel.health.toInt()}/${viewModel.maxHealth.toInt()}",
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
    }

    VerticalDivider(
      modifier = Modifier
        .padding(horizontal = 12.dp)
        .fillMaxHeight(0.6f),
      color = Color.Gray.copy(alpha = 0.5f),
      thickness = 1.dp
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        painter = painterResource(id = R.drawable.attack_damage),
        contentDescription = "Damage",
        tint = Color(0xFFFFCA28),
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "${viewModel.damage.toInt()}",
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun ActiveEffects(viewModel: EntityViewModel) {
  Spacer(modifier = Modifier.height(4.dp))
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
  ) {
    viewModel.statusEffects.forEach { effect ->
      Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
          .padding(2.dp)
          .size(32.dp)
      ) {
        Icon(
          painter = painterResource(id = effect.iconRes),
          contentDescription = effect.nameRes.toString(),
          tint = Color.White,
          modifier = Modifier.fillMaxSize()
        )

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(14.dp)
            .clip(CircleShape)
            .background(Color.Black)
        ) {
          Text(
            text = effect.duration.toString(),
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 8.sp
          )
        }
      }
    }
  }
}

@Composable
fun PopupView(popup: Popup, onComplete: () -> Unit) {
  val offsetY = remember { Animatable(0f) }
  val alpha = remember { Animatable(1f) }

  LaunchedEffect(Unit) {
    launch {
      offsetY.animateTo(-50f, animationSpec = tween(1000))
    }
    launch {
      alpha.animateTo(0f, animationSpec = tween(800, delayMillis = 200))
      onComplete()
    }
  }

  val sign = if (popup.color == Color.Green) "+" else "-"

  Text(
    text = "$sign${popup.amount}",
    color = popup.color,
    fontSize = 28.sp,
    fontWeight = FontWeight.Bold,
    modifier = Modifier
      .offset(y = offsetY.value.dp)
      .alpha(alpha.value)
  )
}