package com.FabboCassa.Tutorial

import com.FabboCassa.Tutorial.screens.GameScreen
import com.FabboCassa.Tutorial.screens.TutorialScreens
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.log.Logger
import ktx.log.logger


private val LOG: Logger = logger<TutorialMain>()

class TutorialMain : KtxGame<TutorialScreens>() {

    val batch: Batch by lazy { SpriteBatch() } //initialized when needed

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG //needed to see log
        LOG.debug { "Create game instance" }
        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }
}
