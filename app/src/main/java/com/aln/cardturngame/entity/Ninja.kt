package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.SharpenedBlade
import com.aln.cardturngame.effect.Vanish
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
  passiveAbility = object :
    Ability(R.string.ability_warriors_blessing, R.string.ability_warriors_blessing_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.addStatusEffect(SharpenedBlade(3), source = source)
    }
  },
  // New Ultimate
  ultimateAbility = object : Ability(R.string.heavy_strike_name, R.string.heavy_strike_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.addStatusEffect(Vanish(2), source = source)
    }
  },
  traits = listOf(BerserkerTrait(), StoneSkinTrait()),
  damageType = DamageType.Melee
)