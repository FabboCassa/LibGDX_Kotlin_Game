package com.FabboCassa.Tutorial

import com.FabboCassa.Tutorial.ecs.system.DamageSystem
import com.FabboCassa.Tutorial.ecs.system.DebugSystem
import com.FabboCassa.Tutorial.ecs.system.MoveSystem
import com.FabboCassa.Tutorial.ecs.system.PlayerAnimationSystem
import com.FabboCassa.Tutorial.ecs.system.PlayerInputSystem
import com.FabboCassa.Tutorial.ecs.system.RemoveSystem
import com.FabboCassa.Tutorial.ecs.system.RenderSystem
import com.FabboCassa.Tutorial.screens.GameScreen
import com.FabboCassa.Tutorial.screens.TutorialScreens
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.log.Logger
import ktx.log.logger


private val LOG: Logger = logger<TutorialMain>()
const val UNIT_SCALE = 1 / 16f
const val V_WIDTH = 9
const val V_HEIGHT = 16

class TutorialMain : KtxGame<TutorialScreens>() {

    val graphicsAtlas by lazy { TextureAtlas(Gdx.files.internal("graphics/graphics.atlas")) }

    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat()) //viewport of the world
    val batch: Batch by lazy { SpriteBatch() } //initialized when needed
    val engine: Engine by lazy { PooledEngine().apply {
        addSystem(PlayerInputSystem(gameViewport))
        addSystem(MoveSystem())
        addSystem(DamageSystem())
        addSystem(PlayerAnimationSystem(graphicsAtlas.findRegion("ship_base"),graphicsAtlas.findRegion("ship_left"),graphicsAtlas.findRegion("ship_right")))
        addSystem(RenderSystem(batch, gameViewport))
        addSystem(RemoveSystem())
        addSystem(DebugSystem())
    } } //pooled stops Garbage Collector because entities are pooled and don't trigger garbage

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG //needed to see log
        LOG.debug { "Create game instance" }
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }

    override fun dispose() { //to track number of batch sprites to improve memory usage
        super.dispose()
        LOG.debug { "Sprites in batch : ${(batch as SpriteBatch).maxSpritesInBatch}" }
        batch.dispose()
        graphicsAtlas.dispose()
    }
}
