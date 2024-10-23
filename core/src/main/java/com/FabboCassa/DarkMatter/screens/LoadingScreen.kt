package com.FabboCassa.DarkMatter.screens

import com.FabboCassa.DarkMatter.DarkMatterMain
import com.FabboCassa.DarkMatter.ecs.asset.SoundAsset
import com.FabboCassa.DarkMatter.ecs.asset.TextureAsset
import com.FabboCassa.DarkMatter.ecs.asset.TextureAtlasAsset
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.logger

private val LOG = logger<LoadingScreen>()

class LoadingScreen(game: DarkMatterMain) : DarkMatterScreens(game) {

    override fun show() {
        val old = System.currentTimeMillis()
        //queue asset loading
        val assetRefs = gdxArrayOf(
            TextureAsset.values().map { assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { assets.loadAsync(it.descriptor) }
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
