package com.FabboCassa.Tutorial

import com.FabboCassa.Tutorial.screens.GameScreen
import com.FabboCassa.Tutorial.screens.TutorialScreens
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.log.Logger
import ktx.log.logger


private val LOG: Logger = logger<TutorialMain>()
const val UNIT_SCALE = 1 / 16f

class TutorialMain : KtxGame<TutorialScreens>() {

    val batch: Batch by lazy { SpriteBatch() } //initialized when needed
    val engine: Engine by lazy { PooledEngine() } //pooled stops Garbage Collector because entities are pooled and don't trigger garbage

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
    }
}
