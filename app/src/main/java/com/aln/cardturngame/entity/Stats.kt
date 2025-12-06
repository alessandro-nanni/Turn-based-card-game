package com.aln.cardturngame.entity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue

class Stats(
  health: Float,
  maxHealth: Float = health,
  damage: Float
) {
  var health by mutableFloatStateOf(health)
  var maxHealth by mutableFloatStateOf(maxHealth)
  var damage by mutableFloatStateOf(damage)
}