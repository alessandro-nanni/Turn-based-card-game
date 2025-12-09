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

class Paladin : Entity(
  name = R.string.entity_paladin,
  iconRes = R.drawable.entity_paladin,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF8BC34A),
  damageType = DamageType.Melee,
  traits = listOf(SpiteTrait()),
  activeAbility = Ability(
    nameRes = R.string.ability_challenge,
    descriptionRes = R.string.ability_challenge_desc,
    formatArgs = listOf(
      Active.DURATION
    )
  ) { source, target ->
    source.applyDamage(target)
    target.addStatusEffect(TauntEffect(Active.DURATION), source)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_guard,
    descriptionRes = R.string.ability_guard_desc,
    formatArgs = listOf(
      Passive.DURATION
    )
  ) { source, target ->
    target.addStatusEffect(ProtectionEffect(Passive.DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_martyr,
    descriptionRes = R.string.ability_martyr_desc,
    formatArgs = listOf(
      Ultimate.SHIELD_DURATION,
      Ultimate.TAUNT_DURATION
    )
  ) { source, target ->
    target.getAliveTeamMembers().forEach { enemy ->
      enemy.addStatusEffect(TauntEffect(Ultimate.TAUNT_DURATION), source)
    }
    source.addStatusEffect(SpikedShieldEffect(Ultimate.SHIELD_DURATION), source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 260f
    const val DAMAGE = 12f

    object Active {
      const val DURATION = 2
    }

    object Passive {
      const val DURATION = 4
    }

    object Ultimate {
      const val TAUNT_DURATION = 2
      const val SHIELD_DURATION = 3
    }
  }
}