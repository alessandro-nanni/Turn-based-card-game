package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class SpikedShieldEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_spiked_shield,
  descriptionRes = R.string.effect_spiked_shield_desc,
  iconRes = R.drawable.effect_spiked_shield,
  initialDuration = duration,
  isPositive = true,
  formatArgs = listOf(DAMAGE_REFLECTED)

) {
  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    val multiplier = DAMAGE_REFLECTED / 100
    val reducedDamage = currentDamage * (1 - multiplier)

    if (source != null && source != owner && source.isAlive) {
      val reflectedDamage = currentDamage * multiplier
      source.receiveDamage(reflectedDamage, source = null)
    }

    return reducedDamage
  }

  private companion object {
    const val DAMAGE_REFLECTED = 20f
  }
}