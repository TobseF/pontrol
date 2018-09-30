package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.mygdx.game.GameState.HackPoint.State.*
import com.mygdx.game.GameState.Virus

class EnemyStep(val state: GameState) {
    val virusFacotry = VirusFactory(state)

    fun step(state: GameState) {
        if (state.viruses.size == 0) {
            virusFacotry.spwanVirus()
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

        if (killed.isNotEmpty()) {
            virusFacotry.killedVirus(killed.size)

        }
        virusFacotry.step()
    }



    private fun deltaTime() = Gdx.graphics.deltaTime


}