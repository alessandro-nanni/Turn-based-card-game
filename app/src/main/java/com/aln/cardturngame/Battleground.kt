package com.aln.cardturngame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.character.AbstractCharacter
import com.aln.cardturngame.character.Warrior

@Composable
fun BattleScreen() {

  val teamPlayer = remember {
    listOf<AbstractCharacter>(
      Warrior(), Warrior(), Warrior(),
    )
  }

  val teamEnemy = remember {
    listOf<AbstractCharacter>(
      Warrior(), Warrior(),
    )
  }

  // Drag & Drop State
  var draggingSource by remember { mutableStateOf<AbstractCharacter?>(null) }
  var dragStart by remember { mutableStateOf(Offset.Zero) }
  var dragCurrent by remember { mutableStateOf(Offset.Zero) }

  val cardBounds = remember { mutableStateMapOf<AbstractCharacter, Rect>() }

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF1E1E1E))
  ) {
    val heightLimit = this.maxHeight / 3.4f
    val widthLimit = this.maxWidth / 2.5f
    val aspectRatio = 0.7f
    val heightFromWidth = widthLimit / aspectRatio
    val finalCardHeight = min(heightLimit, heightFromWidth)
    val finalCardWidth = finalCardHeight * aspectRatio

    BattleLayout(
      finalCardHeight = finalCardHeight,
      finalCardWidth = finalCardWidth,
      teamPlayer = teamPlayer,
      teamEnemy = teamEnemy,
      onCardPositioned = { char, rect ->
        cardBounds[char] = rect
      },
      onDragStart = { char, offset ->
        draggingSource = char
        // Calculate global start position based on the card's top-left + touch offset
        val cardTopLeft = cardBounds[char]?.topLeft ?: Offset.Zero
        val globalStart = cardTopLeft + offset
        dragStart = globalStart
        dragCurrent = globalStart
      },
      onDrag = { change ->
        dragCurrent += change
      },
      onDragEnd = {
        draggingSource?.let { source ->
          // Find which card is under dragCurrent
          val target = cardBounds.entries.firstOrNull { (_, rect) ->
            rect.contains(dragCurrent)
          }?.key

          if (target != null && target != source) {
            handleCardInteraction(source, target, teamPlayer, teamEnemy)
          }
        }
        draggingSource = null
      }
    )

    if (draggingSource != null) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
          color = Color.Red,
          start = dragStart,
          end = dragCurrent,
          strokeWidth = 8f
        )
      }
    }
  }
}

fun handleCardInteraction(
  source: AbstractCharacter,
  target: AbstractCharacter,
  teamPlayer: List<AbstractCharacter>,
  teamEnemy: List<AbstractCharacter>
) {
  // Determine teams
  val sourceIsPlayer = teamPlayer.contains(source)
  val targetIsPlayer = teamPlayer.contains(target)

  // Check relationship
  if (sourceIsPlayer == targetIsPlayer) {
    source.passive(target)
  } else {
    source.active(target)

  }
}

@Composable
fun BattleLayout(
  finalCardHeight: Dp,
  finalCardWidth: Dp,
  teamPlayer: List<AbstractCharacter>,
  teamEnemy: List<AbstractCharacter>,
  onCardPositioned: (AbstractCharacter, Rect) -> Unit,
  onDragStart: (AbstractCharacter, Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit
) {

  Row(
    modifier = Modifier
      .fillMaxSize()
      .padding(start = 40.dp, end = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // LEFT TEAM
    TeamColumn(
      characters = teamPlayer,
      alignment = Alignment.Start,
      cardWidth = finalCardWidth,
      cardHeight = finalCardHeight,
      onCardPositioned = onCardPositioned,
      onDragStart = onDragStart,
      onDrag = onDrag,
      onDragEnd = onDragEnd
    )

    // VS Text
    Text(
      text = "VS",
      color = Color.Gray,
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.alpha(0.5f)
    )

    // RIGHT TEAM
    TeamColumn(
      characters = teamEnemy,
      alignment = Alignment.End,
      cardWidth = finalCardWidth,
      cardHeight = finalCardHeight,
      onCardPositioned = onCardPositioned,
      onDragStart = onDragStart,
      onDrag = onDrag,
      onDragEnd = onDragEnd
    )
  }
}

@Composable
fun TeamColumn(
  characters: List<AbstractCharacter>,
  alignment: Alignment.Horizontal,
  cardWidth: Dp,
  cardHeight: Dp,
  onCardPositioned: (AbstractCharacter, Rect) -> Unit,
  onDragStart: (AbstractCharacter, Offset) -> Unit,
  onDrag: (Offset) -> Unit,
  onDragEnd: () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxHeight(),
    verticalArrangement = Arrangement.SpaceEvenly,
    horizontalAlignment = alignment
  ) {
    characters.forEach { character ->
      character.CharacterCard(
        width = cardWidth,
        height = cardHeight,
        onCardPositioned = onCardPositioned,
        onDragStart = onDragStart,
        onDrag = onDrag,
        onDragEnd = onDragEnd
      )
    }
  }
}