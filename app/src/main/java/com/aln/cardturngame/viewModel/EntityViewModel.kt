package com.aln.cardturngame.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.aln.cardturngame.effect.StatusEffect
import com.aln.cardturngame.effect.StunnedEffect
import com.aln.cardturngame.entityFeatures.DamageType
import com.aln.cardturngame.entity.Entity
import com.aln.cardturngame.entityFeatures.Popup
import com.aln.cardturngame.entityFeatures.Team
import com.aln.cardturngame.trait.ForsakenTrait
import com.aln.cardturngame.trait.Trait
import kotlinx.coroutines.delay
import kotlin.random.Random

class EntityViewModel(
  val entity: Entity
) : ViewModel() {
  var team: Team? = null

  private var baseDamage = entity.initialStats.damage

  var damage by mutableFloatStateOf(baseDamage)
    private set

  var health by mutableFloatStateOf(entity.initialStats.maxHealth)
  var maxHealth by mutableFloatStateOf(entity.initialStats.maxHealth)

  val statusEffects = mutableStateListOf<StatusEffect>()
  val popups = mutableStateListOf<Popup>()
  private var popupIdCounter = 0L

  // Animation States
  var attackAnimOffset by mutableStateOf<Offset?>(null)
  var hitAnimTrigger by mutableIntStateOf(0)
  var passiveAnimTrigger by mutableIntStateOf(0)

  val isAlive: Boolean
    get() = health > 0

  val name: Int = entity.name
  val color: Color = entity.color
  val damageType: DamageType = entity.damageType

  val iconRes: Int = entity.iconRes
  val traits: List<Trait> get() = entity.traits

  fun getAllTeamMembers(): List<EntityViewModel> {
    return team?.entities ?: emptyList()
  }

  fun getAliveTeamMembers(): List<EntityViewModel> {
    return getAllTeamMembers().filter { it.isAlive }
  }


  fun recalculateStats() {
    var newDamage = baseDamage
    statusEffects.forEach { effect ->
      newDamage = effect.modifyDamage(newDamage)
    }

    damage = newDamage
  }

  fun addPopup(text: String, color: Color = Color.Red, isStatus: Boolean = true) {
    val id = popupIdCounter++
    val xOffset = getXOffset()
    popups.add(Popup(id = id, text = text, color = color, xOffset = xOffset, isStatus = isStatus))
  }

  fun addPopup(textRes: Int, color: Color = Color.White) {
    val id = popupIdCounter++
    val xOffset = getXOffset()
    popups.add(Popup(id = id, textRes = textRes, color = color, xOffset = xOffset, isStatus = true))
  }

  fun getXOffset(): Float {
    return Random.nextInt(-20, 60).toFloat()
  }

  fun addPopup(amount: Float, color: Color = Color.Red) {
    val sign = if (color == Color.Green) "+" else "-"
    addPopup("$sign${amount.toInt()}", color, isStatus = false)
  }

  suspend fun receiveDamage(amount: Float, source: EntityViewModel? = null): Float {
    var actualDamage = amount

    statusEffects.toList().forEach { effect ->
      actualDamage = effect.modifyIncomingDamage(this, actualDamage, source)
    }

    traits.forEach { trait ->
      actualDamage = trait.modifyIncomingDamage(this, source, actualDamage)
    }

    if (actualDamage > 0) {
      if (source != null) {
        hitAnimTrigger++
      }

      val overkill = (actualDamage - health).coerceAtLeast(0f)
      health = (health - actualDamage).coerceAtLeast(0f)
      addPopup(actualDamage, Color.Red)

      if (!isAlive) {
        traits.forEach { trait ->
          trait.onDidReceiveDamage(this, source, actualDamage)
        }
        traits.forEach { trait ->
          trait.onDeath(this)
        }
        clearAllEffects()
      }

      traits.forEach { trait ->
        trait.onDidReceiveDamage(this, source, actualDamage)
      }

      source?.let { attacker ->
        attacker.traits.forEach { trait ->
          trait.onDidDealDamage(attacker, this, actualDamage, overkill)
        }
      }
    }

    return actualDamage
  }

  suspend fun heal(
    amount: Float,
    source: EntityViewModel? = null,
    repeats: Int = 1,
    delayTime: Long = 400
  ) {
    if (traits.any { it is ForsakenTrait } && source != this) {
      return
    }

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

  var onGetAttackOffset: ((EntityViewModel) -> Offset?)? = null

  suspend fun applyDamage(
    target: EntityViewModel,
    amount: Float = damage,
    repeats: Int = 1,
    delayTime: Long = 400,
    playAttackAnimation: Boolean = true
  ): Float {
    var totalDamage = 0f

    if (playAttackAnimation) {
      onGetAttackOffset?.invoke(target)?.let {
        attackAnimOffset = it
        delay(200)
      }
    }

    repeat(repeats) {
      if (!target.isAlive) return totalDamage

      var calculatedDamage = amount
      traits.forEach { trait ->
        calculatedDamage = trait.modifyOutgoingDamage(this, target, calculatedDamage)
      }

      totalDamage += target.receiveDamage(calculatedDamage, source = this)

      if (repeats > 1) delay(delayTime)
    }

    if (playAttackAnimation && attackAnimOffset != null) {
      attackAnimOffset = null
      delay(200)
    }

    return totalDamage
  }

  suspend fun applyDamageToTargets(
    targets: List<EntityViewModel>,
    amount: Float = damage,
    repeats: Int = 1,
    delayTime: Long = 400,
    playAttackAnimation: Boolean = true
  ): Float {
    var totalDamage = 0f

    if (playAttackAnimation && targets.isNotEmpty()) {
      onGetAttackOffset?.invoke(targets.random())?.let {
        attackAnimOffset = it
        delay(200)
      }
    }

    repeat(repeats) {
      targets.forEach { target ->
        totalDamage += applyDamage(target, amount, repeats = 1, delayTime = 0, playAttackAnimation = false)
      }

      if (repeats > 1) delay(delayTime)
    }

    if (playAttackAnimation && attackAnimOffset != null) {
      attackAnimOffset = null
      delay(200)
    }

    return totalDamage
  }

  fun getEnemies(): List<EntityViewModel> {
    return team?.enemyTeam?.entities?.filter { it.isAlive } ?: emptyList()
  }

  fun getRandomEnemy(): EntityViewModel {
    return getEnemies().random()
  }



  suspend fun withTemporaryDamage(tempDamage: Float, block: suspend () -> Unit) {
    val originalDamage = damage
    damage = tempDamage
    try {
      block()
    } finally {
      damage = originalDamage
    }
  }

  // EFFECTS
  val isStunned: Boolean
    get() = statusEffects.any { it is StunnedEffect }

  fun addEffect(effect: StatusEffect, source: EntityViewModel?) {
    val existingEffect = statusEffects.find { it::class == effect::class }
    if (existingEffect != null) {
      existingEffect.duration = effect.duration
      existingEffect.source = source
    } else {
      effect.source = source
      statusEffects.add(effect)
      effect.onApply(this)
      recalculateStats()
    }
  }

  fun removeEffect(effect: StatusEffect) {
    effect.onVanish(this)
    statusEffects.remove(effect)
    recalculateStats()
  }

  inline fun <reified T : StatusEffect> removeEffect() {
    val iterator = statusEffects.iterator()
    while (iterator.hasNext()) {
      val effect = iterator.next()
      if (effect is T) {
        effect.onVanish(this)
        iterator.remove()
      }
    }
    recalculateStats()
  }

  fun clearAllEffects(): Int {
    return clearNegativeEffects() + clearPositiveEffects()
  }

  fun clearNegativeEffects(): Int {
    val effectsToRemove = statusEffects.filter { !it.isPositive }
    effectsToRemove.forEach { effect ->
      removeEffect(effect)
    }
    return effectsToRemove.size
  }

  fun clearPositiveEffects(): Int {
    val effectsToRemove = statusEffects.filter { it.isPositive }
    effectsToRemove.forEach { effect ->
      removeEffect(effect)
    }
    return effectsToRemove.size
  }
}