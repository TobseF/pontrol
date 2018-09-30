package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.game.GameState.HackPoint
import com.mygdx.game.GameState.HackPoint.State.Line
import com.mygdx.game.render.EnemyStep
import com.mygdx.game.render.GameStateRender
import com.mygdx.game.render.InputHandler

class MyGdxGame : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var img: Texture
    private lateinit var state: GameState
    private lateinit var renderer: GameStateRender
    private lateinit var enemy: EnemyStep
    private lateinit var input: InputHandler

    override fun create() {
        batch = SpriteBatch()
        img = Texture("toast.png")
        state = GameState()
        initGameState()
        renderer = GameStateRender(state)
        enemy = EnemyStep()
        input = InputHandler(state, enemy)
    }

    private fun initGameState() {
        val line1 = state.lines[1]
        val line2 = state.lines[0]
        val point1 = HackPoint(1, line1, Line)
        line1.hackPoints.add(point1)
        val point2 = HackPoint(1, line2, Line)
        line2.hackPoints.add(point2)
        state.connections.add(GameState.Connection(point2, point1))
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        //batch.draw(img, 0f, 0f)
        batch.end()
        enemy.step(state)


        renderer.render(state)


        val shapeRenderer = ShapeRenderer()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.GREEN
        shapeRenderer.end()
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}
