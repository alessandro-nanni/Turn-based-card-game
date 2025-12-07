package com.aln.cardturngame.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.aln.cardturngame.trait.Trait

sealed class Entity(
  @field:StringRes val name: Int,
  @field:DrawableRes val iconRes: Int,
  val initialStats: Stats,
  val passiveAbility: Ability,
  val activeAbility: Ability,
  val ultimateAbility: Ability,
  val traits: List<Trait> = emptyList()
)
