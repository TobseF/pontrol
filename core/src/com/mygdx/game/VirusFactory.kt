package com.mygdx.game

import com.badlogic.gdx.Gdx
import java.util.*

class VirusFactory(val state: GameState) {

    private fun deltaTime() = Gdx.graphics.deltaTime

    private val nextVirusMin = 0.8F
    private var nextVirusMax = 7F

    private var time = 0F
    private var nextVirus = 6F
    var killedTracker = 0

    private val timeOuts = FloatArray(state.size(), { 0F })

    init {
        for (i in 0 until timeOuts.size) {
            timeOuts[i] = 0F
        }
    }

    fun step() {
        time += deltaTime()
        for (i in 0 until timeOuts.size) {
            timeOuts[i] = timeOuts[i] + deltaTime()
        }
        if (time >= nextVirus) {
            spwanVirus()

            nextVirus = ((nextVirusMax * Math.random()) + nextVirusMin).toFloat()
            time = 0F
        }
    }

    fun killedVirus(killed: Int) {
        killedTracker++
        if (killedTracker >= 5) {
            killedTracker = 0
            nextVirusMax -= 0.5F
        }
        val lines = Array(state.size()) { it }.toMutableList().apply { shuffle() }
        for (i in 0..Math.min(lines.size - 1, killed)) {
            //spwanVirus()
        }
    }


    fun spwanVirus() {
        val newVirus = newVirus()
        if (newVirus != null) {
            timeOuts[newVirus.current.line.i] = 0F
            state.viruses.add(newVirus)
        }
    }

    private fun newVirus(): GameState.Virus? {

        var rows = mutableListOf<Int>()
        for (i in 0 until timeOuts.size) {
            val timeout = timeOuts[i]
            if (timeout >= nextVirusMin) {
                rows.add(i)
            }
        }
        if (rows.isEmpty()) {
            return null
        } else {
            val lineNum = (0 until rows.size).random()
            val line = state.lines[lineNum]
            val a = GameState.GridPoint(0, line)
            val b = GameState.GridPoint(1, line)
            return GameState.Virus(randomAlliance(), a, b)
        }
    }

    private fun IntRange.random() =
            Random().nextInt((endInclusive + 1) - start) + start


    private fun randomAlliance(): GameState.Alliance {
        return GameState.Alliance.values()[(0 until 4).random()]
    }


    fun Array<GameState.BaseLine>.random() = this[(0 until this.size).random()]

}