package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class OverkillTrait : Trait {
  override val nameRes: Int = R.string.trait_overkill
  override val descriptionRes: Int = R.string.trait_overkill_desc

  override suspend fun onDidDealDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float, overkill: Float) {
    if (overkill > 0f) {
      val aliveTeammates = target.getAliveTeamMembers()
      
      if (aliveTeammates.isNotEmpty()) {
        val damagePerTeammate = overkill / aliveTeammates.size
        
        aliveTeammates.forEach { teammate ->
          teammate.receiveDamage(damagePerTeammate, source = owner)
        }
      }
    }
  }
}