package com.FabboCassa.Tutorial.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class TransformComponent: Component, Pool.Poolable {

    val position = Vector3() //opengl is 3d graphic language
    val size = Vector2(1f,1f)
    var rotation = 0f

    override fun reset() { //reset data added for this component
        position.set(Vector3.Zero)
        size.set(1f,1f)
        rotation = 0f  //need to be resetted otherwuise might have strange behavior
    }

    companion object { //not store any state
        val mapper = mapperFor<TransformComponent>() //helps to make exceess of tranformComponent fast 0logn to 0log1
    }
}
