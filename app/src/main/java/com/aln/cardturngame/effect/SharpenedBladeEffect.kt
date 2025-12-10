package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class SharpenedBladeEffect(duration: Int) : StatusEffect(
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
    return if (ownerDamageType == DamageType.Melee) {
      currentDamage * ((100 + DAMAGE_INCREASE) / 100)
    } else {
      currentDamage
    }
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_sharpened_blade
    override val formatArgs = listOf(DAMAGE_INCREASE)
    override val nameRes = R.string.effect_sharpened_blade
    override val descriptionRes = R.string.effect_sharpened_blade_desc

    override val isPositive = true
    private const val DAMAGE_INCREASE = 15f
  }
}