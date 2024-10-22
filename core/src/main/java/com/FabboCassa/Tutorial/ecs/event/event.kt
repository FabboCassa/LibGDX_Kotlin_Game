package com.FabboCassa.Tutorial.ecs.event

import com.FabboCassa.Tutorial.ecs.component.PowerUpType
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.GdxSet
import java.util.EnumMap
import kotlin.reflect.KClass


sealed class GameEvent { //know what sublclasses we have for this class
    object PlayerDeath : GameEvent() {
        var distance = 0f //highscore

        override fun toString(): String = "PlayerDeath (distance=${distance})"
    }

    object CollectPowerUp: GameEvent() {
        lateinit var player: Entity
        var type = PowerUpType.NONE

        override fun toString(): String = "CollectPowerUpEvent (player=$player, type=$type)"
    }
}

interface GameEventListener {
    fun onEvent(event: GameEvent)
}

class GameEventManager {
    private val listeners = ObjectMap<KClass<out GameEvent>, GdxSet<GameEventListener>>() //any object can register a game event

    fun addListener(type: KClass<out GameEvent>, listener: GameEventListener) { //added a specific listener
        var eventListeners = listeners[type]
        if (eventListeners == null) {
            eventListeners = GdxSet()
            listeners.put(type, eventListeners)
        }
        eventListeners.add(listener)
    }

    fun removeListener(type: KClass<out GameEvent>, listener: GameEventListener) { //removed a specific listener
        listeners[type]?.remove(listener)
    }
    fun removeListener(listener: GameEventListener) { //removed all listeners of a specific listener
        listeners.values().forEach { it.remove(listener) }
    }

    fun dispatchEvent(event: GameEvent) {
        listeners[event::class]?.forEach {it.onEvent(event)}   //any logic can dispatch an event and notify listeners
    }
}
