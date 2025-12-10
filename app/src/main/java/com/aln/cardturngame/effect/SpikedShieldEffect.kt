package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class SpikedShieldEffect(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
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

  companion object Spec : Translatable {
    private const val DAMAGE_REFLECTED = 20f
    override val nameRes = R.string.effect_spiked_shield
    override val descriptionRes = R.string.effect_spiked_shield_desc
    val iconRes = R.drawable.effect_spiked_shield
  }
}