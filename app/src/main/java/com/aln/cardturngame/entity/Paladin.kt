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
      ACTIVE_DURATION
    )
  ) { source, target ->
    source.applyDamage(target)
    target.addEffect(TauntEffect(ACTIVE_DURATION), source)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_guard,
    descriptionRes = R.string.ability_guard_desc,
    formatArgs = listOf(
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(ProtectionEffect(PASSIVE_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_martyr,
    descriptionRes = R.string.ability_martyr_desc,
    formatArgs = listOf(
      ULTIMATE_SHIELD_DURATION,
      ULTIMATE_TAUNT_DURATION
    )
  ) { source, randomEnemy ->
    randomEnemy.getAliveTeamMembers().forEach { enemy ->
      enemy.addEffect(TauntEffect(ULTIMATE_TAUNT_DURATION), source)
    }
    source.addEffect(SpikedShieldEffect(ULTIMATE_SHIELD_DURATION), source)
  }
) {
  private companion object {
    const val MAX_HEALTH = 260f
    const val DAMAGE = 12f
    const val ACTIVE_DURATION = 2
    const val PASSIVE_DURATION = 4
    const val ULTIMATE_TAUNT_DURATION = 2
    const val ULTIMATE_SHIELD_DURATION = 3
  }
}