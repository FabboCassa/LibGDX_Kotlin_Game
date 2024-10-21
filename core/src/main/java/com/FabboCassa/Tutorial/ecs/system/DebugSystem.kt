package com.FabboCassa.Tutorial.ecs.system

import com.FabboCassa.Tutorial.ecs.component.PlayerComponent
import com.FabboCassa.Tutorial.ecs.component.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.getSystem
import kotlin.math.max
import kotlin.math.min

private const val WINDOW_INFO_UPDATE_RATE = 0.25f

class DebugSystem :
    IntervalIteratingSystem(allOf(PlayerComponent::class).get(), WINDOW_INFO_UPDATE_RATE) {
    init {
        setProcessing(true) //false to disable debug system (ex. when game released)
    }

    override fun processEntity(entity: Entity) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| must have a PlayerComponent. entity = $entity" }

        when {
            Gdx.input.isKeyPressed(Input.Keys.NUM_1) -> {
                transform.position.y = 1f
                player.life = 1f
                player.shield = 0f
            }

            Gdx.input.isKeyPressed(Input.Keys.NUM_2) -> {
                player.shield = min(player.maxShield, player.shield + 25f)
            }

            Gdx.input.isKeyPressed(Input.Keys.NUM_3) -> {
                player.shield = max(0f, player.shield - 25f)
            }

            Gdx.input.isKeyPressed(Input.Keys.NUM_4) -> {
                engine.getSystem<MoveSystem>().setProcessing(false)
            }

            Gdx.input.isKeyPressed(Input.Keys.NUM_5) -> {
                engine.getSystem<MoveSystem>().setProcessing(true)
            }
        }
        Gdx.graphics.setTitle("DM debug - pos: ${transform.position}, life: ${player.life}, shield: ${player.shield}")
    }
}
