package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.WatchedEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.trait.ForsakenTrait

class Cultist : Entity(
  name = R.string.entity_cultist,
  iconRes = R.drawable.entity_cultist,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFE91E63),
  damageType = DamageType.Magic,
  traits = listOf(ForsakenTrait()),
  activeAbility = Ability(
    nameRes = R.string.ability_bewitched,
    descriptionRes = R.string.ability_bewitched_desc,
    formatArgs = listOf(ACTIVE_REPEATS)
  ) { source, target ->
    source.applyDamage(target)
    target.addEffect(WatchedEffect(ACTIVE_REPEATS), source)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_reckoning,
    descriptionRes = R.string.ability_reckoning_desc,
    formatArgs = listOf(PASSIVE_HEAL, PASSIVE_DAMAGE)
  ) { source, target ->
    val watchedEnemies = target.getEnemies().filter { member ->
      member.statusEffects.any { it is WatchedEffect }
    }

    target.heal(PASSIVE_HEAL * watchedEnemies.size, source)

    source.applyDamageToTargets(watchedEnemies, PASSIVE_DAMAGE)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_harvest,
    descriptionRes = R.string.ability_harvest_desc,
    formatArgs = listOf(ULTIMATE_MULTIPLIER, ULTIMATE_HEAL)
  ) { source, randomEnemy ->
    val watchedDuration = randomEnemy.getAllTeamMembers().sumOf { member ->
      member.statusEffects.find { it is WatchedEffect }?.duration ?: 0
    }
    val dmgDealt = source.applyDamage(randomEnemy, watchedDuration * ULTIMATE_MULTIPLIER)
    source.heal(dmgDealt * ULTIMATE_HEAL / 100, source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 200f
    const val DAMAGE = 8f
    const val ACTIVE_REPEATS = 3
    const val PASSIVE_HEAL = 8f
    const val PASSIVE_DAMAGE = 18f
    const val ULTIMATE_MULTIPLIER = 6f
    const val ULTIMATE_HEAL = 50
  }
}