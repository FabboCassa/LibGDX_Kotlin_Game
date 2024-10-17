package com.FabboCassa.Tutorial.screens

import com.FabboCassa.Tutorial.TutorialMain
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.app.KtxScreen

abstract class TutorialScreens (
    val game: TutorialMain,
    val batch: Batch = game.batch
) : KtxScreen{
}
