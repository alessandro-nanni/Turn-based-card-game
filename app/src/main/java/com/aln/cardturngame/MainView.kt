package com.aln.cardturngame

import androidx.compose.runtime.Composable
import com.aln.cardturngame.ui.BattleScreen
import com.aln.cardturngame.viewModel.BattleViewModel

class MainView(
  private val battleViewModel: BattleViewModel
) {
  @Composable
  fun Content() {
    BattleScreen(battleViewModel)
  }
}