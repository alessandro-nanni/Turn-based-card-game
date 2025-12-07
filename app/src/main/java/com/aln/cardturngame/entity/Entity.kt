package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color

abstract class Entity(
  val name: String,
  val initialStats: Stats,
  val color: Color,
  val passiveAbility: Ability,
  val activeAbility: Ability
)