package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.SpikedShieldEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.trait.IroncladTrait

class Monk : Entity(
  name = R.string.entity_monk,
  iconRes = R.drawable.entity_monk,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFDA855D),
  damageType = DamageType.Magic,
  traits = listOf(IroncladTrait()),
  activeAbility = Ability(
    nameRes = R.string.ability_syphon,
    descriptionRes = R.string.ability_syphon_desc,
  ) { source, target ->
    val healAmount = source.applyDamage(target) / 3
    source.getAliveTeamMembers().forEach { it.heal(healAmount, source) }
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_iron_will,
    descriptionRes = R.string.ability_iron_will_desc,
    formatArgs = listOf(
      SpikedShieldEffect.Spec,
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(SpikedShieldEffect(PASSIVE_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_liberation,
    descriptionRes = R.string.ability_liberation_desc,
    formatArgs = listOf(
      ULTIMATE_DAMAGE_MULTIPLIER
    )
  ) { source, randomEnemy ->
    var effectsCleared = 0
    source.getAliveTeamMembers().forEach { effectsCleared += it.clearAllEffects() }
    source.applyDamage(randomEnemy, effectsCleared * ULTIMATE_DAMAGE_MULTIPLIER)
  }
) {
  private companion object {
    const val MAX_HEALTH = 170f
    const val DAMAGE = 20f
    const val PASSIVE_DURATION = 2
    const val ULTIMATE_DAMAGE_MULTIPLIER = 10f

  }
}