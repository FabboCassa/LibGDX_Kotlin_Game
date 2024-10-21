package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.FabboCassa.Tutorial.ecs.component.FacingComponent
import com.FabboCassa.Tutorial.ecs.component.GraphicComponent
import com.FabboCassa.Tutorial.ecs.component.MoveComponent
import com.FabboCassa.Tutorial.ecs.component.PlayerComponent
import com.FabboCassa.Tutorial.ecs.component.TransformComponent
import com.badlogic.gdx.math.MathUtils
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.Logger
import ktx.log.logger

private val LOG: Logger = logger<GameScreen>()

class GameScreen(game: TutorialMain) : TutorialScreens(game) {


    override fun show() {
        LOG.debug { "First screen shown" }
        engine.entity {
            with<TransformComponent> {
                position.set(4.5f,8f,0f)
            }
            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<FacingComponent>()
        }
    }


    override fun render(delta: Float) {
        engine.update(delta) //time of frame? needed to not have more movement if we have more frame per sec

    }

    override fun dispose() {
    }
}
