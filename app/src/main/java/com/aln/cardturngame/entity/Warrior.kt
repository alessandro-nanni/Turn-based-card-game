package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R

class Warrior : Entity(
  name = "Warrior",
  stats = Stats(
    health = 150f,
    maxHealth = 150f,
    damage = 10f
  ),
  color = Color(0xFFD32F2F),

  activeAbility = object : Ability(
    nameRes = R.string.heavy_strike_name,
    descriptionRes = R.string.heavy_strike_desc
  ) {
    override fun effect(source: Entity, target: Entity) {
      val baseDamage = source.stats.damage

      val totalDamage = baseDamage * 1.5f

      target.stats.health -= totalDamage

      println("${source.name} hit ${target.name} for $totalDamage!")
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