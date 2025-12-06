package com.aln.cardturngame.character

import androidx.compose.ui.graphics.Color

class Warrior(
) : AbstractCharacter(
  name = "Warrior", stats = Stats(
    health = 150f,
    maxHealth = 150f,
    damage = 10f
  ), color = Color(0xFFD32F2F)
) {

  override fun passive(target: AbstractCharacter) {
    // Logic: Just a placeholder for now
    println("$name activates passive: Intimidating Stare at ${target.name}")
  }


  override fun active(target: AbstractCharacter) {
    println("$name uses Heavy Strike on ${target.name}!")

    // Simple damage calculation logic
    // (Assuming you change Stats.health to 'var' to allow modification)
    val damage = this.stats.damage

    // Apply damage (Logic depends on how you handle health updates)
    target.stats.health -= damage

    println("${target.name} took $damage damage. HP is now ${target.stats.health}")
  }
}