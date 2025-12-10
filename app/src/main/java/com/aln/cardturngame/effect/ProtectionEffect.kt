package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class ProtectionEffect(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = true,
  formatArgs = listOf(DAMAGE_REDUCTION_PERCENTAGE)
) {
  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    return currentDamage * ((100 - DAMAGE_REDUCTION_PERCENTAGE) / 100)
  }

  companion object Spec : Translatable {
    private const val DAMAGE_REDUCTION_PERCENTAGE = 25f
    override val nameRes = R.string.effect_protection
    override val descriptionRes = R.string.effect_protection_desc
    val iconRes = R.drawable.effect_protection
  }
}