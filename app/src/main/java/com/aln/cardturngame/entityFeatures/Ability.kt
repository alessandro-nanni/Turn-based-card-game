package com.aln.cardturngame.entityFeatures

import androidx.annotation.StringRes
import com.aln.cardturngame.viewModel.EntityViewModel

class Ability(
  @field:StringRes val nameRes: Int,
  @field:StringRes val descriptionRes: Int,
  private val onEffect: suspend (source: EntityViewModel, target: EntityViewModel) -> Unit
) {
  suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
    onEffect(source, target)
  }
}