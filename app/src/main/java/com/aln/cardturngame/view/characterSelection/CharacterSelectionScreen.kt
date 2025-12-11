package com.aln.cardturngame.view.characterSelection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.R
import com.aln.cardturngame.entity.Entity
import kotlin.reflect.full.createInstance

@Composable
fun CharacterSelectionScreen(
  onStartGame: (String, List<Entity>, String, List<Entity>) -> Unit
) {
  var player1Name by remember { mutableStateOf("Player 1") }
  var player2Name by remember { mutableStateOf("Player 2") }
  var showRulesP1 by remember { mutableStateOf(false) }
  var showRulesP2 by remember { mutableStateOf(false) }

  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }

  val availableCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        OutlinedTextField(
          value = player1Name,
          onValueChange = { player1Name = it },
          label = { Text(stringResource(R.string.ui_player_name, 1), color = Color.Gray) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.White,
            focusedLabelColor = Color.White
          ),
          modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { showRulesP1 = true }) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Help,
            contentDescription = "How to play",
            tint = Color.White
          )
        }

        Button(
          onClick = {
            onStartGame(player1Name, p1Team.toList(), player2Name, p2Team.toList())
          },
          enabled = p1Team.size == 3 && p2Team.size == 3,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50),
            disabledContainerColor = Color.DarkGray
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(56.dp)
        ) {
          Text(
            text = stringResource(R.string.ui_start),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (p1Team.size == 3 && p2Team.size == 3) Color.White else Color.Gray
          )
        }

        IconButton(onClick = { showRulesP2 = true }) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Help,
            contentDescription = "How to play",
            tint = Color.White
          )
        }

        OutlinedTextField(
          value = player2Name,
          onValueChange = { player2Name = it },
          label = { Text(stringResource(R.string.ui_player_name, 2), color = Color.Gray) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color.White,
            focusedLabelColor = Color.White
          ),
          modifier = Modifier.weight(1f)
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p1Team,
          available = availableCharacters,
        )
        HowToPlayOverlay(
          visible = showRulesP1,
          onClose = { showRulesP1 = false }
        )
      }

      VerticalDivider(
        modifier = Modifier.fillMaxHeight(),
        color = Color.Gray.copy(alpha = 0.3f),
        thickness = 1.dp
      )

      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p2Team,
          available = availableCharacters,
        )
        HowToPlayOverlay(
          visible = showRulesP2,
          onClose = { showRulesP2 = false }
        )
      }
    }
  }
}
