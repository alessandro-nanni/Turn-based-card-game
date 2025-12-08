package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.SharpenedBladeEffect
import com.aln.cardturngame.effect.VanishEffect
import com.aln.cardturngame.boardItems.Ability
import com.aln.cardturngame.boardItems.DamageType
import com.aln.cardturngame.boardItems.Stats
import com.aln.cardturngame.trait.SidestepTrait
import com.aln.cardturngame.viewModel.EntityViewModel

class Ninja : Entity(
  name = R.string.entity_ninja,
  iconRes = R.drawable.entity_ninja,
  initialStats = Stats(maxHealth = 240f, damage = 10f),
  color = Color(0xFFFFFB0C),
  activeAbility = object :
    Ability(R.string.ability_slash, R.string.ability_slash_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.applyDamage(target, repeats = 3, delayTime = 300)
    }
  },
  passiveAbility = object :
    Ability(R.string.ability_warriors_blessing, R.string.ability_warriors_blessing_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.addStatusEffect(SharpenedBladeEffect(3), source = source)
    }
  },
  ultimateAbility = object :
    Ability(R.string.ability_vanish, R.string.ability_vanish_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.addStatusEffect(VanishEffect(2), source = source)
    }
  },
  traits = listOf(SidestepTrait()),
  damageType = DamageType.Melee
)