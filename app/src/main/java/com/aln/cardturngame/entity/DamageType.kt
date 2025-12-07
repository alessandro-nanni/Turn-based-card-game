package com.aln.cardturngame.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R

enum class DamageType(
  @param:StringRes val nameResId: Int,
  val tintColor: Color,
  @param:DrawableRes val iconResId: Int
) {
  Ranged(R.string.damage_type_ranged, Color(0xFF8BC34A), R.drawable.damage_type_ranged),
  Magic(R.string.damage_type_magic, Color(0xFF00EAFF), R.drawable.damage_type_magic),
  Melee(R.string.damage_type_melee, Color(0xFFE91E63), R.drawable.damage_type_melee);
}