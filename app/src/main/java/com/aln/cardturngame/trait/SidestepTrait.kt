package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.trait.SpiteTrait.Companion.RAGE_GAIN
import com.aln.cardturngame.viewModel.EntityViewModel

class SidestepTrait : Trait {
  override val nameRes: Int = R.string.trait_sidestep
  override val descriptionRes: Int = R.string.trait_sidestep_desc
  override val formatArgs: List<Any> = listOf(DODGE_CHANCE)

  override fun modifyIncomingDamage(
    owner: EntityViewModel,
    source: EntityViewModel?,
    amount: Float
  ): Float {
    if (kotlin.random.Random.nextFloat() < (DODGE_CHANCE / 100)) {
      return 0f
    }
    return amount
  }

  companion object {
    const val DODGE_CHANCE = 10f
  }
}