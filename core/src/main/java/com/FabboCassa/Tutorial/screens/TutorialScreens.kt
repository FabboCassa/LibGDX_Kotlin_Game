package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen

abstract class TutorialScreens (
    val game: TutorialMain,
    val batch: Batch = game.batch,
    val engine: Engine= game.engine,
    val gameViewport: Viewport = game.gameViewport,
    val uiViewport: Viewport = game.uiViewport
) : KtxScreen{
    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }
}
