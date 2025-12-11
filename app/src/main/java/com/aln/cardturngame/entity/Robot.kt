package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.Electrified
import com.aln.cardturngame.effect.Overloaded
import com.aln.cardturngame.effect.Stunned
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.trait.Meltdown

class Robot : Entity(
  name = R.string.entity_robot,
  iconRes = R.drawable.entity_robot,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFF44F7FD),
  damageType = DamageType.Magic,
  traits = listOf(Meltdown()),
  activeAbility = Ability(
    nameRes = R.string.ability_shock_attack,
    descriptionRes = R.string.ability_shock_attack_desc,
    formatArgs = listOf(Electrified.Spec, ACTIVE_DURATION)
  ) { source, target ->
    target.addEffect(Electrified(ACTIVE_DURATION, source), source)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_overload,
    descriptionRes = R.string.ability_overload_desc,
    formatArgs = listOf(Overloaded.Spec, PASSIVE_DURATION)
  ) { source, target ->
    target.addEffect(Overloaded(PASSIVE_DURATION), source = source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_shutdown,
    descriptionRes = R.string.ability_shutdown_desc,
    formatArgs = listOf(Stunned.Spec, ULTIMATE_STUNNED_DURATION)
  ) { source, randomEnemy ->
    randomEnemy.getAliveTeamMembers()
      .filter { it.statusEffects.any { effect -> effect is Electrified } }
      .forEach { it.addEffect(Stunned(ULTIMATE_STUNNED_DURATION), source) }
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