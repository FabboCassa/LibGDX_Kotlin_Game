package com.FabboCassa.Tutorial.ecs.system

import com.FabboCassa.Tutorial.V_HEIGHT
import com.FabboCassa.Tutorial.V_WIDTH
import com.FabboCassa.Tutorial.ecs.component.FacingComponent
import com.FabboCassa.Tutorial.ecs.component.FacingDirection
import com.FabboCassa.Tutorial.ecs.component.MoveComponent
import com.FabboCassa.Tutorial.ecs.component.PlayerComponent
import com.FabboCassa.Tutorial.ecs.component.RemoveComponent
import com.FabboCassa.Tutorial.ecs.component.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.max
import kotlin.math.min

//fixed timestep
private const val UPDATE_RATE = 1/25f //always running at 25 frame per second
private const val HOR_ACCELERATION = 16.5f
private const val VER_ACCELERATION = 2.25f
private const val MAX_VER_NEG_PLAYER_SPEED = 0.75f//when sucked in dark matter slowest u can go
private const val MAX_VER_POS_PLAYER_SPEED = 5f //because with boost can go faster
private const val MAX_HOR_SPEED = 5.5f

class MoveSystem: IteratingSystem(allOf(TransformComponent::class, MoveComponent::class).exclude(
    RemoveComponent::class.java
).get()) {

    private var accumulator = 0f

    override fun update(deltaTime: Float) {

        accumulator += deltaTime
        while(accumulator>= UPDATE_RATE) { //move step by step so we can check
            accumulator -= UPDATE_RATE

            entities.forEach { entity -> //
                entity[TransformComponent.mapper]?.let { transform ->
                    transform.prePosition.set(transform.position) //store current position in previous position then update
                }
            }

            super.update(UPDATE_RATE)
        }

        val alpha = accumulator / UPDATE_RATE //get value between 0 1 is percentage between us and next frame
        entities.forEach { entity -> //
            entity[TransformComponent.mapper]?.let { transform ->
               transform.interpolationPosition.set(
                   MathUtils.lerp(transform.prePosition.x, transform.position.x, alpha),
                   MathUtils.lerp(transform.prePosition.y, transform.position.y, alpha),
                   transform.position.z
               )
            }
        } //interpolation
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) {"Entity |entity| must have a TransformComponent. entity = $entity"}
        val move = entity[MoveComponent.mapper]
        require(move != null) {"Entity |entity| must have a MoveComponent. entity = $entity"}
        val player = entity[PlayerComponent.mapper]
        if(player != null) {
            entity[FacingComponent.mapper].let {
                facing ->
                movePlayer(transform, move,player,facing, deltaTime)
            }
        } else {
            moveEntity(transform, move, deltaTime)
        }
    }

    private fun movePlayer(transform: TransformComponent, move: MoveComponent, player: PlayerComponent, facing: FacingComponent?, deltaTime: Float) {

        //update horizontal speed
        if (facing != null) {
            move.speed.x = when(facing.direction) {
                FacingDirection.LEFT -> min(0f, move.speed.x - HOR_ACCELERATION * deltaTime)
                FacingDirection.RIGHT -> max(0f, move.speed.x + HOR_ACCELERATION * deltaTime)
                else -> 0f
            }
        }

        move.speed.x = MathUtils.clamp(move.speed.x, -MAX_HOR_SPEED,MAX_HOR_SPEED)

        //update vertical speed
        move.speed.y = MathUtils.clamp(move.speed.y -VER_ACCELERATION * deltaTime,-MAX_VER_NEG_PLAYER_SPEED, MAX_VER_POS_PLAYER_SPEED)

        moveEntity(transform, move, deltaTime)
    }

    private fun moveEntity(transform: TransformComponent, move: MoveComponent, deltaTime: Float) {
        transform.position.x = MathUtils.clamp(
            transform.position.x + move.speed.x * deltaTime,
            0f,
            V_WIDTH - transform.size.x
        )

        transform.position.y = MathUtils.clamp(
            transform.position.y + move.speed.y * deltaTime,
            1f,
            V_HEIGHT+1f - transform.size.y //+1 can go a little outside
        )
    }
}
