package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.viewModel.EntityViewModel

class SharpenedBladeEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_sharpened_blade,
  descriptionRes = R.string.effect_sharpened_blade_desc,
  iconRes = R.drawable.effect_sharpened_blade,
  initialDuration = duration,
  isPositive = true
) {
  private var ownerDamageType: DamageType? = null

  override fun onApply(target: EntityViewModel) {
    ownerDamageType = target.damageType
  }

  override fun modifyDamage(currentDamage: Float): Float {
    return if (ownerDamageType == DamageType.Melee) {
      currentDamage * 1.2f
    } else {
      currentDamage
    }
  }
}