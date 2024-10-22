package com.FabboCassa.Tutorial.ecs.system

import com.FabboCassa.Tutorial.V_WIDTH
import com.FabboCassa.Tutorial.ecs.component.AnimationComponent
import com.FabboCassa.Tutorial.ecs.component.GraphicComponent
import com.FabboCassa.Tutorial.ecs.component.MoveComponent
import com.FabboCassa.Tutorial.ecs.component.PlayerComponent
import com.FabboCassa.Tutorial.ecs.component.PowerUpComponent
import com.FabboCassa.Tutorial.ecs.component.PowerUpType
import com.FabboCassa.Tutorial.ecs.component.RemoveComponent
import com.FabboCassa.Tutorial.ecs.component.TransformComponent
import com.FabboCassa.Tutorial.ecs.event.GameEventCollectPowerUpEvent
import com.FabboCassa.Tutorial.ecs.event.GameEventManager
import com.FabboCassa.Tutorial.ecs.event.GameEventType
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.ashley.with
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<PowerUpSystem>()
private const val MAX_SPAWN_INTERVAL = 1.5f
private const val MIN_SPAWN_INTERVAL = 0.9f
private const val POWER_UP_SPEED = -8.75f
private const val BOOST_1_SPEED_GAIN = 3f
private const val BOOST_2_SPEED_GAIN = 3.75f
private const val LIFE_GAIN = 25f
private const val SHIELD_GAIN = 25f

private class SpawnPattern(
    type1: PowerUpType = PowerUpType.NONE,
    type2: PowerUpType = PowerUpType.NONE,
    type3: PowerUpType = PowerUpType.NONE,
    type4: PowerUpType = PowerUpType.NONE,
    type5: PowerUpType = PowerUpType.NONE,
    val typesArray: GdxArray<PowerUpType> = gdxArrayOf(type1, type2, type3, type4, type5)
)

class PowerUpSystem(
    private val gameEventManager: GameEventManager
):
    IteratingSystem(allOf(PowerUpComponent::class, TransformComponent::class).get()) {

    private val playerBoundingRect = Rectangle()
    private val powerUpBoundingRect = Rectangle()
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }
    private var spawnTime = 0f
    private val spawnPatterns = gdxArrayOf(
        SpawnPattern(type1 = PowerUpType.SPEED_1, type2 = PowerUpType.SPEED_2, type5 = PowerUpType.SHIELD),
        SpawnPattern(type1 = PowerUpType.SPEED_2, type2 = PowerUpType.LIFE, type5 = PowerUpType.SPEED_1),
        SpawnPattern(type2 = PowerUpType.SPEED_1, type4 = PowerUpType.SPEED_1, type5 = PowerUpType.SPEED_1),
        SpawnPattern(type2 = PowerUpType.SPEED_1, type4 = PowerUpType.SPEED_1),
        SpawnPattern(
            type1 = PowerUpType.SHIELD,
            type2 = PowerUpType.SHIELD,
            type4 = PowerUpType.LIFE,
            type5 = PowerUpType.SPEED_2
        )
    )
    private val currentSpawnPattern = GdxArray<PowerUpType>(spawnPatterns.size)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime // reduce spawn time

        if (spawnTime <= 0f) { // if time is up, spawn a power-up
            spawnTime = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL) // reset spawn time

            // Check if current pattern is empty and generate a new one if needed
            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(
                    spawnPatterns[MathUtils.random(0, spawnPatterns.size - 1)].typesArray
                )
                LOG.debug { "Next pattern: $currentSpawnPattern" }
            }

            // Now spawn the next power-up from the current pattern
            val powerUpType = currentSpawnPattern.removeIndex(0)
            if (powerUpType != PowerUpType.NONE) { // Ensure the power-up type is valid
                spawnPowerUp(powerUpType, 1f * MathUtils.random(0, V_WIDTH - 1), 16f)
            }
        }
    }


    private fun spawnPowerUp(powerUpType: PowerUpType, x: Float, y: Float) {

        engine.entity {
            with<TransformComponent> {
                setInitialPosition(x, y, 0f)
            }
            with<PowerUpComponent> { type = powerUpType }
            with<AnimationComponent> { type = powerUpType.animationType }
            with<GraphicComponent>()
            with<MoveComponent> {
                speed.y = POWER_UP_SPEED
            }

        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform!=null) {"Entity |entity| must have a TransformComponent. entity=$entity"}

        if (transform.position.y<= 1f) {
            entity.addComponent<RemoveComponent>(engine)
            return
        }

        powerUpBoundingRect.set(
            transform.position.x,
            transform.position.y,
            transform.size.x,
            transform.size.y)

        playerEntities.forEach{ player ->
            player[TransformComponent.mapper]?.let { playerTransform ->
                playerBoundingRect.set(
                    playerTransform.position.x,
                    playerTransform.position.y,
                    playerTransform.size.x,
                    playerTransform.size.y
                )

                if (playerBoundingRect.overlaps(powerUpBoundingRect)) {
                    collectPowerUp(player, entity)
                }
            }
        }
    }

    private fun collectPowerUp(player: Entity, powerUpEntity: Entity) {
        val powerUpCmp = powerUpEntity[PowerUpComponent.mapper]
        require(powerUpCmp!=null) {"Entity |entity| must have a PowerUpComponent. entity=$powerUpEntity"}

        LOG.debug { "Picking up power up type ${powerUpCmp.type}" }

        when(powerUpCmp.type) {
            PowerUpType.SPEED_1 -> {
                player[MoveComponent.mapper]?.let {it.speed.y += BOOST_1_SPEED_GAIN}
            }
            PowerUpType.SPEED_2 -> {
                player[MoveComponent.mapper]?.let {it.speed.y += BOOST_2_SPEED_GAIN}
            }
            PowerUpType.LIFE -> {
                player[PlayerComponent.mapper]?.let {it.life = min(it.maxLife, it.life+ LIFE_GAIN) }
            }
            PowerUpType.SHIELD -> {
                player[PlayerComponent.mapper]?.let {it.shield +=  min(it.maxShield, it.shield+ SHIELD_GAIN )}
            }
            else -> {
                LOG.error { "Unknown power up type ${powerUpCmp.type}" }
            }
        }

        gameEventManager.dispatchEvent(
            GameEventType.COLLECT_POWER_UP,
            GameEventCollectPowerUpEvent.apply { //notify all listener that listen to power up event
                this.player = player
                this.type = powerUpCmp.type
            }
        )

        powerUpEntity.addComponent<RemoveComponent>(engine)

    }
}
