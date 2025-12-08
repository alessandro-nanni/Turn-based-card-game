package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class SpiteTrait : Trait {
  override val nameRes: Int = R.string.trait_spite
  override val descriptionRes: Int = R.string.trait_spite_desc

  override fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {
    owner.team?.increaseRage(3f)
  }
}