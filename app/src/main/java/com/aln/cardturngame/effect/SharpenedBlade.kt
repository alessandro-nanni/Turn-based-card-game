package com.aln.cardturngame.effect

import com.aln.cardturngame.R

class SharpenedBlade(duration: Int) : StatusEffect(
  nameRes = R.string.effect_sharpened_blade,
  descriptionRes = R.string.effect_sharpened_blade_desc,
  iconRes = R.drawable.effect_sharpened_blade,
  initialDuration = duration,
  isPositive = true
) {
  override fun modifyDamage(currentDamage: Float): Float {
    return currentDamage * 1.2f
  }
}