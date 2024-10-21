package com.FabboCassa.Tutorial.ecs.system

import com.FabboCassa.Tutorial.ecs.component.FacingComponent
import com.FabboCassa.Tutorial.ecs.component.FacingDirection
import com.FabboCassa.Tutorial.ecs.component.PlayerComponent
import com.FabboCassa.Tutorial.ecs.component.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.get

private const val TOUCH_TOLERANCE_DISTANCE =0.2f

class PlayerInputSystem(
    private val gameViewport: Viewport
): IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class,
    FacingComponent::class).get()) {
    private val tmpVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) { //called every frame
        val facingComponent = entity[FacingComponent.mapper]
        require(facingComponent != null) { "Entity |entity| must have a FacingComponent. entity=$entity" }
        val transformComponent = entity[TransformComponent.mapper]
        require(transformComponent != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }

        tmpVec.x = Gdx.input.x.toFloat()
        gameViewport.unproject(tmpVec) //create screen coordinate, x of last touch so we know where mouse is we don't care fo the y position
        val diffX = tmpVec.x - transformComponent.position.x - transformComponent.size.x * 0.5f
        facingComponent.direction = when {
            diffX < -TOUCH_TOLERANCE_DISTANCE -> FacingDirection.LEFT
            diffX > TOUCH_TOLERANCE_DISTANCE -> FacingDirection.RIGHT
            else -> FacingDirection.DEFAULT
        }
    }

}
