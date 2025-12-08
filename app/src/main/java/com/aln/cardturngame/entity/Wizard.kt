package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.BurningEffect
import com.aln.cardturngame.boardItems.Ability
import com.aln.cardturngame.boardItems.DamageType
import com.aln.cardturngame.boardItems.Stats
import com.aln.cardturngame.viewModel.EntityViewModel

class Wizard : Entity(
  name = R.string.entity_wizard,
  iconRes = R.drawable.entity_wizard,
  initialStats = Stats(maxHealth = 180f, damage = 13f),
  color = Color(0xFF2FC0D3),
  activeAbility = object :
    Ability(R.string.ability_zap, R.string.ability_zap_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.applyDamage(target)
    }
  },
  passiveAbility = object :
    Ability(R.string.ability_override, R.string.ability_override_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
    }
  },
  ultimateAbility = object :
    Ability(R.string.ability_rain_fire, R.string.ability_rain_fire_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
    }
  },
  damageType = DamageType.Magic
)