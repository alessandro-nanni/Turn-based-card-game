package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class Electrified(
  duration: Int, private val applier: EntityViewModel
) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs

) {

  override suspend fun onStartTurn(target: EntityViewModel) {
    target.applyDamage(target, amount = DAMAGE_AMOUNT)
    val electrifiedDuration =
      target.statusEffects.find { it is Electrified }?.duration?.minus(
        1
      )
    target.removeEffect<Electrified>()

    val potentialTargets = target.getAliveTeamMembers().filter { it != target }
    val newTarget = potentialTargets.randomOrNull()

    if (newTarget != null && electrifiedDuration != null && electrifiedDuration > 0) {
      newTarget.addEffect(Electrified(electrifiedDuration, applier), applier)
    }
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_electrified
    override val formatArgs = listOf(DAMAGE_AMOUNT)
    override val nameRes = R.string.effect_electrified
    override val descriptionRes = R.string.effect_electrified_desc
    override val isPositive = false

    private const val DAMAGE_AMOUNT = 12f
  }
}