package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.trait.BerserkerTrait
import com.aln.cardturngame.trait.StoneSkinTrait
import com.aln.cardturngame.viewModel.EntityViewModel

class Ninja : Entity(
  name = R.string.entity_ninja,
  iconRes = R.drawable.ninja,
  initialStats = Stats(maxHealth = 240f, damage = 10f),
  color = Color(0xFFFFFB0C),
  activeAbility = object : Ability(R.string.ability_slash, R.string.ability_slash_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.applyDamage(target, repeats = 3, delayTime = 300)
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
      // Ninja Ult: Massive damage to single target
      source.applyDamage(target, amount = source.damage * 3)
    }
  },
  traits = listOf(BerserkerTrait(), StoneSkinTrait()),
  damageType = DamageType.Melee
)