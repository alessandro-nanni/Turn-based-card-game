package com.aln.cardturngame.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.BurningEffect
import com.aln.cardturngame.effect.StatusEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.trait.Trait
import com.aln.cardturngame.viewModel.EntityViewModel


@Composable
fun CharacterInfoCard(viewModel: EntityViewModel, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  Box(
    modifier = modifier
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
        .widthIn(max = 600.dp)
        .fillMaxWidth()
        .padding(16.dp),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
      elevation = CardDefaults.cardElevation(8.dp)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {

          DamageTypeChip(viewModel.damageType)

          Text(
            text = stringResource(viewModel.name),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
          )
          StatsView(viewModel)

        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.Gray.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(
            modifier = Modifier
              .weight(1f)
              .padding(end = 8.dp)
              .heightIn(max = 250.dp)
              .verticalScroll(rememberScrollState())
          ) {
            Ability(
              context = context,
              label = stringResource(R.string.ui_active),
              ability = viewModel.entity.activeAbility,
              color = Color(0xFF66BB6A),
            )
            Ability(
              context = context,
              label = stringResource(R.string.ui_passive),
              ability = viewModel.entity.passiveAbility,
              color = Color(0xFF42A5F5)
            )
            Ability(
              context = context,
              label = stringResource(R.string.ui_ultimate),
              ability = viewModel.entity.ultimateAbility,
              color = Color(0xFFE91E63)
            )
          }

          val hasEffects = viewModel.statusEffects.isNotEmpty()

          if (viewModel.traits.isNotEmpty()) {
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
                .padding(horizontal = 8.dp)
                .heightIn(max = 250.dp)
                .verticalScroll(rememberScrollState())
            ) {
              Text(
                text = stringResource(R.string.ui_traits),
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
              )
              viewModel.traits.forEach {
                Trait(it, context = context)
              }
            }
          }

          if (hasEffects) {
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
                .heightIn(max = 250.dp)
                .verticalScroll(rememberScrollState())
            ) {
              Text(
                text = stringResource(R.string.ui_effects),
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
              )
              viewModel.statusEffects.forEach {
                Effect(it, context)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun DamageTypeChip(
  damageType: DamageType,
) {
  Icon(
    painter = painterResource(damageType.iconResId),
    contentDescription = damageType.name,
    tint = damageType.tintColor,
    modifier = Modifier.size(20.dp)
  )
}

@Composable
fun Ability(context: Context, label: String, ability: Ability, color: Color) {
  var popupControl by remember { mutableStateOf<Pair<String, Offset>?>(null) }

  Column(modifier = Modifier.padding(bottom = 12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = label,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 12.sp,
        style = TextStyle(
          platformStyle = PlatformTextStyle(
            includeFontPadding = false
          )
        ),
        modifier = Modifier
          .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
          .padding(horizontal = 6.dp, vertical = 3.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = ability.getName(context),
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )
    }

    val description = ability.getDescription(context)
    val annotatedString = remember(description) {
      buildAnnotatedString {
        val pattern = "\\[\\[(.*?)\\|(.*?)]]".toRegex(RegexOption.DOT_MATCHES_ALL)
        var lastIndex = 0
        pattern.findAll(description).forEach { match ->
          append(description.substring(lastIndex, match.range.first))

          pushStringAnnotation(tag = "DESC", annotation = match.groupValues[2])
          withStyle(SpanStyle(color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)) {
            append(match.groupValues[1])
          }
          pop()

          lastIndex = match.range.last + 1
        }
        append(description.substring(lastIndex))
      }
    }

    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    Box {
      Text(
        text = annotatedString,
        color = Color.LightGray,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        modifier = Modifier
          .padding(top = 4.dp)
          .pointerInput(annotatedString) {
            detectTapGestures { pos ->
              layoutResult.value?.let { layout ->
                val offset = layout.getOffsetForPosition(pos)
                val annotation = annotatedString
                  .getStringAnnotations(tag = "DESC", start = offset, end = offset)
                  .firstOrNull()
                if (annotation != null) {
                  popupControl = annotation.item to pos
                }
              }
            }
          },
        onTextLayout = { layoutResult.value = it }
      )

      // Visualizzazione del Popup
      popupControl?.let { (desc, pos) ->
        Popup(
          alignment = Alignment.TopStart,
          offset = IntOffset(pos.x.toInt(), pos.y.toInt() + 20),
          onDismissRequest = { popupControl = null }
        ) {
          Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .widthIn(max = 200.dp)
              .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          ) {
            Text(
              text = desc,
              color = Color.White,
              fontSize = 12.sp,
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun Trait(trait: Trait, context: Context) {
  Column(modifier = Modifier.padding(bottom = 8.dp)) {
    Text(
      text = "• ${trait.getName(context)}",
      color = Color(0xFFFF9800),
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = trait.getDescription(context),
      color = Color.LightGray,
      fontSize = 11.sp,
      lineHeight = 13.sp,
      modifier = Modifier.padding(start = 8.dp)
    )
  }
}

@Composable
fun Effect(effect: StatusEffect, context: Context) {
  Column(
    modifier = Modifier.padding(bottom = 8.dp),
    horizontalAlignment = Alignment.Start
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        painter = painterResource(id = effect.iconRes),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = effect.getName(context),
        color = if (effect.isPositive) Color(0xFF00D471) else Color(0xFFBD3BF5),
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
      text = effect.getDescription(context),
      color = Color.LightGray,
      fontSize = 11.sp,
      lineHeight = 13.sp
    )
  }
}