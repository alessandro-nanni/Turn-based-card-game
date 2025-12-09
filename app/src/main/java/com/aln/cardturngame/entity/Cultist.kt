package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.BurningEffect
import com.aln.cardturngame.effect.PainLinkEffect
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
    target.addStatusEffect(WatchedEffect(ACTIVE_REPEATS), source)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_reckoning,
    descriptionRes = R.string.ability_reckoning_desc,
    formatArgs = listOf(PASSIVE_HEAL, PASSIVE_DAMAGE)
  ) { source, target ->
    val watchedEnemies = target.getAliveTeamMembers().count { member ->
      member.statusEffects.any { it is WatchedEffect }
    }
    target.heal(13f * watchedEnemies)

  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_rain_fire,
    descriptionRes = R.string.ability_rain_fire_desc,
    formatArgs = listOf(ULTIMATE_REPEATS, ULTIMATE_BURN_DURATION)
  ) { source, target ->
    source.applyDamage(
      target,
      repeats = ULTIMATE_REPEATS,
      delayTime = 150L
    )
    target.addStatusEffect(BurningEffect(ULTIMATE_BURN_DURATION), source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 200f
    const val DAMAGE = 8f
    const val ACTIVE_REPEATS = 3
    const val PASSIVE_HEAL = 6f
    const val PASSIVE_DAMAGE = 18
    const val ULTIMATE_REPEATS = 5
    const val ULTIMATE_BURN_DURATION = 3
  }
}