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
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF9C27B0),
  activeAbility = Ability(
    nameRes = R.string.ability_zap,
    descriptionRes = R.string.ability_zap_desc,
  ) { source, target ->
    source.applyDamage(target)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_override,
    descriptionRes = R.string.ability_override_desc,
    formatArgs = listOf(
      Passive.DAMAGE_PERCENTAGE
    )
  ) { _, target ->
    val enemies = target.getEnemies()
    if (enemies.isNotEmpty()) {
      val randomEnemy = enemies.random()
      val reducedDamage = target.damage * Passive.DAMAGE_PERCENTAGE / 100
      target.withTemporaryDamage(reducedDamage) {
        target.entity.activeAbility.effect(target, randomEnemy)
      }
    }
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_blessing,
    descriptionRes = R.string.ability_blessing_desc,
    formatArgs = listOf(
      Ultimate.HEAL_AMOUNT
    )
  ) { source, _ ->
    source.getAliveTeamMembers().forEach {
      it.heal(40f)
      it.clearNegativeEffects()
    }
  },
  damageType = DamageType.Magic,
  traits = listOf(OverkillTrait())
) {
  private companion object {
    const val MAX_HEALTH = 150f
    const val DAMAGE = 35f

    object Passive {
      const val DAMAGE_PERCENTAGE = 50
    }

    object Ultimate {
      const val HEAL_AMOUNT = 40
    }
  }
}