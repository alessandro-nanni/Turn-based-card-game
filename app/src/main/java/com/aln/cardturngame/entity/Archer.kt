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
    formatArgs = listOf(Active.REPEATS)
  ) { source, target ->
    source.applyDamageToTargets(
      target.getAliveTeamMembers(),
      repeats = Active.REPEATS,
      delayTime = 200L
    )
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_cover,
    descriptionRes = R.string.ability_cover_desc,
    formatArgs = listOf(Passive.DURATION)
  ) { source, target ->
    source.addStatusEffect(PainLinkEffect(Passive.DURATION, target), source = source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_rain_fire,
    descriptionRes = R.string.ability_rain_fire_desc,
    formatArgs = listOf(Ultimate.REPEATS, Ultimate.BURN_DURATION)
  ) { source, target ->
    source.applyDamage(
      target,
      repeats = Ultimate.REPEATS,
      delayTime = 150L
    )
    target.addStatusEffect(BurningEffect(Ultimate.BURN_DURATION), source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 100f
    const val DAMAGE = 15f

    object Active {
      const val REPEATS = 3
    }

    object Passive {
      const val DURATION = 2
    }

    object Ultimate {
      const val REPEATS = 5
      const val BURN_DURATION = 3
    }
  }
}