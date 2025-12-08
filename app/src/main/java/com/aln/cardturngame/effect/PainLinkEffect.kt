package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class PainLinkEffect(
  duration: Int,
  private val linkedTarget: EntityViewModel
) : StatusEffect(
  nameRes = R.string.effect_pain_link,
  descriptionRes = R.string.effect_pain_link_desc,
  iconRes = R.drawable.effect_pain_link,
  initialDuration = duration,
  isPositive = true
) {
  override fun modifyIncomingDamage(owner: EntityViewModel, currentDamage: Float, source: EntityViewModel?): Float {
    if (linkedTarget.isAlive && linkedTarget != owner && currentDamage >= 1f) {
      val splitDamage = currentDamage / 2f

      linkedTarget.receiveDamage(splitDamage, source = source)

      return splitDamage
    }
    return currentDamage
  }
}