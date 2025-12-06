package com.aln.cardturngame

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class MainView {
  @Composable
  fun Content(modifier: Modifier = Modifier){
    Text(
      text = "Hello asdru!",
      modifier = modifier
    )
  }
}