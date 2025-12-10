package com.aln.cardturngame.effect

import com.aln.cardturngame.R


class TauntEffect(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = false,
) {
  companion object Spec {
    val nameRes = R.string.effect_taunt
    val descriptionRes = R.string.effect_taunt_desc
    val iconRes = R.drawable.effect_taunt
  }
}