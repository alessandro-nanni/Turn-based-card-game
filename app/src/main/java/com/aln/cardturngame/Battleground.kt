package com.aln.cardturngame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp

data class GameCharacter(
  val name: String,
  val hp: Int,
  val color: Color
)

@Composable
fun BattleScreen() {
  // Mock Data
  val teamPlayer = listOf(
    GameCharacter("Warrior", 30, Color(0xFF4CAF50)),
    GameCharacter("Mage", 80, Color(0xFF2196F3)),
    GameCharacter("Rogue", 90, Color(0xFFFFC107))
  )

  val teamEnemy = listOf(
    GameCharacter("Orc", 10, Color(0xFFF44336)),
    GameCharacter("Goblin", 60, Color(0xFFE91E63))
  )

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

    BattleLayout(finalCardHeight, finalCardWidth, teamPlayer, teamEnemy)

  }
}

@Composable
fun BattleLayout(
  finalCardHeight: Dp,
  finalCardWidth: Dp,
  teamPlayer: List<GameCharacter>,
  teamEnemy: List<GameCharacter>
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
      cardHeight = finalCardHeight
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
      cardHeight = finalCardHeight
    )
  }
}

@Composable
fun TeamColumn(
  characters: List<GameCharacter>,
  alignment: Alignment.Horizontal,
  cardWidth: Dp,
  cardHeight: Dp
) {
  Column(
    modifier = Modifier.fillMaxHeight(),
    verticalArrangement = Arrangement.SpaceEvenly,
    horizontalAlignment = alignment
  ) {
    characters.forEach { character ->
      CharacterCard(
        character = character,
        width = cardWidth,
        height = cardHeight
      )
    }

  }
}

@Composable
fun CharacterCard(
  character: GameCharacter,
  width: Dp,
  height: Dp
) {

  Card(
    modifier = Modifier
      .width(width)
      .height(height),
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
          .background(character.color)
          .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = character.name.first().toString(),
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

        val hpPercent = (character.hp / 100f).coerceIn(0f, 1f)

        val barColor = when {
          character.hp > 50 -> Color(0xFF4CAF50)
          character.hp > 20 -> Color(0xFFFFC107)
          else -> Color(0xFFF44336)
        }

        // 3. The Progress Bar UI
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