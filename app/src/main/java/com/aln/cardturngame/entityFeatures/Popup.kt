package com.aln.cardturngame.entityFeatures

import androidx.compose.ui.graphics.Color


data class Popup(
  val id: Long,
  val text: String = "",
  val textRes: Int? = null,
  val color: Color = Color.Red,
  val xOffset: Float = 0f
)