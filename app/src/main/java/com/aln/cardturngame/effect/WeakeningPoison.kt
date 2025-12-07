package com.aln.cardturngame.effect

import com.aln.cardturngame.R
import com.aln.cardturngame.entity.Entity

class WeakeningPoison(duration: Int) : StatusEffect(
  nameRes = R.string.poison_name,
  descriptionRes = R.string.poison_desc,
    iconRes = R.drawable.weakening_poison,
    initialDuration = duration
) {
    override fun onApply(target: Entity) {
        target.stats.damage -= 5f
        println("${target.name} has been weakened! Damage -5")
    }

    override suspend fun onStartTurn(target: Entity) {
        target.stats.receiveDamage(10f)
        println("${target.name} took 10 poison damage.")
    }

    override fun onVanish(target: Entity) {
        target.stats.damage += 5f
        println("${target.name} is no longer weakened. Damage restored.")
    }
}