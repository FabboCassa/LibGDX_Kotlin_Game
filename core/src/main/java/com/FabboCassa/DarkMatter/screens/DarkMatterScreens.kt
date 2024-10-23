package com.FabboCassa.DarkMatter.screens

import com.FabboCassa.DarkMatter.DarkMatterMain
import com.FabboCassa.DarkMatter.ecs.audio.AudioService
import com.FabboCassa.DarkMatter.ecs.event.GameEventManager
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage

abstract class DarkMatterScreens (
    val game: DarkMatterMain,
    val gameViewport: Viewport = game.gameViewport,
    val uiViewport: Viewport = game.uiViewport,
    val gameEventManager: GameEventManager = game.gameEventManager,
    val assets: AssetStorage = game.assets,
    val audioService: AudioService = game.audioService,
    val preferences: Preferences = game.preferences
) : KtxScreen{
    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }
}
