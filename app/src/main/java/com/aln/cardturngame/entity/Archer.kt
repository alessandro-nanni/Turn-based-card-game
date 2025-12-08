package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class Archer : Entity(
  name = R.string.entity_archer,
  iconRes = R.drawable.entity_archer,
  initialStats = Stats(maxHealth = 180f, damage = 13f),
  color = Color(0xFF2FC0D3),
  activeAbility = object :
    Ability(R.string.ability_arrow_rain, R.string.ability_arrow_rain_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.getAliveTeamMembers()
        .forEach { e -> source.applyDamage(e, repeats = 2, delayTime = 450) }
    }
  },
  passiveAbility = object :
    Ability(R.string.ability_cover, R.string.ability_cover_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
    }
  },
  ultimateAbility = object :
    Ability(R.string.poison_name, R.string.poison_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.receiveDamage(30f)
    }
  },
  damageType = DamageType.Magic
)