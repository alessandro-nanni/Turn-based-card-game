package com.aln.cardturngame.ui

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.StatusEffect
import com.aln.cardturngame.entity.Popup
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
            .clip(CircleShape)
            .background(if (viewModel.isAlive) viewModel.color else Color.Gray)
            .border(2.dp, Color.White, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = viewModel.name.first().toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
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
    // Health
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

    // Vertical Separator
    VerticalDivider(
      modifier = Modifier
        .padding(horizontal = 12.dp)
        .fillMaxHeight(0.6f),
      color = Color.Gray.copy(alpha = 0.5f),
      thickness = 1.dp
    )

    // Damage
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
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.fillMaxWidth()
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

@Composable
fun InfoCard(viewModel: EntityViewModel) {
  val hasEffects = viewModel.statusEffects.isNotEmpty()
  // Adjust width: wider if showing effects column
  val cardWidth = if (hasEffects) 600.dp else 420.dp

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.6f))
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {},
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .width(cardWidth)
        .padding(16.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
      elevation = CardDefaults.cardElevation(8.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start
      ) {
        // Header Row: Name on Left, Stats on Right
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = viewModel.name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )
          StatsView(viewModel)
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(12.dp))

        // Content Row: 2 or 3 Columns
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // COL 1: Abilities
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(end = 8.dp)
          ) {
            CompactAbility(
              "Active",
              viewModel.entity.activeAbility.nameRes,
              viewModel.entity.activeAbility.descriptionRes,
              Color(0xFF66BB6A)
            )
            CompactAbility(
              "Passive",
              viewModel.entity.passiveAbility.nameRes,
              viewModel.entity.passiveAbility.descriptionRes,
              Color(0xFF42A5F5)
            )
            CompactAbility(
              "Ultimate",
              viewModel.entity.ultimateAbility.nameRes,
              viewModel.entity.ultimateAbility.descriptionRes,
              Color(0xFFAB47BC)
            )
          }

          // Separator 1
          VerticalDivider(
            modifier = Modifier
              .fillMaxHeight()
              .padding(vertical = 4.dp),
            color = Color.Gray.copy(alpha = 0.2f),
            thickness = 1.dp
          )

          // COL 2: Traits
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp)
          ) {
            Text(
              text = "Traits",
              color = Color.Gray,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(bottom = 6.dp)
            )
            if (viewModel.traits.isNotEmpty()) {
              viewModel.traits.forEach { trait ->
                CompactTrait(trait.nameRes, trait.descriptionRes)
              }
            } else {
              Text(
                text = "None",
                color = Color.DarkGray,
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
              )
            }
          }

          // COL 3: Effects (Conditional)
          if (hasEffects) {
            // Separator 2
            VerticalDivider(
              modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 4.dp),
              color = Color.Gray.copy(alpha = 0.2f),
              thickness = 1.dp
            )

            Column(
              modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
            ) {
              Text(
                text = "Effects",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
              )
              viewModel.statusEffects.forEach { effect ->
                CompactEffect(effect)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CompactAbility(label: String, nameRes: Int, descRes: Int, color: Color) {
  Column(modifier = Modifier.padding(bottom = 12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = label,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = stringResource(nameRes),
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )
    }
    Text(
      text = stringResource(descRes),
      color = Color.LightGray,
      fontSize = 11.sp,
      lineHeight = 14.sp,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}

@Composable
fun CompactTrait(nameRes: Int, descRes: Int) {
  Column(modifier = Modifier.padding(bottom = 8.dp)) {
    Text(
      text = "• ${stringResource(nameRes)}",
      color = Color(0xFFFF9800),
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = stringResource(descRes),
      color = Color.LightGray,
      fontSize = 11.sp,
      lineHeight = 13.sp,
      modifier = Modifier.padding(start = 8.dp)
    )
  }
}

@Composable
fun CompactEffect(effect: StatusEffect) {
  Column(modifier = Modifier.padding(bottom = 8.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        painter = painterResource(id = effect.iconRes),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = stringResource(effect.nameRes),
        color = Color(0xFFFFC107),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "x${effect.duration}",
        color = Color.Gray,
        fontSize = 10.sp
      )
    }
    Text(
      text = stringResource(effect.descriptionRes),
      color = Color.LightGray,
      fontSize = 11.sp,
      lineHeight = 13.sp,
      modifier = Modifier.padding(start = 22.dp)
    )
  }
}