package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class SpikedShieldEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_spiked_shield,
  descriptionRes = R.string.effect_spiked_shield_desc,
  iconRes = R.drawable.effect_spiked_shield,
  initialDuration = duration,
  isPositive = true
) {
  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    val reducedDamage = currentDamage * 0.8f

    if (source != null && source != owner && source.isAlive) {
      val reflectedDamage = currentDamage * 0.2f
      source.receiveDamage(reflectedDamage, source = null)
    }

    return reducedDamage
  }
}