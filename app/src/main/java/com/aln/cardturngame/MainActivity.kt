package com.aln.cardturngame

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.aln.cardturngame.entity.Mage
import com.aln.cardturngame.entity.Team
import com.aln.cardturngame.entity.Warrior
import com.aln.cardturngame.ui.theme.CardTurnGameTheme
import com.aln.cardturngame.viewModel.BattleViewModel
import com.aln.cardturngame.viewModel.BattleViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // set horizontal layout
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    enableEdgeToEdge()

    window.attributes.layoutInDisplayCutoutMode =
      WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

    val windowInsetsController =
      WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    // Initialize Teams
    val leftTeam = Team(listOf(Mage()))
    val rightTeam = Team(listOf(Warrior(), Warrior(), Mage()))

    // Initialize ViewModel
    val factory = BattleViewModelFactory(leftTeam, rightTeam)
    val battleViewModel = ViewModelProvider(this, factory)[BattleViewModel::class.java]

    val mainView = MainView(battleViewModel)

    setContent {
      CardTurnGameTheme {
        mainView.Content()
      }
    }
  }
}