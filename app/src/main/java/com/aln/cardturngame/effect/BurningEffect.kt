package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class BurningEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_burning,
  descriptionRes = R.string.effect_burning_desc,
  iconRes = R.drawable.effect_burning,
  initialDuration = duration,
  isPositive = false,
  formatArgs = listOf(HEALTH_PERCENTAGE)
) {
  override suspend fun onStartTurn(target: EntityViewModel) {
    val damage = target.maxHealth / HEALTH_PERCENTAGE
    target.applyDamage(target, amount = damage)
  }

  private companion object {
    const val HEALTH_PERCENTAGE = 10
  }
}