package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.value.Traits
import com.aln.cardturngame.viewModel.EntityViewModel

class SpiteTrait : Trait {
  override val nameRes: Int = R.string.trait_spite
  override val descriptionRes: Int = R.string.trait_spite_desc
  override val formatArgs: List<Any> = listOf(Traits.Spite.RAGE_GAIN)

  override fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {
    owner.team?.increaseRage(Traits.Spite.RAGE_GAIN)
  }
}