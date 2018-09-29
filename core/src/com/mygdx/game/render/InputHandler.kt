package com.mygdx.game.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.mygdx.game.GameState


class InputHandler(val state: GameState) {

    val maxTouchDistance = 15

    init {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {

                var height = Gdx.graphics.height
                val lineGap = height / state.size()
                val lineGapHalf = lineGap / 2
                val line = (state.size() - 1) - (y / lineGap)
                var deltaY = Math.abs(Math.floorMod((y), lineGap) - lineGapHalf)
                if (deltaY > lineGapHalf) {
                    deltaY -= lineGapHalf
                }

                val poinGap = RenderOptions.pointRaster.toInt()
                val poinGapHalf = (RenderOptions.pointRaster / 2).toInt()

                var width = Gdx.graphics.width
                var deltaX = Math.abs(Math.floorMod((x), poinGap) - poinGapHalf)
                val point = (x / poinGap)
                if (deltaX > lineGapHalf) {
                    deltaX -= lineGapHalf
                }
                if (deltaX <= maxTouchDistance && deltaY <= maxTouchDistance) {
                    addHackPoint(line, point)
                }

                return true // return true to indicate the event was handled
            }

            override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {
                // your touch up code here
                return true // return true to indicate the event was handled
            }
        }
    }

    private fun addHackPoint(line: Int, point: Int) {
        val baseLine = state.lines[line]
        baseLine.hackPoints.add(GameState.HackPoint(point, baseLine))
    }

}