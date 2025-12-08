package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class BurningEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_burning,
  descriptionRes = R.string.effect_burning_desc,
  iconRes = R.drawable.effect_burning,
  initialDuration = duration,
  isPositive = false
) {
  override suspend fun onStartTurn(target: EntityViewModel) {
    val damage = target.maxHealth / 20
    target.applyDamage(target, amount = damage)
  }

}