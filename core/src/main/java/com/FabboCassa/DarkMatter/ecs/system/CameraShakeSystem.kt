package com.FabboCassa.DarkMatter.ecs.system

import com.FabboCassa.DarkMatter.ecs.event.GameEvent
import com.FabboCassa.DarkMatter.ecs.event.GameEventListener
import com.FabboCassa.DarkMatter.ecs.event.GameEventManager
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import ktx.collections.GdxArray

private class CameraShake : Pool.Poolable {

    var maxDistortion = 0f
    var duration = 0f
    lateinit var camera: Camera
    private var storeCameraPos = true ///flag to know if is to store
    private val originalCameraPos = Vector3()
    private var currentDuration = 0f

    //shaking logic
    fun update(deltaTime: Float): Boolean {
        if (storeCameraPos) {
            storeCameraPos = false
            originalCameraPos.set(camera.position)
        }
        if (currentDuration < duration) {
            val currentPower =
                maxDistortion * ((duration - currentDuration) / duration)//value between 0 and maxDistortion

            camera.position.x = originalCameraPos.x + MathUtils.random(-1f, 1f) * currentPower
            camera.position.y = originalCameraPos.y + MathUtils.random(-1f, 1f) * currentPower

            camera.update()

            currentDuration += deltaTime
            return false
        }
        //shake ended
        camera.position.set(originalCameraPos)
        camera.update()
        return true
    }

    override fun reset() {
        maxDistortion = 0f
        duration = 0f
        currentDuration = 0f
        storeCameraPos = true
        originalCameraPos.set(Vector3.Zero)
    }

}

private class CameraShakePool(private val gameCamera: Camera) : Pool<CameraShake>() {
    override fun newObject() = CameraShake().apply {
        this.camera = gameCamera
    }
}

class CameraShakeSystem(
    camera: Camera,
    private val gameEventManager: GameEventManager
) : EntitySystem(), GameEventListener {

    private val cameraShakePool = CameraShakePool(camera)
    private val activeShakes = GdxArray<CameraShake>()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.PlayerHit::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(this)
    }

    override fun update(deltaTime: Float) {
        if(!activeShakes.isEmpty) {
            val shake = activeShakes.first()
            if (shake.update(deltaTime)) {
                activeShakes.removeIndex(0)
                cameraShakePool.free(shake)
            }
        }
    }

    override fun onEvent(event: GameEvent) {
        if (activeShakes.size < 4) { //bigger number longer shaking
            activeShakes.add(cameraShakePool.obtain().apply {
                duration = 0.25f
                maxDistortion = 0.25f
            })
        }
    }
}
