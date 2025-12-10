package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.ElectrifiedEffect
import com.aln.cardturngame.effect.OverloadedEffect
import com.aln.cardturngame.effect.StunnedEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats

class Robot : Entity(
  name = R.string.entity_robot,
  iconRes = R.drawable.entity_robot,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF44F7FD),
  damageType = DamageType.Magic,
  activeAbility = Ability(
    nameRes = R.string.ability_shock_attack,
    descriptionRes = R.string.ability_shock_attack_desc,
    formatArgs = listOf(ElectrifiedEffect.Spec, ACTIVE_DURATION)
  ) { source, target ->
    target.addEffect(ElectrifiedEffect(ACTIVE_DURATION, source), source)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_overload,
    descriptionRes = R.string.ability_overload_desc,
    formatArgs = listOf(OverloadedEffect.Spec, PASSIVE_DURATION)
  ) { source, target ->
    target.addEffect(OverloadedEffect(PASSIVE_DURATION), source = source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_shutdown,
    descriptionRes = R.string.ability_shutdown_desc,
    formatArgs = listOf(StunnedEffect.Spec, ULTIMATE_STUNNED_DURATION)
  ) { source, randomEnemy ->
    randomEnemy.getAliveTeamMembers()
      .filter { it.statusEffects.any { effect -> effect is ElectrifiedEffect } }
      .forEach { it.addEffect(StunnedEffect(ULTIMATE_STUNNED_DURATION), source) }
  }
) {
  private companion object {
    const val MAX_HEALTH = 180f
    const val DAMAGE = 0f
    const val ACTIVE_DURATION = 3
    const val PASSIVE_DURATION = 3
    const val ULTIMATE_STUNNED_DURATION = 3
  }
}