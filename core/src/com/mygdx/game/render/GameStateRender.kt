package com.mygdx.game.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.GameState
import com.mygdx.game.GameState.*
import com.mygdx.game.GameState.HackPoint.State.*

class GameStateRender(val state: GameState) {
    private var height = Gdx.graphics.height.toFloat()
    private var width = Gdx.graphics.width.toFloat()
    private var pointRaster = RenderOptions.pointRaster
    private val lineGap = height / state.size()

    private val renderer = ShapeRenderer()

    object GameColor {
        val neutral = Color.LIGHT_GRAY!!
        val selected = Color.YELLOW!!
    }

    fun updateSize() {
        height = Gdx.graphics.height.toFloat()
        width = Gdx.graphics.width.toFloat()
    }

    fun render(state: GameState) {
        updateSize()

        renderer.begin(ShapeRenderer.ShapeType.Filled)

        var y = lineGap / 2
        for (line in state.lines) {
            drawLine(line, y)
            y += lineGap
        }
        for (virus in state.viruses) {
            renderer.color = virus.alliance.toColor()
            var x = virus.current.i * pointRaster
            var y = virus.current.line.i * lineGap + (lineGap / 2)
            x += virus.stepProgress * pointRaster
            renderCircle(x, y, 12F)

        }
        for (connection in state.connections) {
            renderer.color = GameColor.neutral
            renderer.rectLine(connection.a.toVec(), connection.b.toVec(), 5F)
        }

        renderer.end()
    }

    private fun HackPoint.toVec(): Vector2 {
        val x = this.i * pointRaster + (pointRaster / 2)
        val y = this.line.i * lineGap + (lineGap / 2)
        return Vector2(x, y)
    }

    private fun Alliance.toColor(): Color {
        return when (this) {
            Alliance.Red -> Color.RED
            Alliance.Blue -> Color.BLUE
            Alliance.Green -> Color.GREEN
            Alliance.Neutral -> Color.LIGHT_GRAY
        }
    }

    private fun drawLine(line: BaseLine, y: Float) {
        renderer.color = GameColor.neutral
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
        renderer.color = hackPoint.alliance.toColor()
        when (hackPoint.state) {
            Free -> {
                renderCircle(x, y, radius)
            }
            Line -> {
                renderer.color = if (hackPoint.seleced) GameColor.selected else GameColor.neutral
                renderHackPointLine(x, y)
            }
            Change -> {
                renderHackPointChange(x, y)
            }
        }

    }

    fun renderCircle(x: Float, y: Float, radius: Float) {
        renderer.circle(state.position + x, y, radius)
    }

    fun renderHackPointLine(x: Float, y: Float) {
        val height = 32F
        val width = 12F
        val dX = width / 2
        val dY = height / 2
        renderer.rect(state.position + x - dX, y - dY, width, height)
    }

    fun renderHackPointChange(x: Float, y: Float) {
        val side = 26F
        val dY = side / 2
        renderer.rect(state.position + x, y - dY - 6, 0f, 0F, side, side, 1F, 1F, 45F)
    }

}