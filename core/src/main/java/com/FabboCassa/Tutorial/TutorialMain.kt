package com.FabboCassa.Tutorial

import com.FabboCassa.Tutorial.screens.FirstScreen
import com.FabboCassa.Tutorial.screens.SecondScreen
import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.log.Logger
import ktx.log.logger


private val LOG: Logger = logger<TutorialMain>()

class TutorialMain : KtxGame<KtxScreen>() {

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG //needed to see log
        LOG.debug { "Create game instance" }
        addScreen(FirstScreen(this))
        addScreen(SecondScreen(this))
        setScreen<FirstScreen>()
    }
}
