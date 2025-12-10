package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Translatable


class TauntEffect(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = false,
) {
  companion object Spec : Translatable {
    override val nameRes = R.string.effect_taunt
    override val descriptionRes = R.string.effect_taunt_desc
    val iconRes = R.drawable.effect_taunt
  }
}