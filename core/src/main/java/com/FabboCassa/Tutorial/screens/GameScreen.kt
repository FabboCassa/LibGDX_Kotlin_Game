package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import ktx.graphics.use
import ktx.log.Logger
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(game: TutorialMain) : TutorialScreens(game) {

    private val texture =
        Texture(Gdx.files.internal("graphics/ship_base.png")) //complete texture with a lor of things
    private val sprite = Sprite(texture) //a texture that can be "modified" rotate, change color... sprite is a texture region

    override fun show() {
        LOG.debug { "First screen shown" }
        sprite.setPosition(1f, 1f)
    }

    override fun render(delta: Float) {

        batch.use { //ktx extension
            sprite.draw(batch)
        }
    }

    override fun dispose() {
        batch.dispose()
        texture.dispose() //put in memory
    }
}
