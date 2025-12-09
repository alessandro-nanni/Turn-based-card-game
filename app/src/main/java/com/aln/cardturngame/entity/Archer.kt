package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.BurningEffect
import com.aln.cardturngame.effect.PainLinkEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.value.Entities

class Archer : Entity(
  name = R.string.entity_archer,
  iconRes = R.drawable.entity_archer,
  initialStats = Stats(maxHealth = Entities.Archer.MAX_HEALTH, damage = Entities.Archer.DAMAGE),
  color = Color(0xFF2FC0D3),
  damageType = DamageType.Ranged,
  activeAbility = Ability(
    nameRes = R.string.ability_arrow_rain,
    descriptionRes = R.string.ability_arrow_rain_desc,
    formatArgs = listOf(Entities.Archer.ArrowRain.REPEATS)
  ) { source, target ->
    source.applyDamageToTargets(
      target.getAliveTeamMembers(),
      repeats = Entities.Archer.ArrowRain.REPEATS,
      delayTime = Entities.Archer.ArrowRain.DELAY
    )
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_cover,
    descriptionRes = R.string.ability_cover_desc,
    formatArgs = listOf(Entities.Archer.Cover.DURATION)
  ) { source, target ->
    source.addStatusEffect(PainLinkEffect(Entities.Archer.Cover.DURATION, target), source = source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_rain_fire,
    descriptionRes = R.string.ability_rain_fire_desc,
    formatArgs = listOf(Entities.Archer.RainFire.REPEATS, Entities.Archer.RainFire.BURN_DURATION)
  ) { source, target ->
    source.applyDamage(target, repeats = Entities.Archer.RainFire.REPEATS, delayTime = Entities.Archer.RainFire.DELAY)
    target.addStatusEffect(BurningEffect(Entities.Archer.RainFire.BURN_DURATION), source)
  }
)