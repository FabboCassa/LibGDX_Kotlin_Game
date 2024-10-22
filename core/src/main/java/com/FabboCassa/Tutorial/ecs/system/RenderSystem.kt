package com.FabboCassa.Tutorial.ecs.system

import com.FabboCassa.Tutorial.ecs.component.GraphicComponent
import com.FabboCassa.Tutorial.ecs.component.PowerUpType
import com.FabboCassa.Tutorial.ecs.component.TransformComponent
import com.FabboCassa.Tutorial.ecs.event.GameEvent
import com.FabboCassa.Tutorial.ecs.event.GameEventCollectPowerUpEvent
import com.FabboCassa.Tutorial.ecs.event.GameEventListener
import com.FabboCassa.Tutorial.ecs.event.GameEventManager
import com.FabboCassa.Tutorial.ecs.event.GameEventType
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<RenderSystem>()

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    backgroundTexture: Texture,
    private val gameEventManager: GameEventManager
) : GameEventListener, SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
) {

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEventType.COLLECT_POWER_UP, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEventType.COLLECT_POWER_UP, this)
    }


    private val backgroundTexture = Sprite(backgroundTexture.apply {
        setWrap(
            Texture.TextureWrap.Repeat,
            Texture.TextureWrap.Repeat
        ) //set background to repeat (infinite scroll around)
    })

    private val backgroundScrollSpeed = Vector2(0.03f, -0.25f)

    override fun update(deltaTime: Float) {
        uiViewport.apply()

        batch.use(uiViewport.camera.combined) {
            backgroundTexture.run {
                backgroundScrollSpeed.y = min(-0.25f, backgroundScrollSpeed.y + deltaTime * (1f / 10f)) //goi back to original speed in 10 seconds
                scroll(backgroundScrollSpeed.x * deltaTime, backgroundScrollSpeed.y * deltaTime)
                draw(batch)
            }
        }
        forceSort()
        gameViewport.apply()

        batch.use(gameViewport.camera.combined) {
            //entity rendering
            super.update(deltaTime)
        }

    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        //render entities
        val transform = entity[TransformComponent.mapper]
        require(transform != null) {
            "Entity |entity| must have a TransformComponent. entity=$entity"
        }

        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) {
            "Entity |entity| must have a GraphicComponent. entity=$entity"
        }

        if (graphic.sprite.texture == null) {
            LOG.error { "Entity has no texture for rendering. entity=$entity" }
            return
        }

        graphic.sprite.run {
            rotation = transform.rotation
            setBounds(
                transform.interpolationPosition.x,
                transform.interpolationPosition.y,
                transform.size.x,
                transform.size.y
            )
            draw(batch)
        }

    }

    override fun onEvent(type: GameEventType, data: GameEvent?) {
        if (type == GameEventType.COLLECT_POWER_UP) {
            val eventData = data as GameEventCollectPowerUpEvent
            if (eventData.type == PowerUpType.SPEED_1) {
                backgroundScrollSpeed.y -= 0.25f
            }
            if (eventData.type == PowerUpType.SPEED_2) {
                backgroundScrollSpeed.y -= 0.5f
            }
        }
    }
}
