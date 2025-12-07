package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.WeakeningPoison

class Mage : Entity(
  name = "Mage",
  stats = Stats(
    maxHealth = 150f,
    damage = 20f
  ),
  color = Color(0xFF2FC0D3),

  activeAbility = object : Ability(
    nameRes = R.string.heavy_strike_name,
    descriptionRes = R.string.heavy_strike_desc
  ) {
    override suspend fun effect(source: Entity, target: Entity) {

      target.addStatusEffect(WeakeningPoison(4))

      println("${source.name} hit ${target.name} multiple times!")
    }
  },
  passiveAbility = object : Ability(
    nameRes = R.string.heavy_strike_name,
    descriptionRes = R.string.heavy_strike_desc
  ) {
    override suspend fun effect(source: Entity, target: Entity) {
      target.stats.heal(amount = 20f, repeats = 3)

      println("${source.name} kissed ${target.name} for the lols!")
    }
  }
)