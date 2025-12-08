package com.aln.cardturngame.effect

import com.aln.cardturngame.R

class TauntEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_taunt,
  descriptionRes = R.string.effect_taunt_desc,
  iconRes = R.drawable.effect_taunt,
  initialDuration = duration,
  isPositive = false
)