package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.app.KtxScreen
import ktx.log.Logger
import ktx.log.logger

private val LOG: Logger = logger<FirstScreen>()

class FirstScreen(game: TutorialMain): TutorialScreens(game) {
    override fun show() {
        LOG.debug { "First screen shown" }
    }

    override fun render(delta: Float) {
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            game.setScreen<SecondScreen>()
        }
    }
}
