package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.ProtectionEffect
import com.aln.cardturngame.effect.SpikedShieldEffect
import com.aln.cardturngame.effect.TauntEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.trait.SpiteTrait
import com.aln.cardturngame.viewModel.EntityViewModel

class Paladin : Entity(
  name = R.string.entity_paladin,
  iconRes = R.drawable.entity_paladin,
  initialStats = Stats(maxHealth = 260f, damage = 12f),
  color = Color(0xFF8BC34A),
  damageType = DamageType.Melee,
  traits = listOf(SpiteTrait()),
  activeAbility = object :
    Ability(R.string.ability_challenge, R.string.ability_challenge_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.applyDamage(target)
      target.addStatusEffect(TauntEffect(2), source)
    }
  },
  passiveAbility = object :
    Ability(R.string.ability_guard, R.string.ability_guard_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.addStatusEffect(ProtectionEffect(4), source)
    }
  },
  ultimateAbility = object :
    Ability(R.string.ability_martyr, R.string.ability_martyr_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.getEnemies().forEach { enemy ->
        enemy.addStatusEffect(TauntEffect(2), source)
      }
      source.addStatusEffect(SpikedShieldEffect(3), source)
    }
  }
)