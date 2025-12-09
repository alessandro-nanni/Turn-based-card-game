package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class ForsakenTrait : Trait {
  override val nameRes: Int = R.string.trait_forsaken
  override val descriptionRes: Int = R.string.trait_forsaken_desc

  override fun modifyHeal(owner: EntityViewModel, amount: Float) = 0f
}