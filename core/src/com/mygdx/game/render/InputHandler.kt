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
                var deltaY = Math.abs(Math.floorMod(y, lineGap) - lineGapHalf)
                if (deltaY > lineGapHalf) {
                    deltaY -= lineGapHalf
                }

                val poinGap = RenderOptions.pointRaster.toInt()
                val poinGapHalf = (RenderOptions.pointRaster / 2).toInt()

                var deltaX = Math.abs(Math.floorMod((x), poinGap) - poinGapHalf)
                val point = (x / poinGap)
                if (deltaX > lineGapHalf) {
                    deltaX -= lineGapHalf
                }
                if (deltaX <= maxTouchDistance && deltaY <= maxTouchDistance) {
                    addHackPoint(line, point, button)
                }

                return true
            }

            override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean {

                return true
            }
        }
    }

    private fun addHackPoint(line: Int, point: Int, button: Int) {
        val baseLine = state.lines[line]
        val existing = baseLine.hackPoints.find { it.i == point }
        if (existing != null) {
            if (button == 0 && existing.state == GameState.HackPoint.State.Line) {
                selectLine(existing)
            } else
                if (button == 0) {
                    existing.alliance = existing.alliance.next()
                } else {
                    existing.state = existing.state.next()
                }
        } else {
            baseLine.hackPoints.add(GameState.HackPoint(point, baseLine))
        }
    }

    private fun selectLine(existing: GameState.HackPoint) {
        if (existing.seleced) {
            state.selectedLine = null
        } else if (state.selectedLine != null) {
            state.connections.add(GameState.Connection(state.selectedLine!!, existing))
            state.selectedLine!!.seleced = false
            state.selectedLine = null
        } else {
            state.selectedLine = existing
            existing.seleced = true
            existing.alliance = GameState.Alliance.Neutral
        }
    }

}