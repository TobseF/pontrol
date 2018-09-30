package com.mygdx.game.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.GameState
import com.mygdx.game.GameState.*
import com.mygdx.game.GameState.HackPoint.State.*


class GameStateRender(private val state: GameState) {

    private var height = Gdx.graphics.height.toFloat()
    private var width = Gdx.graphics.width.toFloat()
    private var pointRaster = RenderOptions.pointRaster
    private val lineGap = height / state.size()

    private val renderer = ShapeRenderer()

    private val batch = SpriteBatch()
    private val scoreFont: BitmapFont
    private val defaultFont: BitmapFont

    init {
        scoreFont = BitmapFont(Gdx.files.internal("segment7.fnt"), false)
        defaultFont = BitmapFont(Gdx.files.internal("sans.fnt"), false)
    }

    object GameColor {
        val neutral = Color.LIGHT_GRAY!!
        val selected = Color.YELLOW!!
        val end = Color.RED.cpy()!!
        val shadow = Color.BLACK.cpy()!!

        init {
            end.a = 0.6F
            shadow.a = 0.7F
        }
    }

    private fun updateSize() {
        height = Gdx.graphics.height.toFloat()
        width = Gdx.graphics.width.toFloat()
    }

    fun render(state: GameState) {
        updateSize()

        renderer.begin(Filled)
        renderer.setAutoShapeType(true)

        renderLines(state)
        renderConnections(state)
        renderViruses(state)
        renderEnd(state)
        renderer.end()

        batch.begin()
        renderScore(state)
        batch.end()

        renderGameOver()

    }

    private fun renderGameOver() {
        if (state.lives == 0) {
            reenableAlpha()
            renderer.apply {
                begin(Filled)
                color = GameColor.shadow
                rect(0F, 0F, width, height)
                end()
            }
            batch.begin()
            defaultFont.draw(batch, "GAME OVER", width / 2 - 300, height / 2 + 100)
            defaultFont.draw(batch, "Score: " + state.points, width / 2 - 305, height / 2 - 50)
            batch.end()
        }
    }

    private fun reenableAlpha() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    private fun renderScore(state: GameState) {
        scoreFont.draw(batch, "${state.lives} ${state.points}", width - 300, height - 80)

    }

    private fun renderEnd(state: GameState) {
        val margin = 15F
        val y = (state.end * pointRaster) + (pointRaster / 2)
        renderer.color = GameColor.end
        renderer.rectLine(Vector2(y, margin), Vector2(y, height - margin), 12F)
    }

    private fun renderLines(state: GameState) {
        var y = lineGap / 2
        for (line in state.lines) {
            drawLine(line, y)
            y += lineGap
        }
    }

    private fun renderViruses(state: GameState) {
        for (virus in state.viruses) {
            renderer.color = virus.alliance.toColor()
            if (virus.isOnConnection()) {
                val x = (virus.current.i * pointRaster) + (pointRaster / 2)
                val y = virus.current.line.i * lineGap + (lineGap / 2)
                var connectionD = virus.distanceLine() * lineGap
                if (virus.current.line.i > virus.next.line.i) {
                    connectionD = -connectionD
                }
                val yD = virus.stepProgress * connectionD
                renderCircle(x, y + yD, 12F)
            } else {
                var x = (virus.current.i * pointRaster) + (pointRaster / 2)
                val y = virus.current.line.i * lineGap + (lineGap / 2)
                x += virus.stepProgress * pointRaster
                renderCircle(x, y, RenderOptions.virusRadius)
            }
        }
    }

    private fun renderConnections(state: GameState) {
        for (connection in state.connections) {
            renderer.color = GameColor.neutral
            renderer.rectLine(connection.a.toVec(), connection.b.toVec(), 5F)
        }
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
            Alliance.Yellow -> Color.YELLOW
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
        renderer.color = GameColor.neutral
        when (hackPoint.state) {
            Line -> {
                renderer.color = if (hackPoint.seleced) GameColor.selected else GameColor.neutral
                renderHackPointLine(x, y)
            }
            Change -> {
                renderer.color = hackPoint.alliance.toColor()
                renderHackPointChange(x, y)
            }
            Blocked -> {
                renderer.color = hackPoint.alliance.toColor()
                renderBlocked(x, y)
            }
        }
    }

    private fun renderCircle(x: Float, y: Float, radius: Float) {
        renderer.circle(state.position + x, y, radius)
    }

    private fun renderHackPointLine(x: Float, y: Float) {
        val height = 32F
        val width = 12F
        val dX = width / 2
        val dY = height / 2
        renderer.rect(state.position + x - dX, y - dY, width, height)
    }

    private fun renderHackPointChange(x: Float, y: Float) {
        val side = 26F
        val dY = side / 2
        renderer.rect(state.position + x, y - dY - 6, 0f, 0F, side, side, 1F, 1F, 45F)
    }

    private fun renderBlocked(x: Float, y: Float) {
        val width = 28F
        val d = width / 2
        renderer.rect(state.position + x - d, y - d, width, width)
    }

}