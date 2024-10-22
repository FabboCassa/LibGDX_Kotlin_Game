package com.FabboCassa.Tutorial.ecs.event

import com.FabboCassa.Tutorial.ecs.component.PowerUpType
import com.badlogic.ashley.core.Entity
import ktx.collections.GdxSet
import java.util.EnumMap

enum class GameEventType {
    PLAYER_DEATH,
    COLLECT_POWER_UP
}

interface GameEvent

object GameEventPlayerDeath : GameEvent { //object because only one instance
    var distance = 0f //highscore

    override fun toString(): String = "GameEventPlayerDeath (distance=$distance)"
}

object GameEventCollectPowerUpEvent: GameEvent {
    lateinit var player: Entity
    var type = PowerUpType.NONE

    override fun toString(): String = "GameEventCollectPowerUpEvent (player=$player, type=$type)"
}

interface GameEventListener {
    fun onEvent(type: GameEventType, data: GameEvent? = null)
}

class GameEventManager {
    private val listeners = EnumMap<GameEventType, GdxSet<GameEventListener>>(GameEventType::class.java) //any object can register a game event

    fun addListener(type: GameEventType, listener: GameEventListener) { //added a specific listener
        var eventListeners = listeners[type]
        if (eventListeners == null) {
            eventListeners = GdxSet()
            listeners[type] = eventListeners
        }
        eventListeners.add(listener)
    }

    fun removeListener(type: GameEventType, listener: GameEventListener) { //removed a specific listener
        listeners[type]?.remove(listener)
    }
    fun removeListener(listener: GameEventListener) { //removed all listeners of a specific listener
        listeners.values.forEach { it.remove(listener) }
    }

    fun dispatchEvent(type: GameEventType, data: GameEvent? = null) {
        listeners[type]?.forEach {it.onEvent(type, data)}   //any logic can dispatch an event and notify listeners
    }
}
