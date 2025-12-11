package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class Ironclad : Trait {
  override val nameRes: Int = R.string.trait_ironclad
  override val descriptionRes: Int = R.string.trait_ironclad_desc
  override val formatArgs: List<Any> = listOf(DAMAGE_IGNORED)

  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    if (amount <= DAMAGE_IGNORED) {
      owner.addPopup(nameRes)
      return 0f
    }
    return amount
  }

  companion object {
    const val DAMAGE_IGNORED = 7f
  }
}