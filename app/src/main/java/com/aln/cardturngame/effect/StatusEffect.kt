package com.aln.cardturngame.effect

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

abstract class StatusEffect(
  @field:StringRes override val nameRes: Int,
  @field:StringRes override val descriptionRes: Int,
  @field:DrawableRes val iconRes: Int,
  initialDuration: Int,
  override val isPositive: Boolean,
  override val formatArgs: List<Any> = emptyList()
) : Translatable {
  var duration by mutableIntStateOf(initialDuration)
  var source: EntityViewModel? = null

  open fun onApply(target: EntityViewModel) {}
  open fun onVanish(target: EntityViewModel) {}

  open suspend fun onStartTurn(target: EntityViewModel) {}

  open fun modifyDamage(currentDamage: Float): Float = currentDamage

  open fun modifyIncomingDamage(owner: EntityViewModel, currentDamage: Float, source: EntityViewModel?): Float = currentDamage

  fun tick(): Boolean {
    duration--
    return duration <= 0
  }
}