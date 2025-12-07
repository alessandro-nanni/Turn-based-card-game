package com.aln.cardturngame.effect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.aln.cardturngame.viewModel.EntityViewModel

abstract class StatusEffect(
  @field:StringRes val nameRes: Int,
  @field:StringRes val descriptionRes: Int,
  @field:DrawableRes val iconRes: Int,
  initialDuration: Int,
  val isPositive: Boolean = false
) {
  var duration by mutableIntStateOf(initialDuration)

  open fun onApply(target: EntityViewModel) {}
  open suspend fun onStartTurn(target: EntityViewModel) {}
  open fun onVanish(target: EntityViewModel) {}

  fun tick(): Boolean {
    duration--
    return duration <= 0
  }
}