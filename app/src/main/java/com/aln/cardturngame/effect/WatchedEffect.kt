package com.aln.cardturngame.effect

import com.aln.cardturngame.R

class WatchedEffect(duration: Int) : StatusEffect(
  nameRes = R.string.effect_watched,
  descriptionRes = R.string.effect_watched_desc,
  iconRes = R.drawable.effect_watched,
  initialDuration = duration,
  isPositive = false
)