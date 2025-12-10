package com.aln.cardturngame.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.aln.cardturngame.entityFeatures.Ability
import kotlinx.coroutines.delay


@Composable
fun CharacterAbility(context: Context, label: String, ability: Ability, color: Color) {
  var popupControl by remember { mutableStateOf<Pair<String, Offset>?>(null) }

  LaunchedEffect(popupControl) {
    if (popupControl != null) {
      delay(2000)
      popupControl = null
    }
  }

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
        val pattern = "\\[\\[(.*?)\\|(.*?)\\|(.*?)]]".toRegex(RegexOption.DOT_MATCHES_ALL)
        var lastIndex = 0
        pattern.findAll(description).forEach { match ->
          append(description.substring(lastIndex, match.range.first))

          val effectName = match.groupValues[1]
          val effectDesc = match.groupValues[2]
          val isPositive = match.groupValues[3].toBoolean()

          val effectColor = if (isPositive) Color(0xFF00D471) else Color(0xFFBD3BF5)

          pushStringAnnotation(tag = "DESC", annotation = effectDesc)
          withStyle(SpanStyle(color = effectColor, fontWeight = FontWeight.Bold)) {
            append(effectName)
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
              .widthIn(min = 280.dp, max = 400.dp)
              .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          ) {
            Text(
              text = desc,
              color = Color.White,
              fontSize = 12.sp,
              modifier = Modifier.padding(12.dp)
            )
          }
        }
      }
    }
  }
}