package com.aln.cardturngame

import androidx.compose.runtime.Composable
import com.aln.cardturngame.entity.Entity
import com.aln.cardturngame.entity.Team
import com.aln.cardturngame.entity.Warrior

class MainView(
  val battleground: Battleground =
    Battleground(
      leftTeam = Team(listOf<Entity>(Warrior(),Warrior(),Warrior())),
      rightTeam = Team(listOf<Entity>(Warrior(),Warrior()))
    )
) {
  @Composable
  fun Content() {
    battleground.BattleScreen()
  }
}