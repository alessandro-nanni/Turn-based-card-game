package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class BerserkerTrait : Trait {
  override val nameRes: Int = R.string.trait_beserker_name
  override val descriptionRes: Int = R.string.trait_beserker_desc

    override fun modifyOutgoingDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float): Float {
        val healthPercent = owner.health / owner.maxHealth
        if (healthPercent < 0.3f) {
            return amount * 1.5f
        }
        return amount
    }
}