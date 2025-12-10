package com.aln.cardturngame.entityFeatures

import android.content.Context
import androidx.annotation.StringRes
import com.aln.cardturngame.viewModel.EntityViewModel

data class Ability(
  @get:StringRes override val nameRes: Int,
  @get:StringRes override val descriptionRes: Int,
  override val formatArgs: List<Any> = emptyList(),
  private val onEffect: suspend (source: EntityViewModel, target: EntityViewModel) -> Unit
) : Translatable {

  suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
    onEffect(source, target)
  }

  override fun getName(context: Context): String {
    return context.getString(nameRes)
  }

  override fun getDescription(context: Context): String {
    val processedArgs = formatArgs.map { arg ->
      if (arg is Translatable) {
        val name = context.getString(arg.nameRes)
        val descArgs = arg.formatArgs.toTypedArray()
        val desc = context.getString(arg.descriptionRes, *descArgs)
        "[[$name|$desc]]"
      } else {
        arg
      }
    }
    return context.getString(descriptionRes, *processedArgs.toTypedArray())
  }

}