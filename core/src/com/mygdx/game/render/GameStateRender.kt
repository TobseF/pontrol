package com.mygdx.game.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.GameState
import com.mygdx.game.GameState.Alliance
import com.mygdx.game.GameState.BaseLine

class GameStateRender {
    private var height = Gdx.graphics.height.toFloat()
    private var width = Gdx.graphics.width.toFloat()
    private var pointRaster = RenderOptions.pointRaster

    private val renderer = ShapeRenderer()
    private lateinit var state: GameState

    object GameColor {
        val neutral = Color.LIGHT_GRAY!!
    }

    fun updateSize() {
        height = Gdx.graphics.height.toFloat()
        width = Gdx.graphics.width.toFloat()
    }

    fun render(state: GameState) {
        this.state = state
        updateSize()
        val lineGap = height / state.size()

        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = GameColor.neutral
        var y = lineGap / 2
        for (line in state.lines) {
            drawLine(line, y)
            y += lineGap
        }
        for (virus in state.viruses) {
            renderer.color = virus.alliance.virusColor()
            var x = virus.current.i * pointRaster
            var y = virus.current.line.i * lineGap + (lineGap / 2)
            x += virus.stepProgess * pointRaster
            renderCircle(x, y, 12F)

        }
        renderer.end()
    }

    private fun Alliance.virusColor(): Color {
        return when (this) {
            Alliance.Red -> Color.RED
            Alliance.Blue -> Color.BLUE
            Alliance.Green -> Color.GREEN
            Alliance.Neutral -> Color.LIGHT_GRAY
        }
    }

    private fun drawLine(line: BaseLine, y: Float) {
        renderer.rectLine(Vector2(0F, y), Vector2(width, y), 5F)
        val start = (pointRaster / 2).toInt()
        for (x in start until width.toInt() step pointRaster.toInt()) {
            renderCircle(x.toFloat(), y, 4F)
        }
        for (hackPoint in line.hackPoints) {
            drawHackPoint(hackPoint, y)
        }
    }

    private fun drawHackPoint(hackPoint: GameState.HackPoint, y: Float) {
        val x = (pointRaster * hackPoint.i) + (pointRaster / 2)
        val radius = 8F
        renderCircle(x, y, radius)
    }

    fun renderCircle(x: Float, y: Float, radius: Float) {
        renderer.circle(state.position + x, y, radius)
    }

}