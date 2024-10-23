package com.FabboCassa.DarkMatter.screens

import com.FabboCassa.DarkMatter.DarkMatterMain
import com.FabboCassa.DarkMatter.UNIT_SCALE
import com.FabboCassa.DarkMatter.V_WIDTH
import com.FabboCassa.DarkMatter.ecs.asset.MusicAsset
import com.FabboCassa.DarkMatter.ecs.component.AnimationComponent
import com.FabboCassa.DarkMatter.ecs.component.AnimationType
import com.FabboCassa.DarkMatter.ecs.component.AttachComponent
import com.FabboCassa.DarkMatter.ecs.component.FacingComponent
import com.FabboCassa.DarkMatter.ecs.component.GraphicComponent
import com.FabboCassa.DarkMatter.ecs.component.MoveComponent
import com.FabboCassa.DarkMatter.ecs.component.PlayerComponent
import com.FabboCassa.DarkMatter.ecs.component.TransformComponent
import com.FabboCassa.DarkMatter.ecs.event.GameEvent
import com.FabboCassa.DarkMatter.ecs.event.GameEventListener
import com.FabboCassa.DarkMatter.ecs.system.DAMAGE_AREA_HEIGHT
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.Logger
import ktx.log.logger
import kotlin.math.min

private val LOG: Logger = logger<GameScreen>()
private const val MAX_DELTA_TIME =
    1 / 20f //we define the minimum frame per sec, can't go under 20 frame per second

class GameScreen(game: DarkMatterMain, private val engine: Engine = game.engine) : DarkMatterScreens(game), GameEventListener {


    override fun show() {
        LOG.debug { "First screen shown" }

        gameEventManager.addListener(GameEvent.PlayerDeath::class, this)

        audioService.play(MusicAsset.GAME)
        spawnPlayer()
        engine.entity {
            with<TransformComponent> {
                size.set(V_WIDTH.toFloat(), DAMAGE_AREA_HEIGHT)
            }
            with<AnimationComponent> { type = AnimationType.DARK_MATTER }
            with<GraphicComponent>()
        }
    }

    override fun hide() {
        super.hide()
        gameEventManager.removeListener(this)
    }

    private fun spawnPlayer() {
        val playerShip = engine.entity {
            with<TransformComponent> { //dimesion
                setInitialPosition(4.5f, 8f, -1f)
            }
            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }
        engine.entity {
            with<TransformComponent>()
            with<AttachComponent> {
                entity = playerShip
                offset.set(1f * UNIT_SCALE, -6f * UNIT_SCALE)
            }
            with<GraphicComponent>()
            with<AnimationComponent> { type = AnimationType.FIRE }
        }
    }


    override fun render(delta: Float) {
        (game.batch as SpriteBatch).renderCalls = 0
        engine.update(
            min(
                MAX_DELTA_TIME,
                delta
            )
        )// min amount 20, if goes over takes delta otherwie max_delta, avoid "spiral of death"
        audioService.update()
        LOG.debug { "Render calls: ${(game.batch as SpriteBatch).renderCalls}" }

    }

    override fun dispose() {
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.PlayerDeath -> {
                spawnPlayer()
            }

            GameEvent.CollectPowerUp -> TODO()
            GameEvent.PlayerHit -> TODO()
        }
    }
}
