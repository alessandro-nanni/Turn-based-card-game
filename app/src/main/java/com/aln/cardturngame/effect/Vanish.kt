package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entity.DamageType
import com.aln.cardturngame.viewModel.EntityViewModel

class Vanish(duration: Int) : StatusEffect(
  nameRes = R.string.effect_vanish,
  descriptionRes = R.string.effect_vanish_desc,
  iconRes = R.drawable.effect_vanish,
  initialDuration = duration,
  isPositive = true
) {
  private var ownerDamageType: DamageType? = null

}