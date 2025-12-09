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
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFFFFB0C),
  activeAbility = Ability(
    nameRes = R.string.ability_slash,
    descriptionRes = R.string.ability_slash_desc,
    formatArgs = listOf(
      Active.REPEATS
    )
  ) { source, target ->
    source.applyDamage(target, repeats = Active.REPEATS, delayTime = 300)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_warriors_blessing,
    descriptionRes = R.string.ability_warriors_blessing_desc,
    formatArgs = listOf(
      Passive.DURATION
    )
  ) { source, target ->
    target.addStatusEffect(SharpenedBladeEffect(Passive.DURATION), source = source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_vanish,
    descriptionRes = R.string.ability_vanish_desc,
    formatArgs = listOf(
      Ultimate.DURATION
    )
  ) { source, _ ->
    source.addStatusEffect(VanishEffect(Ultimate.DURATION), source = source)
  },
  traits = listOf(SidestepTrait()),
  damageType = DamageType.Melee
) {
  private companion object {
    const val MAX_HEALTH = 240f
    const val DAMAGE = 10f

    object Active {
      const val REPEATS = 3
    }

    object Passive {
      const val DURATION = 3
    }

    object Ultimate {
      const val DURATION = 3
    }
  }
}