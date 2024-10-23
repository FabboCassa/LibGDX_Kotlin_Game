package com.FabboCassa.DarkMatter.ecs.system

import com.FabboCassa.DarkMatter.ecs.component.AttachComponent
import com.FabboCassa.DarkMatter.ecs.component.GraphicComponent
import com.FabboCassa.DarkMatter.ecs.component.RemoveComponent
import com.FabboCassa.DarkMatter.ecs.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get

class AttachSystem: EntityListener, IteratingSystem(allOf(AttachComponent::class, TransformComponent::class, GraphicComponent::class).get()) {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }
    override fun entityAdded(entity: Entity) = Unit

    override fun entityRemoved(removedEntity: Entity) {
        entities.forEach {entity ->
            entity[AttachComponent.mapper]?.let { attach ->
                if (attach.entity == removedEntity) {
                    entity.addComponent<RemoveComponent>(engine)
                }
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val attach = entity[AttachComponent.mapper]
        require(attach!=null) {"Entity |entity| must have a AttachComponent. entity = $entity"}
        val graphic = entity[GraphicComponent.mapper]
        require(graphic!=null) {"Entity |entity| must have a GraphicComponent. entity = $entity"}
        val transform = entity[TransformComponent.mapper]
        require(transform!=null) {"Entity |entity| must have a TransformComponent. entity = $entity"}

        //update position attach entities
        attach.entity[TransformComponent.mapper]?.let { attachTransform ->
            transform.interpolationPosition.set(
                attachTransform.interpolationPosition.x + attach.offset.x,
                attachTransform.interpolationPosition.y + attach.offset.y,
                transform.position.z
            )
        }

        //update graphic alpha value
        attach.entity[GraphicComponent.mapper]?.let { attachGraphic ->
            graphic.sprite.setAlpha(attachGraphic.sprite.color.a)
        }
    }
}
