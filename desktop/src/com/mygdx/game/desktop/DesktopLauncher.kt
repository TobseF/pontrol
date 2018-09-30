package com.mygdx.game.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.mygdx.game.MyGdxGame

fun main(arg: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.samples = 3
    config.width = 1280
    config.height = 720
    config.initialBackgroundColor = Color.DARK_GRAY
    config.title = "Pontrol"
    config.addIcon("icon.png", Files.FileType.Internal)
    LwjglApplication(MyGdxGame(), config)
}