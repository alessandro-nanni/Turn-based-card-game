package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.WeakeningPoison
import com.aln.cardturngame.viewModel.EntityViewModel

class Mage : Entity(
  name = R.string.mage_name,
  initialStats = Stats(maxHealth = 150f, damage = 20f),
  color = Color(0xFF2FC0D3),
  activeAbility = object : Ability(R.string.heavy_strike_name, R.string.heavy_strike_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.addStatusEffect(WeakeningPoison(2))
    }
  },
  passiveAbility = object : Ability(R.string.heavy_strike_name, R.string.heavy_strike_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.heal(amount = 20f, repeats = 3)
    }
  },
  ultimateAbility = object : Ability(R.string.poison_name, R.string.poison_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      // Mage Ult: Massive Poison
      target.addStatusEffect(WeakeningPoison(4))
      target.receiveDamage(30f)
      println("${source.name} cast Ultimate Poison on ${target.name}!")
    }
  }
)