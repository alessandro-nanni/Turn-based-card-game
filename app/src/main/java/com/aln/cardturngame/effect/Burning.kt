package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class Burning(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {
  override suspend fun onStartTurn(target: EntityViewModel) {
    val damage = target.maxHealth / HEALTH_PERCENTAGE
    target.applyDamage(target, amount = damage)
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_burning
    override val formatArgs = listOf(HEALTH_PERCENTAGE)
    override val nameRes = R.string.effect_burning
    override val descriptionRes = R.string.effect_burning_desc
    override val isPositive = false

    private const val HEALTH_PERCENTAGE = 10
  }
}