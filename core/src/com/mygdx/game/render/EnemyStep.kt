package com.mygdx.game.render

import com.badlogic.gdx.Gdx
import com.mygdx.game.GameState
import com.mygdx.game.GameState.GridPoint
import com.mygdx.game.GameState.HackPoint.State.Blocked
import java.util.*

class EnemyStep {

    private lateinit var state: GameState

    fun step(state: GameState) {
        this.state = state
        if (state.viruses.size == 0) {
            state.viruses.add(newVirus())
        }
        val step = 1 / 3F // step per second
        for (virus in state.viruses) {
            virus.stepProgress += (step * deltaTime())
            if (virus.stepProgress >= 1) {
                virus.stepProgress = 0F
                val hackPoint = virus.next.getHackPoint()
                if (hackPoint != null) {
                    if (hackPoint.state == Blocked) {
                        state.viruses.remove(virus)
                    }
                }
            }
        }
    }

    private fun deltaTime() = Gdx.graphics.deltaTime

    private fun newVirus(): GameState.Virus {
        val line = state.lines.random()
        val i = 0
        val a = GridPoint(i, line)
        val b = GridPoint(i + 1, line)
        return GameState.Virus(randomAlliance(), a, b)
    }

    fun Array<GameState.BaseLine>.random() = this[(0 until this.size).random()]

    private fun IntRange.random() =
            Random().nextInt((endInclusive + 1) - start) + start


    private fun randomAlliance(): GameState.Alliance {
        return GameState.Alliance.values()[(1..3).random()]
    }
}