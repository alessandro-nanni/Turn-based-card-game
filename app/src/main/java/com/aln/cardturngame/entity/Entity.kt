package com.aln.cardturngame.entity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

abstract class Entity(
  val name: String,
  val stats: Stats,
  val color: Color,
  val passiveAbility: Ability,
  val activeAbility: Ability
) {

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
    onPressStatus: (Entity, Boolean) -> Unit
  ) {

    Card(
      modifier = Modifier
        .width(width)
        .height(height)
        .onGloballyPositioned { coordinates ->
          onCardPositioned(this, coordinates.boundsInRoot())
        }
        .then(
          if (canAct) Modifier.border(2.dp, Color.White, CardDefaults.shape)
          else Modifier
        )
        .combinedClickable(
          onClick = {
          },
          onDoubleClick = { onDoubleTap(this) },
          onLongClick = {
            onPressStatus(this, true)

          },
        )
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

        // Avatar
        Box(
          modifier = Modifier
            .fillMaxWidth(0.75f)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(this@Entity.color)
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

        // Stats Section
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
    }
  }

  @Composable
  fun InfoCard() {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.7f)),
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
            Text(
              text = this@Entity.name,
              color = Color.White,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
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
        }
      }
    }
  }
}