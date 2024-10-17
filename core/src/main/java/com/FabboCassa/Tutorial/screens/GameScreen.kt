package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.FabboCassa.Tutorial.UNIT_SCALE
import com.FabboCassa.Tutorial.ecs.component.GraphicComponent
import com.FabboCassa.Tutorial.ecs.component.TransformComponent
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import ktx.graphics.use
import ktx.log.Logger
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(game: TutorialMain) : TutorialScreens(game) {

    private val playerTexture =
        Texture(Gdx.files.internal("graphics/ship_base.png")) //complete texture with a lor of things
    private val viewport = FitViewport(9f, 16f) //viewport of the world
    private val player = game.engine.entity {
        with<TransformComponent> {
            position.set(1f, 1f, 0f)
        }
        with<GraphicComponent>{
            sprite.run {
                setRegion(playerTexture)
                setSize(texture.width* UNIT_SCALE, texture.height * UNIT_SCALE) //convert in our unit scale
                setOriginCenter()
            }
        }
    }

    override fun show() {
        LOG.debug { "First screen shown" }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {

        engine.update(delta) //time of frame? needed to not have more movement if we have more frame per sec

        viewport.apply() //apply before start rendering if we have multiple viewports
        batch.use(viewport.camera.combined) {batch ->//ktx extension
            player[GraphicComponent.mapper]?.let { graphic ->
                player[TransformComponent.mapper]?.let { transform ->
                    graphic.sprite.run {
                        rotation = transform.rotation
                        setBounds(transform.position.x, transform.position.y, transform.size.x, transform.size.y)
                        draw(batch)
                    }
                }
            }
        }
    }

    override fun dispose() {
        playerTexture.dispose()
    }
}
