package com.aln.cardturngame.entityFeatures

import androidx.annotation.StringRes
import com.aln.cardturngame.viewModel.EntityViewModel

abstract class Ability(
  @field:StringRes val nameRes: Int,
  @field:StringRes val descriptionRes: Int
) {
  abstract suspend fun effect(source: EntityViewModel, target: EntityViewModel)
}