package com.aln.cardturngame.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.trait.Trait

sealed class Entity(
  @field:StringRes val name: Int,
  @field:DrawableRes val iconRes: Int,
  val damageType : DamageType,
  val initialStats: Stats,
  val color: Color,
  val passiveAbility: Ability,
  val activeAbility: Ability,
  val ultimateAbility: Ability,
  val traits: List<Trait> = emptyList()
)