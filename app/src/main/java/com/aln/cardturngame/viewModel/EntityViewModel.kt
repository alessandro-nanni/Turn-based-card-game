package com.aln.cardturngame.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.aln.cardturngame.effect.StatusEffect
import com.aln.cardturngame.entity.DamageType
import com.aln.cardturngame.entity.Entity
import com.aln.cardturngame.entity.Popup
import com.aln.cardturngame.trait.Trait
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

  val name: Int = entity.name
  val color: Color = entity.color
  val damageType: DamageType = entity.damageType

  val iconRes: Int = entity.iconRes
  val traits: List<Trait> get() = entity.traits

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

  fun receiveDamage(amount: Float, source: EntityViewModel? = null) {
    var actualDamage = amount
    traits.forEach { trait ->
      actualDamage = trait.modifyIncomingDamage(this, source, actualDamage)
    }

    health = (health - actualDamage).coerceAtLeast(0f)
    addPopup(actualDamage)

    traits.forEach { trait ->
      trait.onDidReceiveDamage(this, source, actualDamage)
    }

    source?.let { attacker ->
      attacker.traits.forEach { trait ->
        trait.onDidDealDamage(attacker, this, actualDamage)
      }
    }
  }

  suspend fun heal(amount: Float, repeats: Int = 1, delayTime: Long = 400) {
    repeat(repeats) {
      var actualHeal = amount
      traits.forEach { trait ->
        actualHeal = trait.modifyHeal(this, actualHeal)
      }

      health = (health + actualHeal).coerceAtMost(maxHealth)
      addPopup(actualHeal, Color.Green)
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

      var calculatedDamage = amount
      traits.forEach { trait ->
        calculatedDamage = trait.modifyOutgoingDamage(this, target, calculatedDamage)
      }

      target.receiveDamage(calculatedDamage, source = this)

      if (repeats > 1) delay(delayTime)
    }
  }
}