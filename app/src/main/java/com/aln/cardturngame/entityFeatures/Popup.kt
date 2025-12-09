package com.aln.cardturngame.entityFeatures

import androidx.compose.ui.graphics.Color
data class Popup(
  val id: Long,
  val amount: Int,
  val color: Color = Color.Red,
  val xOffset: Float = 0f
)