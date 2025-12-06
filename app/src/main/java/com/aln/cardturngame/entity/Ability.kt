package com.aln.cardturngame.entity

import androidx.annotation.StringRes

abstract class Ability(
  @param:StringRes val nameRes: Int,
  @param:StringRes val descriptionRes: Int
) {
  abstract fun effect(source: Entity, target: Entity)
}