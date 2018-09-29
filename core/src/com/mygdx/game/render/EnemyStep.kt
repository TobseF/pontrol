package com.mygdx.game.render

import com.badlogic.gdx.Gdx
import com.mygdx.game.GameState
import com.mygdx.game.GameState.GridPoint
import com.mygdx.game.GameState.HackPoint.State.Blocked
import com.mygdx.game.GameState.HackPoint.State.Change
import com.mygdx.game.GameState.Virus
import java.util.*

class EnemyStep {

    private lateinit var state: GameState

    fun step(state: GameState) {
        this.state = state
        if (state.viruses.size == 0) {
            spwanVirus(2)
        }
        val step = 1 / 3F // step per second
        val killed = arrayListOf<Virus>()
        for (virus in state.viruses) {
            virus.stepProgress += (step * deltaTime())
            if (virus.stepProgress >= 1) {
                virus.stepProgress = 0F

                val hackPoint = virus.next.getHackPoint()
                if (hackPoint != null) {
                    if (hackPoint.state == Blocked && hackPoint.alliance == virus.alliance) {
                        killed.add(virus)
                        hackPoint.line.hackPoints.remove(hackPoint)
                    } else if (hackPoint.state == Change) {
                        virus.alliance = hackPoint.alliance
                    }
                }
                virus.current.i = virus.current.i + 1
                virus.next.i = virus.next.i + 1
            }
        }
        state.viruses.removeAll(killed)
        val lines = Array(state.size()) { it }.toMutableList().apply { shuffle() }

        if (killed.isNotEmpty()) {
            for (i in 0..Math.min(lines.size - 1, killed.size)) {
                spwanVirus(lines[i])
            }
        }
    }

    fun spwanVirus(line: Int) {
        state.viruses.add(newVirus(line))
    }

    fun spwanVirus() {
        spwanVirus(state.lines.random().i)
    }


    private fun deltaTime() = Gdx.graphics.deltaTime

    private fun newVirus(lineNumber: Int): Virus {
        val line = state.lines[lineNumber]
        val i = 0
        val a = GridPoint(i, line)
        val b = GridPoint(i, line)
        return Virus(randomAlliance(), a, b)
    }

    fun Array<GameState.BaseLine>.random() = this[(0 until this.size).random()]

    private fun IntRange.random() =
            Random().nextInt((endInclusive + 1) - start) + start


    private fun randomAlliance(): GameState.Alliance {
        return GameState.Alliance.values()[(0 until 3).random()]
    }
}