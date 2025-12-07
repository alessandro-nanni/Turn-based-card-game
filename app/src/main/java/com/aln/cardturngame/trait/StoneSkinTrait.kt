package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class StoneSkinTrait : Trait {
  override val nameRes: Int = R.string.trait_stone_skin_name
  override val descriptionRes: Int = R.string.trait_stone_skin_desc

    override fun modifyIncomingDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float {
        return (amount - 3f).coerceAtLeast(0f)
    }
}