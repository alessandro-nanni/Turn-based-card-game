package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Translatable

class Watched(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive
) {
  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_watched
    override val nameRes = R.string.effect_watched
    override val descriptionRes = R.string.effect_watched_desc
    override val isPositive = false
  }
}