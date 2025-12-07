package com.aln.cardturngame.trait

import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class SpikyArmorTrait : Trait {
  override val nameRes: Int = R.string.trait_spiky_armor_name
  override val descriptionRes: Int = R.string.trait_spiky_armor_desc

  override fun onDidReceiveDamage(owner: EntityViewModel, source: EntityViewModel?, amount: Float) {
    if (source != null && source != owner) {
      source.receiveDamage(5f, source = owner)
    }
  }
}