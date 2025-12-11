package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class Spite : Trait {
  override val nameRes: Int = R.string.trait_spite
  override val descriptionRes: Int = R.string.trait_spite_desc
  override val formatArgs: List<Any> = listOf(RAGE_GAIN)

  override fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {
    owner.team?.increaseRage(RAGE_GAIN)
  }

  companion object {
    const val RAGE_GAIN = 3f
  }
}