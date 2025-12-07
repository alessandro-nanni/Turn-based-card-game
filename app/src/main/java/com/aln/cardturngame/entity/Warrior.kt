package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.trait.BerserkerTrait
import com.aln.cardturngame.trait.StoneSkinTrait
import com.aln.cardturngame.viewModel.EntityViewModel

class Warrior : Entity(
  name = R.string.warrior_name,
  iconRes = R.drawable.attack_damage,
  initialStats = Stats(maxHealth = 150f, damage = 20f),
  color = Color(0xFFD32F2F),
  activeAbility = object : Ability(R.string.heavy_strike_name, R.string.heavy_strike_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.applyDamage(target, repeats = 3)
    }
  },
  passiveAbility = object : Ability(R.string.heavy_strike_name, R.string.heavy_strike_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.heal(amount = 20f, repeats = 3)
    }
  },
  // New Ultimate
  ultimateAbility = object : Ability(R.string.heavy_strike_name, R.string.heavy_strike_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      // Warrior Ult: Massive damage to single target
      source.applyDamage(target, amount = source.damage * 3)
      println("${source.name} used Ultimate on ${target.name}!")
    }
  },
  traits = listOf(BerserkerTrait(), StoneSkinTrait())
)