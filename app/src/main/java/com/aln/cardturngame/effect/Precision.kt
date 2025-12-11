package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class Precision(duration: Int) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  isPositive = isPositive,
  formatArgs = formatArgs
) {
  private var ownerDamageType: DamageType? = null

  override fun onApply(target: EntityViewModel) {
    ownerDamageType = target.damageType
  }

  override fun modifyDamage(currentDamage: Float): Float {
    return if (ownerDamageType == DamageType.Ranged) {
      currentDamage * ((100 + DAMAGE_INCREASE) / 100)
    } else {
      currentDamage
    }
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_precision
    override val formatArgs = listOf(DAMAGE_INCREASE)
    override val nameRes = R.string.effect_precision
    override val descriptionRes = R.string.effect_precision_desc

    override val isPositive = true
    private const val DAMAGE_INCREASE = 30f
  }
}