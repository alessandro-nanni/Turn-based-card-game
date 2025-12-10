package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.effect.PrecisionEffect
import com.aln.cardturngame.entityFeatures.Ability
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Stats
import com.aln.cardturngame.trait.QuickDrawTrait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Rouge : Entity(
  name = R.string.entity_rouge,
  iconRes = R.drawable.entity_rouge,
  initialStats = Stats(maxHealth = MAX_HEALTH, damage = DAMAGE),
  color = Color(0xFFFF5722),
  damageType = DamageType.Ranged,
  traits = listOf(QuickDrawTrait()),
  activeAbility = Ability(
    nameRes = R.string.ability_bullseye,
    descriptionRes = R.string.ability_bullseye_desc
  ) { source, target ->
    source.applyDamage(target)
  },
  passiveAbility = Ability(
    nameRes = R.string.ability_take_aim,
    descriptionRes = R.string.ability_take_aim_desc,
    formatArgs = listOf(
      PrecisionEffect.Spec,
      PASSIVE_DURATION
    )
  ) { source, target ->
    target.addEffect(PrecisionEffect(PASSIVE_DURATION), source)
  },
  ultimateAbility = Ability(
    nameRes = R.string.ability_barrage,
    descriptionRes = R.string.ability_barrage_desc,
    formatArgs = listOf(
      ULTIMATE_ACTIVE_TIMES
    )
  ) { source, randomEnemy ->
    val rangers = source.getAliveTeamMembers().filter { it.damageType == DamageType.Ranged }

    CoroutineScope(Dispatchers.Main).launch {
      repeat(ULTIMATE_ACTIVE_TIMES) {
        rangers.forEach { ranger ->
          delay(200)
          val target = randomEnemy.getAliveTeamMembers().randomOrNull()
          target?.let { ranger.applyDamage(it) }
        }
      }
    }
  }
) {
  private companion object {
    const val MAX_HEALTH = 140f
    const val DAMAGE = 22f
    const val PASSIVE_DURATION = 2
    const val ULTIMATE_ACTIVE_TIMES = 2
  }
}