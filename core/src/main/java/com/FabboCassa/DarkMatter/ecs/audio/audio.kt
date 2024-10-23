package com.FabboCassa.DarkMatter.ecs.audio

import com.FabboCassa.DarkMatter.ecs.asset.MusicAsset
import com.FabboCassa.DarkMatter.ecs.asset.SoundAsset
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Pool
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger
import java.util.EnumMap
import kotlin.math.max

private val LOG = logger<AudioService>()
private const val MAX_SOUND_INSTANCES = 50

interface AudioService {
    fun play(
        soundAsset: SoundAsset,
        volume: Float = 1f
    ) //soundAsset small audio, like for event, short piece of music(3-4 kb)

    fun play(
        musicAsset: MusicAsset,
        volume: Float = 1f,
        loop: Boolean = true
    ) //loop when ends restart, music is long audio file, usally 1 or 2 music asset

    fun pause()
    fun resume()
    fun stop(clearSounds: Boolean = true)
    fun update()

}

private class SoundRequest : Pool.Poolable {
    lateinit var soundAsset: SoundAsset
    var volume = 1f
    override fun reset() {
        volume = 1f
    }

}


private class SoundRequestPool : Pool<SoundRequest>() {
    override fun newObject() = SoundRequest()
}

class DefaultAudioService(private val assets: AssetStorage) : AudioService {
    private val soundCache =
        EnumMap<SoundAsset, Sound>(SoundAsset::class.java) //store in cache to have faster access in future
    private val soundRequestPool = SoundRequestPool()
    private val soundRequests = EnumMap<SoundAsset, SoundRequest>(SoundAsset::class.java)
    private var currentMusic: Music? = null
    private var currentMusicAsset: MusicAsset? = null

    override fun play(soundAsset: SoundAsset, volume: Float) {
        when {
            soundAsset in soundRequests -> { //check if is in queue
                //play sound only 1 with highest volume
                soundRequests[soundAsset]?.let { request ->
                    request.volume = max(request.volume, volume)
                }
            }

            soundRequests.size >= MAX_SOUND_INSTANCES -> {//limit max amount sound played in 1 frame
                LOG.debug { "Maximum sound request reached" }
                return
            }

            else -> {
                if(soundAsset.descriptor !in assets) {
                    LOG.error { "Trying to play audio not loaded" }
                    return
                } else if (soundAsset !in soundCache) {
                    soundCache[soundAsset] = assets[soundAsset.descriptor] //store in cache if not present
                }

                soundRequests[soundAsset] = soundRequestPool.obtain().apply { //add to queue
                    this.soundAsset = soundAsset
                    this.volume = volume
                }

            }
        }
    }

    override fun play(musicAsset: MusicAsset, volume: Float, loop: Boolean) {
        val musicDeferred = assets.loadAsync(musicAsset.descriptor)
        KtxAsync.launch {
            musicDeferred.join()//wait until loading done
            if(assets.isLoaded(musicAsset.descriptor)) {
                //check if loaded
                currentMusic?.stop()
                if(currentMusic != null) {
                    currentMusicAsset?.let { assets.unload(it.descriptor) }  //if music already playing we stop previous instant
                }
                currentMusicAsset = musicAsset
                currentMusic = assets[musicAsset.descriptor].apply {
                    this.volume = volume
                    this.isLooping = loop
                    play()
                }
            }
        }
    }

    override fun pause() {
        currentMusic?.pause()
    }

    override fun resume() {
        currentMusic?.play()
    }

    override fun stop(clearSounds: Boolean) {
        currentMusic?.stop()
        if (clearSounds) {
            soundRequests.clear()
        }
    }

    override fun update() {
        if(!soundRequests.isEmpty()) { //if sound request not empty, we iterate on it and play it
            soundRequests.values.forEach { request ->
                soundCache[request.soundAsset]?.play(request.volume)
                soundRequestPool.free(request)
                soundRequests.remove(request.soundAsset)
            }
        }
    }
}
