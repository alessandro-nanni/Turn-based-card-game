package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.SharpenedBladeEffect
import com.aln.cardturngame.effect.VanishEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.trait.SidestepTrait

class Ninja : Entity(
  name = R.string.entity_ninja,
  iconRes = R.drawable.entity_ninja,
  initialStats = Stats(maxHealth = 240f, damage = 10f),
  color = Color(0xFFFFFB0C),
  activeAbility = Ability(R.string.ability_slash, R.string.ability_slash_desc) { source, target ->
    source.applyDamage(target, repeats = 3, delayTime = 300)
  },
  passiveAbility = Ability(R.string.ability_warriors_blessing, R.string.ability_warriors_blessing_desc) { source, target ->
    target.addStatusEffect(SharpenedBladeEffect(3), source = source)
  },
  ultimateAbility = Ability(R.string.ability_vanish, R.string.ability_vanish_desc) { source, _ ->
    source.addStatusEffect(VanishEffect(2), source = source)
  },
  traits = listOf(SidestepTrait()),
  damageType = DamageType.Melee
)