package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R

class Warrior : Entity(
  name = "Warrior",
  stats = Stats(
    maxHealth = 150f,
    damage = 200f
  ),
  color = Color(0xFFD32F2F),

  activeAbility = object : Ability(
    nameRes = R.string.heavy_strike_name,
    descriptionRes = R.string.heavy_strike_desc
  ) {
    override fun effect(source: Entity, target: Entity) {
      source.stats.applyDamage(target)

      println("${source.name} hit ${target.name}!")
    }
  },
  passiveAbility = object : Ability(
    nameRes = R.string.heavy_strike_name,
    descriptionRes = R.string.heavy_strike_desc
  ) {
    override fun effect(source: Entity, target: Entity) {
      println("${source.name} kissed ${target.name} for the lols!")
    }
  }
)