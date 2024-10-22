package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.FabboCassa.Tutorial.ecs.asset.TextureAsset
import com.FabboCassa.Tutorial.ecs.asset.TextureAtlasAsset
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.logger

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: TutorialMain) : TutorialScreens(game) {

    override fun show() {
        val old = System.currentTimeMillis()
        //queue asset loading
        val assetRefs = gdxArrayOf(
            TextureAsset.values().map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { assets.loadAsync(it.descriptor) }
        ).flatten()


        //start loading screen transition

        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.debug{"Assets loaded in ${System.currentTimeMillis() - old} ms"}
            assetsLoaded()
        }

        //setup UI
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
        game.setScreen<GameScreen>()
        game.removeScreen<LoadingScreen>()
        dispose() //when switch from loading we don't need loading anymore
    }
}
