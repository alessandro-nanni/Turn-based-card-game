package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class SidestepTrait : Trait {
  override val nameRes: Int = R.string.trait_sidestep
  override val descriptionRes: Int = R.string.trait_sidestep_desc

    override fun modifyIncomingDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float {
      if (kotlin.random.Random.nextFloat() < 0.1f) {
        return 0f
      }
      return amount
    }
}