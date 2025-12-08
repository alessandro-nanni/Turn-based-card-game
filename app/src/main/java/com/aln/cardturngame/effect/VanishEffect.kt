package com.aln.cardturngame.effect

import com.aln.cardturngame.R

class VanishEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_vanish,
  descriptionRes = R.string.effect_vanish_desc,
  iconRes = R.drawable.effect_vanish,
  initialDuration = duration,
  isPositive = true
)