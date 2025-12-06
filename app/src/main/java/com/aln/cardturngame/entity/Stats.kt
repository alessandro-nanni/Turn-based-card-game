package com.aln.cardturngame.entity

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aln.cardturngame.R

class Stats(
  maxHealth: Float,
  health: Float = maxHealth,
  damage: Float
) {
  lateinit var entity: Entity
  var health by mutableFloatStateOf(health)
  var maxHealth by mutableFloatStateOf(maxHealth)
  var damage by mutableFloatStateOf(damage)

  fun applyDamage(target: Entity, amount: Float = damage){
    target.stats.receiveDamage(amount)
  }

  fun receiveDamage(amount: Float) {
    health = (health - amount).coerceAtLeast(0f)
  }

  @Composable
  fun StatsView() {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Health
      Text(
        text = "${health.toInt()}/${maxHealth.toInt()}",
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        painter = painterResource(id = R.drawable.health),
        contentDescription = "Damage",
        tint = Color.White,
        modifier = Modifier.size(28.dp)
      )

      Spacer(modifier = Modifier.width(16.dp))

      // Damage
      Text(
        text = "${damage.toInt()}",
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        painter = painterResource(id = R.drawable.attack_damage),
        contentDescription = "Damage",
        tint = Color.White,
        modifier = Modifier.size(28.dp)
      )
    }
  }
}