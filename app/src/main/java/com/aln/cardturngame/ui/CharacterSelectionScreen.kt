package com.aln.cardturngame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aln.cardturngame.entity.Entity
import com.aln.cardturngame.entity.Mage
import com.aln.cardturngame.entity.Warrior
import com.aln.cardturngame.viewModel.EntityViewModel

@Composable
fun CharacterSelectionScreen(
    onStartGame: (String, List<Entity>, String, List<Entity>) -> Unit
) {
    var player1Name by remember { mutableStateOf("Player 1") }
    var player2Name by remember { mutableStateOf("Player 2") }
    
    val p1Team = remember { mutableStateListOf<Entity>() }
    val p2Team = remember { mutableStateListOf<Entity>() }

    // Use instances just for display in the grid
    val availableCharacters = remember { listOf(Warrior(), Mage()) }
    
    // State for Info Dialog
    var infoCharacter by remember { mutableStateOf<Entity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Player 1 Column
            Column(modifier = Modifier.weight(1f)) {
                PlayerSelectionSection(
                    playerName = player1Name,
                    onNameChange = { player1Name = it },
                    team = p1Team,
                    available = availableCharacters,
                    onInfoClick = { infoCharacter = it },
                    color = Color(0xFFD32F2F)
                )
            }

            // Vertical Divider
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                color = Color.Gray.copy(alpha = 0.5f)
            )

            // Player 2 Column
            Column(modifier = Modifier.weight(1f)) {
                PlayerSelectionSection(
                    playerName = player2Name,
                    onNameChange = { player2Name = it },
                    team = p2Team,
                    available = availableCharacters,
                    onInfoClick = { infoCharacter = it },
                    color = Color(0xFF2FC0D3)
                )
            }
        }

        // Start Button
        if (p1Team.isNotEmpty() && p2Team.isNotEmpty()) {
            Button(
                onClick = { 
                    onStartGame(player1Name, p1Team.toList(), player2Name, p2Team.toList()) 
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("START GAME", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Info Dialog
        if (infoCharacter != null) {
            Dialog(onDismissRequest = { infoCharacter = null }) {
                // Wrap in EntityViewModel for the existing InfoCard
                val tempViewModel = remember(infoCharacter) { EntityViewModel(infoCharacter!!) }
                Box(contentAlignment = Alignment.Center) {
                    InfoCard(tempViewModel)
                    // Close button for the dialog
                    IconButton(
                        onClick = { infoCharacter = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerSelectionSection(
    playerName: String,
    onNameChange: (String) -> Unit,
    team: MutableList<Entity>,
    available: List<Entity>,
    onInfoClick: (Entity) -> Unit,
    color: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Name Field
        OutlinedTextField(
            value = playerName,
            onValueChange = onNameChange,
            label = { Text("Player Name", color = Color.Gray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = color,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selected Team Display
        Text("Selected Team", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (team.isEmpty()) {
                Text("Select characters below", color = Color.Gray, fontSize = 12.sp)
            } else {
                team.forEach { entity ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(entity.color)
                            .clickable { team.remove(entity) }, // Click to remove
                        contentAlignment = Alignment.Center
                    ) {
                         Text(
                            text = entity.name.first().toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Available Characters Grid
        Text("Available Characters", color = Color.White, fontWeight = FontWeight.Bold)
        Text("(No duplicates allowed)", color = Color.Gray, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(available) { entity ->
                val isSelected = team.any { it::class == entity::class }
                
                CharacterGridItem(
                    entity = entity,
                    isSelected = isSelected,
                    onSelect = { 
                        // Create a NEW instance when adding to team
                        val newInstance = when(entity) {
                            is Warrior -> Warrior()
                            is Mage -> Mage()
                            else -> throw IllegalStateException("Unknown entity type")
                        }
                        team.add(newInstance) 
                    },
                    onInfo = { onInfoClick(entity) }
                )
            }
        }
    }
}

@Composable
fun CharacterGridItem(
    entity: Entity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onInfo: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .alpha(if (isSelected) 0.5f else 1f)
            .clickable(enabled = !isSelected) { onSelect() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(entity.name, color = Color.White, fontSize = 14.sp)
            }

            // Info Button
            IconButton(
                onClick = onInfo,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}