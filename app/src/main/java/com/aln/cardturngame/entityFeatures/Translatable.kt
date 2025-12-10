package com.aln.cardturngame.entityFeatures

import android.content.Context
import androidx.annotation.StringRes


interface Translatable {
  @get:StringRes
  val nameRes: Int
  @get:StringRes
  val descriptionRes: Int
  val formatArgs: List<Any>
    get() = emptyList()

  fun getName(context: Context): String {
    return context.getString(nameRes)
  }

  fun getDescription(context: Context): String {
    return if (formatArgs.isNotEmpty()) {
      val args = formatArgs.map { arg ->
        if (arg is Translatable) arg.getLinkString(context) else arg
      }.toTypedArray()
      context.getString(descriptionRes, *args)
    } else {
      context.getString(descriptionRes)
    }
  }

  fun getLinkString(context: Context): String {
    return "[[${getName(context)}|${getDescription(context)}]]"
  }
}