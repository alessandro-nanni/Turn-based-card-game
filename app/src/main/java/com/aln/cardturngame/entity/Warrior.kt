package com.aln.cardturngame.entity

import androidx.compose.ui.graphics.Color
import com.aln.cardturngame.R
import com.aln.cardturngame.viewModel.EntityViewModel

class Warrior : Entity(
  name = "Warrior",
  initialStats = Stats(
    maxHealth = 150f,
    damage = 20f
  ),
  color = Color(0xFFD32F2F),

  activeAbility = object : Ability(
    nameRes = R.string.heavy_strike_name,
    descriptionRes = R.string.heavy_strike_desc
  ) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      source.applyDamage(target, repeats = 3)
      println("${source.name} hit ${target.name} multiple times!")
    }
  },
  passiveAbility = object : Ability(
    nameRes = R.string.heavy_strike_name,
    descriptionRes = R.string.heavy_strike_desc
  ) {
    override suspend fun effect(source: EntityViewModel, target: EntityViewModel) {
      target.heal(amount = 20f, repeats = 3)
      println("${source.name} kissed ${target.name} for the lols!")
    }
  }
)