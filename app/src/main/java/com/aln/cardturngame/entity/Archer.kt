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
  initialStats = Stats(maxHealth = 180f, damage = 13f),
  color = Color(0xFF2FC0D3),
  damageType = DamageType.Ranged,
  activeAbility = Ability(R.string.ability_arrow_rain, R.string.ability_arrow_rain_desc) { source, target ->
    source.applyDamageToTargets(target.getAliveTeamMembers(), repeats = 2, delayTime = 450)
  },
  passiveAbility = Ability(R.string.ability_cover, R.string.ability_cover_desc) { source, target ->
    source.addStatusEffect(PainLinkEffect(2, target), source = source)
  },
  ultimateAbility = Ability(R.string.ability_rain_fire, R.string.ability_rain_fire_desc) { source, target ->
    source.applyDamage(target, repeats = 5, delayTime = 250)
    target.addStatusEffect(BurningEffect(2), source)
  }
)