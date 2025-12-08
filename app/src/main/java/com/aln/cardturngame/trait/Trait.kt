package com.aln.cardturngame.trait

import androidx.annotation.StringRes
import com.aln.cardturngame.viewModel.EntityViewModel

interface Trait {
  @get:StringRes
  val nameRes: Int

  @get:StringRes
  val descriptionRes: Int

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