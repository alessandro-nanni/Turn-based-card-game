package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class ProtectionEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_protection,
  descriptionRes = R.string.effect_protection_desc,
  iconRes = R.drawable.effect_protection,
  initialDuration = duration,
  isPositive = true
) {
  override fun modifyIncomingDamage(owner: EntityViewModel, currentDamage: Float, source: EntityViewModel?): Float {
    return currentDamage * 0.75f
  }
}