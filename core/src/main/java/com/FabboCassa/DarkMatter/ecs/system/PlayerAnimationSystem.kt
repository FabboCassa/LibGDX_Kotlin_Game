package com.FabboCassa.DarkMatter.ecs.system

import com.FabboCassa.DarkMatter.ecs.component.FacingComponent
import com.FabboCassa.DarkMatter.ecs.component.FacingDirection
import com.FabboCassa.DarkMatter.ecs.component.GraphicComponent
import com.FabboCassa.DarkMatter.ecs.component.PlayerComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerAnimationSystem(
    private val defaultRegion: TextureRegion,
    private val leftRegion: TextureRegion,
    private val rightRegion: TextureRegion
): IteratingSystem(allOf(PlayerComponent::class, FacingComponent::class, GraphicComponent::class).get()),
    EntityListener {
    private var lastDirection = FacingDirection.DEFAULT

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facingComponent = entity[FacingComponent.mapper]
        require(facingComponent != null) { "Entity |entity| must have a FacingComponent. entity=$entity" }
        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| must have a GraphicComponent. entity=$entity" }

        if(facingComponent.direction == lastDirection && graphic.sprite.texture != null) {
            //texture already  set and direction not changed
            return
        }

        lastDirection = facingComponent.direction
        val region = when (facingComponent.direction) {
            FacingDirection.LEFT -> leftRegion
            FacingDirection.RIGHT -> rightRegion
            else -> defaultRegion
        }
        graphic.setSpriteRegion(region)
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family,this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        entity[GraphicComponent.mapper]?.setSpriteRegion(defaultRegion)
    }

    override fun entityRemoved(entity: Entity?) = Unit

}
