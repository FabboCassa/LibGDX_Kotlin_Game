package com.FabboCassa.DarkMatter

import com.FabboCassa.DarkMatter.ecs.asset.MusicAsset
import com.FabboCassa.DarkMatter.ecs.asset.TextureAsset
import com.FabboCassa.DarkMatter.ecs.asset.TextureAtlasAsset
import com.FabboCassa.DarkMatter.ecs.audio.DefaultAudioService
import com.FabboCassa.DarkMatter.ecs.event.GameEventManager
import com.FabboCassa.DarkMatter.ecs.system.AnimationSystem
import com.FabboCassa.DarkMatter.ecs.system.AttachSystem
import com.FabboCassa.DarkMatter.ecs.system.CameraShakeSystem
import com.FabboCassa.DarkMatter.ecs.system.DamageSystem
import com.FabboCassa.DarkMatter.ecs.system.DebugSystem
import com.FabboCassa.DarkMatter.ecs.system.MoveSystem
import com.FabboCassa.DarkMatter.ecs.system.PlayerAnimationSystem
import com.FabboCassa.DarkMatter.ecs.system.PlayerInputSystem
import com.FabboCassa.DarkMatter.ecs.system.PowerUpSystem
import com.FabboCassa.DarkMatter.ecs.system.RemoveSystem
import com.FabboCassa.DarkMatter.ecs.system.RenderSystem
import com.FabboCassa.DarkMatter.screens.DarkMatterScreens
import com.FabboCassa.DarkMatter.screens.LoadingScreen
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.Logger
import ktx.log.logger


private val LOG: Logger = logger<DarkMatterMain>()
const val UNIT_SCALE = 1 / 16f
const val V_WIDTH = 9
const val V_HEIGHT = 16
const val V_WIDTH_PIXELS = 135
const val V_HEIGHT_PIXELS = 240

class DarkMatterMain : KtxGame<DarkMatterScreens>() {

    val uiViewport = FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())

    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage() //default behaviour with 1 thread
    }

    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat()) //viewport of the world
    val batch: Batch by lazy { SpriteBatch() } //initialized when needed

    val gameEventManager = GameEventManager()

    val audioService by lazy { DefaultAudioService(assets) }
    val preferences by lazy { Gdx.app.getPreferences("dark-matter") }//pass name of preference, if exists load it otherwise create

    val engine: Engine by lazy {

        PooledEngine().apply {
            val graphicsAtlas = assets[TextureAtlasAsset.GAME_GRAPHICS.descriptor]
            val backgroundTexture = assets[TextureAsset.BACKGROUND.descriptor]

            addSystem(PlayerInputSystem(gameViewport))
            addSystem(MoveSystem())
            addSystem(PowerUpSystem(gameEventManager, audioService))
            addSystem(DamageSystem(gameEventManager))
            addSystem(CameraShakeSystem(gameViewport.camera, gameEventManager))
            addSystem(
                PlayerAnimationSystem(
                    graphicsAtlas.findRegion("ship_base"),
                    graphicsAtlas.findRegion("ship_left"),
                    graphicsAtlas.findRegion("ship_right")
                )
            )
            addSystem(AttachSystem())
            addSystem(AnimationSystem(graphicsAtlas))
            addSystem(
                RenderSystem(
                    batch,
                    gameViewport,
                    uiViewport,
                    backgroundTexture,
                    gameEventManager
                )
            )
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
        }
    } //pooled stops Garbage Collector because entities are pooled and don't trigger garbage

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG //needed to see log
        LOG.debug { "Create game instance" }
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }

    override fun dispose() { //to track number of batch sprites to improve memory usage
        super.dispose()
        LOG.debug { "Sprites in batch : ${(batch as SpriteBatch).maxSpritesInBatch}" }
        MusicAsset.values().forEach {
            LOG.debug { "Recount $it : ${assets.getReferenceCount(it.descriptor)}" }
        }
        batch.dispose()
        assets.dispose()
    }
}
