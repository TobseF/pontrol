package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.mygdx.game.GameState.GridPoint
import com.mygdx.game.GameState.HackPoint.State.*
import com.mygdx.game.GameState.Virus
import java.util.*

class EnemyStep {

    private lateinit var state: GameState

    fun step(state: GameState) {
        this.state = state
        if (state.viruses.size == 0) {
            spwanVirus(1)
        }
        val step = GameOption.enemySpeed
        val killed = arrayListOf<Virus>()
        for (virus in state.viruses) {
            virus.stepProgress += (step * deltaTime())
            if (virus.stepProgress >= 1) {
                virus.stepProgress = 0F
                val hackPoint = virus.next.getHackPoint()
                var followLine = true
                if (hackPoint != null) {
                    if (hackPoint.state == Blocked && hackPoint.alliance == virus.alliance) {
                        killed.add(virus)
                        hackPoint.line.hackPoints.remove(hackPoint)
                    } else if (hackPoint.state == Change) {
                        virus.alliance = hackPoint.alliance
                    } else if (hackPoint.state == Line && hackPoint.isConnected()) {
                        followLine = false
                        if (virus.isOnConnection()) {
                            virus.current.i = virus.next.i
                            virus.current.line = virus.next.line
                            virus.next.i = virus.next.i + 1
                            virus.next.line = virus.next.line
                        } else {
                            virus.current.i = virus.next.i
                            val destination = hackPoint.nextOnConnection()
                            virus.next.i = destination.i
                            virus.next.line = destination.line
                        }
                    }
                }
                if (followLine) {
                    virus.current.i = virus.current.i + 1
                    virus.next.i = virus.next.i + 1
                    if (virus.current.i == state.end) {
                        killed.add(virus)
                        if (virus.isFriendly()) {
                            state.points++
                        } else {
                            state.lives--
                            if (state.lives <= 0) {
                                state.lives = 0
                                println("Game Over")
                            }
                        }
                    }
                }
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
        //spwanVirus(1)
    }

    private fun deltaTime() = Gdx.graphics.deltaTime

    private fun newVirus(lineNumber: Int): Virus {
        val line = state.lines[lineNumber]
        val a = GridPoint(0, line)
        val b = GridPoint(1, line)
        return Virus(randomAlliance(), a, b)
    }

    fun Array<GameState.BaseLine>.random() = this[(0 until this.size).random()]

    private fun IntRange.random() =
            Random().nextInt((endInclusive + 1) - start) + start


    private fun randomAlliance(): GameState.Alliance {
        return GameState.Alliance.values()[(0 until 4).random()]
    }
}