package com.aln.cardturngame.effect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.aln.cardturngame.entity.Entity

abstract class StatusEffect(
  @field:StringRes val nameRes: Int,
  @field:StringRes val descriptionRes: Int,
  @field:DrawableRes val iconRes: Int,
  initialDuration: Int
) {
  var duration by mutableIntStateOf(initialDuration)

  open fun onApply(target: Entity) {}
  open suspend fun onStartTurn(target: Entity) {}
  open fun onVanish(target: Entity) {}

  fun tick(): Boolean {
    duration--
    return duration <= 0
  }
}