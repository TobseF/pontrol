package com.mygdx.game

import com.mygdx.game.GameState.Alliance.Neutral
import com.mygdx.game.GameState.HackPoint.State.Free

class GameState {
    var lives = 0
    var position = 0f
    val lines: Array<BaseLine>
    val connections = arrayListOf<Connection>()
    val viruses = arrayListOf<Virus>()
    var selectedLine: HackPoint? = null


    fun size() = lines.size

    init {
        lines = Array(5, { BaseLine(it) })
    }

    enum class Alliance { Red, Blue, Green, Neutral;

        fun next(): Alliance {
            val index = if (ordinal + 1 > Alliance.values().size - 1) 0 else ordinal + 1
            return Alliance.values()[index]
        }
    }

    data class Virus(var alliance: Alliance, val current: GridPoint, val next: GridPoint) {
        var stepProgress = 0F
        val visited = arrayListOf<GridPoint>()

        init {
            visited.add(current)
        }

        override fun toString() = "($current-$next):$alliance"
    }

    open class GridPoint(var i: Int = 0, val line: BaseLine) {
        fun getHackPoint(): HackPoint? {
            return line.getHackPoint(i)
        }

        override fun toString() = "(${line.i},$i)"
    }

    val level = Level()

    class BaseLine(val i: Int = 0) {
        val hackPoints = arrayListOf<HackPoint>()

        fun getHackPoint(i: Int): HackPoint? {
            return hackPoints.find { it.i == i }
        }
    }

    class Connection(var a: HackPoint, var b: HackPoint)

    class HackPoint(i: Int, line: BaseLine, var state: State = Free, var alliance: Alliance = Neutral) : GridPoint(i, line) {
        var seleced = false

        enum class State { Free, Blocked, Line, Change;

            fun next(): State {
                val index = if (ordinal + 1 > State.values().size - 1) 0 else ordinal + 1
                return State.values()[index]
            }
        }

        override fun toString(): String = "($i,${line.i} $state - $state)"
    }

    class Level(lines: Int = 5)
}