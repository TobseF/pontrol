package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.mygdx.game.render.RenderOptions


class InputHandler(val state: GameState, val enemyStep: EnemyStep) {

    val maxTouchDistance = 15

    init {
        Gdx.input.inputProcessor = object : InputAdapter() {

            override fun keyTyped(character: Char): Boolean {
                if (character == 'v') {
                    // enemyStep.spwanVirus()
                }
                return true
            }

            override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {

                val height = Gdx.graphics.height
                val lineGap = height / state.size()
                val lineGapHalf = lineGap / 2
                val line = (state.size() - 1) - (y / lineGap)
                var deltaY = Math.abs(Math.floorMod(y, lineGap) - lineGapHalf)
                if (deltaY > lineGapHalf) {
                    deltaY -= lineGapHalf
                }

                val pointGap = RenderOptions.pointRaster.toInt()
                val pointGapHalf = (RenderOptions.pointRaster / 2).toInt()

                var deltaX = Math.abs(Math.floorMod((x), pointGap) - pointGapHalf)
                val point = (x / pointGap)
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
                    existing.alliance = existing.alliance.nextEnemy()
                } else {
                    existing.state = existing.state.next()
                }
        } else {
            baseLine.hackPoints.add(GameState.HackPoint(point, baseLine))
        }
    }

    private fun selectLine(secondPoint: GameState.HackPoint) {
        if (secondPoint.seleced) {
            state.selectedLine?.seleced = false
            state.selectedLine = null
        } else if (state.selectedLine != null) {
            val firstPoint = state.selectedLine!!
            if (secondPoint.isConnected()) {
                state.connections.remove(secondPoint.connection)
                state.selectedLine = null
                firstPoint.seleced = false
                firstPoint.connection = null
                secondPoint.connection = null
                secondPoint.seleced = false
            } else if (firstPoint.i == secondPoint.i) {
                // Make connection
                val connection = GameState.Connection(firstPoint, secondPoint)
                firstPoint.seleced = false
                state.connections.add(connection)
                state.selectedLine = null
            } else {
                state.selectedLine = secondPoint
                firstPoint.seleced = false
                secondPoint.seleced = true
            }
        } else {
            state.selectedLine = secondPoint
            secondPoint.seleced = true
        }
    }

}