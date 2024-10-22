package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.FabboCassa.Tutorial.ecs.event.GameEventManager
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage

abstract class TutorialScreens (
    val game: TutorialMain,
    val gameViewport: Viewport = game.gameViewport,
    val uiViewport: Viewport = game.uiViewport,
    val gameEventManager: GameEventManager = game.gameEventManager,
    val assets: AssetStorage = game.assets
) : KtxScreen{
    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }
}
