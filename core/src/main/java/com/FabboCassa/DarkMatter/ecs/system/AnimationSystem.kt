package com.FabboCassa.DarkMatter.ecs.system

import com.FabboCassa.DarkMatter.ecs.component.Animation2D
import com.FabboCassa.DarkMatter.ecs.component.AnimationComponent
import com.FabboCassa.DarkMatter.ecs.component.AnimationType
import com.FabboCassa.DarkMatter.ecs.component.GraphicComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.GdxRuntimeException
import ktx.ashley.allOf
import ktx.ashley.get
import java.util.EnumMap
import ktx.log.logger

private val LOG = logger<AnimationSystem>()

class AnimationSystem(
    private val atlas: TextureAtlas //because we have only 1 atlas
): IteratingSystem(allOf(AnimationComponent::class, GraphicComponent::class).get()), EntityListener {

    private val animationCache = EnumMap<AnimationType, Animation2D>(AnimationType::class.java) //create a cache because the call to always take frame are expensive

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        entity[AnimationComponent.mapper]?.let { animationComponent ->
            animationComponent.animation = getAnimation(animationComponent.type) //get animation for the component
            val frame = animationComponent.animation.getKeyFrame(animationComponent.stateTime) //get the frame
            entity[GraphicComponent.mapper]?.setSpriteRegion(frame) //set frame
        }
    }

    private fun getAnimation(type: AnimationType): Animation2D {
        var animation = animationCache[type] //check if we already have the animation in the cache
        if (animation == null) {
            //load animation
            var regions = atlas.findRegions(type.atlasKey) //get the regions from the atlas
            if (regions.isEmpty) {
                LOG.error { "There are no regions for animation ${type.atlasKey}" }
                regions = atlas.findRegions("error")
                if (regions.isEmpty) throw GdxRuntimeException("There are no error regions in the atlas")
            }else {
                LOG.debug { "Adding animation of type $type with ${regions.size} regions" }
            }
            animation = Animation2D(type, regions, type.playMode, type.speedRate)
            animationCache[type] = animation //add the animation to the cache
        }
        return animation
    }

    override fun entityRemoved(entity: Entity) = Unit

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationComponent = entity[AnimationComponent.mapper]
        require(animationComponent!=null) {"Entity |entity| must have a AnimationComponent. entity = $entity"}
        val graphic = entity[GraphicComponent.mapper]
        require(graphic!=null) {"Entity |entity| must have a GraphicComponent. entity = $entity"}

        if (animationComponent.type == AnimationType.NONE) {
            LOG.error { "No type specified for animation component $animationComponent for |entity| entity=$entity" }
            return
        }

        if (animationComponent.type == animationComponent.animation.type) {
            //animation is correct  set -> update it
            animationComponent.stateTime +=deltaTime
        } else {
            animationComponent.stateTime = 0f
            animationComponent.animation= getAnimation(animationComponent.type)
        }

        val frame = animationComponent.animation.getKeyFrame(animationComponent.stateTime)
        graphic.setSpriteRegion(frame)
    }
}
