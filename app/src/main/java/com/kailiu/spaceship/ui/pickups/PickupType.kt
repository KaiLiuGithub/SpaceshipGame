package com.kailiu.spaceship.ui.pickups

enum class PickupType(var value: Int) {
    SHIELD(0), AMMO(1), LIFE(2);
    companion object {
        fun valueOf(value: Int): PickupType? = values().find { it.value == value }
    }
}