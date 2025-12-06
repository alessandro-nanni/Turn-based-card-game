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

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // set horizontal layout
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    // enable Edge-to-Edge (draw behind bars)
    enableEdgeToEdge()

    // display app in camera cutout are
    window.attributes.layoutInDisplayCutoutMode =
      WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

    // hide System Bars & Set "Immersive Sticky" Mode
    val windowInsetsController =
      WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    // Hide both status bar and navigation bar
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    val mainView = MainView()

    setContent {
      mainView.Content()
    }
  }
}