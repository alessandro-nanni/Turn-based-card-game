package com.aln.cardturngame.entityFeatures

import androidx.annotation.StringRes
import com.aln.cardturngame.entityFeatures.Translatable
import com.aln.cardturngame.viewModel.EntityViewModel

class Ability(
  @field:StringRes override val nameRes: Int,
  @field:StringRes override val descriptionRes: Int,
  override val formatArgs: List<Any> = emptyList(),
  private val onEffect: suspend (source: EntityViewModel, target: EntityViewModel) -> Unit
) : Translatable {
  suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
    onEffect(source, target)
  }
}