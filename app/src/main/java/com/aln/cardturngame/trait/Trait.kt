package com.aln.cardturngame.trait

import com.aln.cardturngame.value.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

interface Trait : Translatable {
  fun modifyOutgoingDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float): Float =
    amount

  fun modifyIncomingDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float): Float =
    amount

  fun modifyHeal(owner: EntityViewModel, amount: Float): Float = amount
  fun onStartTurn(owner: EntityViewModel) {}
  fun onEndTurn(owner: EntityViewModel) {}
  fun onDidDealDamage(owner: EntityViewModel, target: EntityViewModel, amount: Float, overkill: Float = 0f) {}
  fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {}
  fun onDeath(owner: EntityViewModel) {}
}