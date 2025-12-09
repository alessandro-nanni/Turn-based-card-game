package com.aln.cardturngame.value

object Entities {
    object Archer {
        const val MAX_HEALTH = 180f
        const val DAMAGE = 13f
        
        object ArrowRain {
            const val REPEATS = 2
            const val DELAY = 450L
        }
        
        object Cover {
            const val DURATION = 2
        }
        
        object RainFire {
            const val REPEATS = 5
            const val DELAY = 250L
            const val BURN_DURATION = 2
        }
    }
}