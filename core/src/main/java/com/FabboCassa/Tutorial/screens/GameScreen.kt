package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.FabboCassa.Tutorial.UNIT_SCALE
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.graphics.use
import ktx.log.Logger
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(game: TutorialMain) : TutorialScreens(game) {

    private val texture =
        Texture(Gdx.files.internal("graphics/ship_base.png")) //complete texture with a lor of things
    private val sprite = Sprite(texture).apply {
        setSize(1f, 1f) //scale to 1 unit of the world
    } //a texture that can be "modified" rotate, change color... sprite is a texture region
    private val viewport = FitViewport(9f, 16f) //viewport of the world

    override fun show() {
        LOG.debug { "First screen shown" }
        sprite.setPosition(1f, 1f)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {

        viewport.apply() //apply before start rendering if we have multiple viewports
        batch.use(viewport.camera.combined) { //ktx extension
            sprite.draw(batch)
        }
    }

    override fun dispose() {
        batch.dispose()
        texture.dispose() //put in memory
    }
}
