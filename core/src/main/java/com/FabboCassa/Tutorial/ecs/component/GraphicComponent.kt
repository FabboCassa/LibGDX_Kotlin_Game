package com.FabboCassa.Tutorial.ecs.component

import com.FabboCassa.Tutorial.UNIT_SCALE
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class GraphicComponent: Component, Pool.Poolable {

    val sprite = Sprite()

    override fun reset() {
        sprite.texture = null //don't reuse previous texture
        sprite.setColor(1f,1f,1f,1f)
    }

    fun setSpriteRegion(defaultRegion: TextureRegion) {
        sprite.run {
            setRegion(defaultRegion)
            setSize(
                texture.width * UNIT_SCALE,
                texture.height * UNIT_SCALE
            ) //convert in our unit scale
            setOriginCenter()
        }
    }

    companion object {
        val mapper = mapperFor<GraphicComponent>()
    }
}
