package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.BurningEffect
import com.aln.cardturngame.effect.PainLinkEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats

class Archer : Entity(
  name = R.string.entity_archer,
  iconRes = R.drawable.entity_archer,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF2FC0D3),
  damageType = DamageType.Ranged,
  activeAbility = Ability(
    nameRes = R.string.ability_arrow_rain,
    descriptionRes = R.string.ability_arrow_rain_desc,
    formatArgs = listOf(ACTIVE_REPEATS)
  ) { source, target ->
    source.applyDamageToTargets(
      target.getAliveTeamMembers(),
      repeats = ACTIVE_REPEATS,
      delayTime = 200L
    )
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_cover,
    descriptionRes = R.string.ability_cover_desc,
    formatArgs = listOf(PASSIVE_DURATION)
  ) { source, target ->
    source.addStatusEffect(PainLinkEffect(PASSIVE_DURATION, target), source = source)
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
    const val MAX_HEALTH = 100f
    const val DAMAGE = 15f
    const val ACTIVE_REPEATS = 3
    const val PASSIVE_DURATION = 2
    const val ULTIMATE_REPEATS = 5
    const val ULTIMATE_BURN_DURATION = 3
  }
}