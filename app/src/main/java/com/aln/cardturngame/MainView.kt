package com.aln.cardturngame

import androidx.compose.runtime.Composable
import com.aln.cardturngame.entity.Mage
import com.aln.cardturngame.entity.Team
import com.aln.cardturngame.entity.Warrior

class MainView(
  val battleground: Battleground =
    Battleground(
      leftTeam = Team(listOf(Mage(), Warrior(), Warrior())),
      rightTeam = Team(listOf(Warrior(), Warrior(), Mage()))
    )
) {
  @Composable
  fun Content() {
    battleground.BattleScreen()
  }
}