package com.FabboCassa.DarkMatter.android

import android.os.Bundle
import com.FabboCassa.DarkMatter.DarkMatterMain
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

/** Launches the Android application.  */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(DarkMatterMain(), AndroidApplicationConfiguration().apply {

        })
    }
}
