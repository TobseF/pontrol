package com.mygdx.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
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
        val line = state.lines[1]
        line.hackPoints.add(GameState.HackPoint(3, line))
        line.hackPoints.add(GameState.HackPoint(5, line))
        renderer = GameStateRender(state)
        enemy = EnemyStep()
        input = InputHandler(state)
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
