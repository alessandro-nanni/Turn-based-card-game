package com.aln.cardturngame.value

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
            context.getString(descriptionRes, *formatArgs.toTypedArray())
        } else {
            context.getString(descriptionRes)
        }
    }
}