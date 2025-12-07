package com.aln.cardturngame.entity

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.launch

abstract class Entity(
  val name: String,
  val stats: Stats,
  val color: Color,
  val passiveAbility: Ability,
  val activeAbility: Ability
) {

  init {
    stats.entity = this
  }

  val statusEffects = mutableStateListOf<StatusEffect>()

  fun addStatusEffect(effect: StatusEffect) {
    val existingEffect = statusEffects.find { it::class == effect::class }

    if (existingEffect != null) {
      // update the duration to the new one
      existingEffect.duration = effect.duration
    } else {
      // add to list and trigger onApply
      statusEffects.add(effect)
      effect.onApply(this)
    }
  }

  fun removeStatusEffect(effect: StatusEffect) {
    effect.onVanish(this)
    statusEffects.remove(effect)
  }

  // State for popups
  val popups = mutableStateListOf<Popup>()
  private var popupIdCounter = 0L

  fun addPopup(amount: Float, color: Color = Color.Red) {
    val id = popupIdCounter++
    popups.add(Popup(id, amount.toInt(), color))
  }

  val isAlive: Boolean
    get() = stats.health > 0

  @Composable
  fun CharacterCard(
    width: Dp,
    height: Dp,
    canAct: Boolean,
    onCardPositioned: (Entity, Rect) -> Unit,
    onDragStart: (Entity, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: (Entity) -> Unit,
    onPressStatus: (Entity, Boolean) -> Unit,
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
            onCardPositioned(this@Entity, coordinates.boundsInRoot())
          }
          .then(
            if (canAct) Modifier.border(2.dp, Color.White, cardShape)
            else Modifier
          )
          .pointerInput(Unit) {
            detectTapGestures(
              onDoubleTap = {
                if (this@Entity.isAlive) {
                  onDoubleTap(this@Entity)
                }
              },
              onLongPress = {
                onPressStatus(this@Entity, true)
              },
              onPress = {
                tryAwaitRelease()
                onPressStatus(this@Entity, false)
              }
            )
          }
          .pointerInput(Unit) {
            detectDragGestures(
              onDragStart = { offset ->
                onDragStart(this@Entity, offset)
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
              .background(if (this@Entity.isAlive) this@Entity.color else Color.Gray)
              .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = this@Entity.name.first().toString(),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Stats()

          if (this@Entity.statusEffects.isNotEmpty()) {
            ActiveEffects()
          }

        }
      }


      popups.forEach { popup ->
        key(popup.id) {
          PopupView(popup) {
            popups.remove(popup)
          }
        }
      }
    }
  }

  @Composable
  fun Stats(){
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      val hp = this@Entity.stats.health
      val hpPercent = (hp / this@Entity.stats.maxHealth).coerceIn(0f, 1f)

      val barColor = when {
        hpPercent > 0.5f -> Color(0xFF4CAF50)
        hpPercent > 0.2f -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
      }

      // HP Bar
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
  fun ActiveEffects(){
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      this@Entity.statusEffects.forEach { effect ->
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
  fun InfoCard() {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.7f))
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null
        ) {},
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth(0.7f)
          .fillMaxHeight()
          .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.Start
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              horizontalArrangement = Arrangement.Start
            ) {
              Text(
                text = this@Entity.name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
              )
              if (!this@Entity.isAlive) {
                Icon(
                  painter = painterResource(id = R.drawable.dead),
                  contentDescription = "Damage",
                  tint = Color.White,
                  modifier = Modifier.size(28.dp)
                )
              }
            }
            this@Entity.stats.StatsView()
          }
          Spacer(modifier = Modifier.height(16.dp))

          // Active Ability
          Text(
            text = "Active: ${stringResource(this@Entity.activeAbility.nameRes)}",
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Bold
          )
          Text(
            text = stringResource(this@Entity.activeAbility.descriptionRes),
            color = Color.LightGray
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Passive Ability
          Text(
            text = "Passive: ${stringResource(this@Entity.passiveAbility.nameRes)}",
            color = Color(0xFF2196F3),
            fontWeight = FontWeight.Bold
          )
          Text(
            text = stringResource(this@Entity.passiveAbility.descriptionRes),
            color = Color.LightGray
          )

          if (statusEffects.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "Status Effects:",
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            statusEffects.forEach { effect ->
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 8.dp)
              ) {
                // Icon
                Icon(
                  painter = painterResource(id = effect.iconRes),
                  contentDescription = null,
                  tint = Color.Unspecified,
                  modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = stringResource(effect.nameRes),
                      color = Color(0xFFFFC107),
                      fontWeight = FontWeight.Bold,
                      fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "(${effect.duration} turns)",
                      color = Color.Gray,
                      fontSize = 12.sp
                    )
                  }

                  Text(
                    text = stringResource(effect.descriptionRes),
                    color = Color.LightGray,
                    fontSize = 14.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}