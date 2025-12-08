package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.trait.OverkillTrait
import com.aln.cardturngame.viewModel.EntityViewModel

class Wizard : Entity(
  name = R.string.entity_wizard,
  iconRes = R.drawable.entity_wizard,
  initialStats = Stats(maxHealth = 150f, damage = 45f),
  color = Color(0xFF9C27B0),
  activeAbility = object :
    Ability(R.string.ability_zap, R.string.ability_zap_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.applyDamage(target)
    }
  },
  passiveAbility = object :
    Ability(R.string.ability_override, R.string.ability_override_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      val enemies = target.getEnemies()
      if (enemies.isNotEmpty()) {
        val randomEnemy = enemies.random()
        val reducedDamage = target.damage / 2f
        target.withTemporaryDamage(reducedDamage) {
          target.entity.activeAbility.effect(target, randomEnemy)
        }
      }
    }
  },
  ultimateAbility = object :
    Ability(R.string.ability_blessing, R.string.ability_blessing_desc) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.getAliveTeamMembers().forEach {
        run {
          it.heal(40f)
          it.clearNegativeEffects()
        }
      }
    }
  },
  damageType = DamageType.Magic,
  traits = listOf(OverkillTrait())
)