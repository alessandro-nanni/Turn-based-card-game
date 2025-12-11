package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class Overloaded(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {

  override fun modifyDamage(currentDamage: Float): Float {
    return currentDamage * ((100 + DAMAGE_INCREASE) / 100)
  }

  override suspend fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    return currentDamage * ((100 + DAMAGE_INCREASE) / 100)
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_overloaded
    override val formatArgs = listOf(DAMAGE_INCREASE)
    override val nameRes = R.string.effect_overloaded
    override val descriptionRes = R.string.effect_overloaded_desc
    override val isPositive = true
    private const val DAMAGE_INCREASE = 30f
  }
}