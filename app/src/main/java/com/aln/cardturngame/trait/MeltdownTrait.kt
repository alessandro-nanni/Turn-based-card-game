package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class MeltdownTrait : Trait {
  override val nameRes: Int = R.string.trait_meltdown
  override val descriptionRes: Int = R.string.trait_meltdown_desc
  override val formatArgs: List<Any> = listOf(DEATH_DAMAGE)

  override suspend fun onDeath(owner: EntityViewModel) {
    owner.applyDamageToTargets(owner.getEnemies(), DEATH_DAMAGE)
  }

  companion object {
    const val DEATH_DAMAGE = 23f
  }
}