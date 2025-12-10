package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class PainLinkEffect(
  duration: Int,
  private val linkedTarget: EntityViewModel
) : StatusEffect(
  nameRes = nameRes,
  descriptionRes = descriptionRes,
  iconRes = iconRes,
  initialDuration = duration,
  formatArgs = formatArgs,
  isPositive = isPositive

) {
  override suspend fun modifyIncomingDamage(
    owner: EntityViewModel,
    currentDamage: Float,
    source: EntityViewModel?
  ): Float {
    if (linkedTarget.isAlive && linkedTarget != owner && currentDamage >= 1f) {
      val splitDamage = currentDamage * SPLIT_PERCENTAGE / 100

      linkedTarget.receiveDamage(splitDamage, source = source)

      return splitDamage
    }
    return currentDamage
  }

  companion object Spec : Translatable {
    val iconRes = R.drawable.effect_pain_link
    override val formatArgs = listOf(SPLIT_PERCENTAGE)
    override val nameRes = R.string.effect_pain_link
    override val descriptionRes = R.string.effect_pain_link_desc
    override val isPositive = true

    private const val SPLIT_PERCENTAGE = 50
  }
}