package com.aln.cardturngame.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.aln.cardturngame.effect.StatusEffect
import com.aln.cardturngame.entity.Entity
import com.aln.cardturngame.entity.Popup
import kotlinx.coroutines.delay

class EntityViewModel(
    val entity: Entity
) : ViewModel() {
    var health by mutableFloatStateOf(entity.initialStats.maxHealth)
    var maxHealth by mutableFloatStateOf(entity.initialStats.maxHealth)
    var damage by mutableFloatStateOf(entity.initialStats.damage)

    val statusEffects = mutableStateListOf<StatusEffect>()
    val popups = mutableStateListOf<Popup>()
    private var popupIdCounter = 0L

    val isAlive: Boolean
        get() = health > 0

    val name: String get() = entity.name
    val color: Color get() = entity.color

    fun addStatusEffect(effect: StatusEffect) {
        val existingEffect = statusEffects.find { it::class == effect::class }

        if (existingEffect != null) {
            existingEffect.duration = effect.duration
        } else {
            statusEffects.add(effect)
            effect.onApply(this)
        }
    }

    fun removeStatusEffect(effect: StatusEffect) {
        effect.onVanish(this)
        statusEffects.remove(effect)
    }

    fun addPopup(amount: Float, color: Color = Color.Red) {
        val id = popupIdCounter++
        popups.add(Popup(id, amount.toInt(), color))
    }

    fun receiveDamage(amount: Float) {
        health = (health - amount).coerceAtLeast(0f)
        addPopup(amount)
    }

    suspend fun heal(amount: Float, repeats: Int = 1, delayTime: Long = 400) {
        repeat(repeats) {
            health = (health + amount).coerceAtMost(maxHealth)
            addPopup(amount, Color.Green)
            if (repeats > 1) delay(delayTime)
        }
    }

    suspend fun applyDamage(
        target: EntityViewModel,
        amount: Float = damage,
        repeats: Int = 1,
        delayTime: Long = 400
    ) {
        repeat(repeats) {
            if (!target.isAlive) return
            target.receiveDamage(amount)
            if (repeats > 1) delay(delayTime)
        }
    }
}