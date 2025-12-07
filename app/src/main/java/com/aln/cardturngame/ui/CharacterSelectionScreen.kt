package com.aln.cardturngame.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.entity.Entity
import com.aln.cardturngame.viewModel.EntityViewModel
import kotlin.reflect.full.createInstance

@Composable
fun CharacterSelectionScreen(
  onStartGame: (String, List<Entity>, String, List<Entity>) -> Unit
) {
  var player1Name by remember { mutableStateOf("Player 1") }
  var player2Name by remember { mutableStateOf("Player 2") }

  val p1Team = remember { mutableStateListOf<Entity>() }
  val p2Team = remember { mutableStateListOf<Entity>() }

  // Updated reflection logic
  val availableCharacters = remember {
    Entity::class.sealedSubclasses.map { it.createInstance() }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF121212))
      .padding(16.dp)
  ) {
    // Top Row: P1 Name - Start Button - P2 Name
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Player 1 Name Input
      OutlinedTextField(
        value = player1Name,
        onValueChange = { player1Name = it },
        label = { Text("Player 1 Name", color = Color.Gray) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = Color(0xFFD32F2F),
          unfocusedBorderColor = Color.Gray,
          cursorColor = Color(0xFFD32F2F),
          focusedLabelColor = Color(0xFFD32F2F)
        ),
        modifier = Modifier.weight(1f)
      )

      // Start Game Button (Centered)
      Button(
        onClick = {
          onStartGame(player1Name, p1Team.toList(), player2Name, p2Team.toList())
        },
        enabled = p1Team.isNotEmpty() && p2Team.isNotEmpty(),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF4CAF50),
          disabledContainerColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(56.dp)
      ) {
        Text(
          text = "START",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = if (p1Team.isNotEmpty() && p2Team.isNotEmpty()) Color.White else Color.Gray
        )
      }

      // Player 2 Name Input
      OutlinedTextField(
        value = player2Name,
        onValueChange = { player2Name = it },
        label = { Text("Player 2 Name", color = Color.Gray) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White,
          focusedBorderColor = Color(0xFF2FC0D3),
          unfocusedBorderColor = Color.Gray,
          cursorColor = Color(0xFF2FC0D3),
          focusedLabelColor = Color(0xFF2FC0D3)
        ),
        modifier = Modifier.weight(1f)
      )
    }

    // Split Screen Selection Area
    Row(
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Player 1 Grid
      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p1Team,
          available = availableCharacters,
          color = Color(0xFFD32F2F)
        )
      }

      // Vertical Divider
      VerticalDivider(
        modifier = Modifier.fillMaxHeight(),
        color = Color.Gray.copy(alpha = 0.3f),
        thickness = 1.dp
      )

      // Player 2 Grid
      Box(modifier = Modifier.weight(1f)) {
        PlayerGridSection(
          team = p2Team,
          available = availableCharacters,
          color = Color(0xFF2FC0D3)
        )
      }
    }
  }
}

@Composable
fun PlayerGridSection(
  team: MutableList<Entity>,
  available: List<Entity>,
  color: Color
) {
  var infoCharacter by remember { mutableStateOf<Entity?>(null) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    // Grid View
    AnimatedVisibility(
      visible = infoCharacter == null,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(available) { entity ->
          val isSelected = team.any { it::class == entity::class }

          CharacterGridItem(
            entity = entity,
            isSelected = isSelected,
            activeColor = color,
            onSelect = {
              val existing = team.find { it::class == entity::class }
              if (existing != null) {
                team.remove(existing)
              } else {
                // Reflectively create a new instance for the actual team member
                val newInstance = entity::class.createInstance()
                team.add(newInstance)
              }
            },
            onInfo = { infoCharacter = entity }
          )
        }
      }
    }

    // Info Card Overlay
    AnimatedVisibility(
      visible = infoCharacter != null,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      infoCharacter?.let { entity ->
        Box(modifier = Modifier.fillMaxSize()) {
          val tempViewModel = remember(entity) { EntityViewModel(entity) }

          InfoCard(viewModel = tempViewModel)

          // Close Button
          IconButton(
            onClick = { infoCharacter = null },
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(16.dp)
              .background(Color.Black.copy(alpha = 0.5f), CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Info",
              tint = Color.White
            )
          }
        }
      }
    }
  }
}

@Composable
fun CharacterGridItem(
  entity: Entity,
  isSelected: Boolean,
  activeColor: Color,
  onSelect: () -> Unit,
  onInfo: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
    border = if (isSelected) BorderStroke(2.dp, activeColor) else null,
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(0.85f)
      .clickable { onSelect() }
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(entity.color),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = entity.name.first().toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = entity.name,
          color = if (isSelected) Color.White else Color.Gray,
          fontSize = 12.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          maxLines = 1
        )
      }

      // Info Button
      IconButton(
        onClick = onInfo,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .size(28.dp)
          .padding(2.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Info",
          tint = Color.Gray,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
