package com.mygdx.game

import com.mygdx.game.GameState.HackPoint.State.Blocked

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

    enum class Alliance { Red, Blue, Green,
        Yellow // is our friend
        ;

        fun nextEnemy(): Alliance {
            val index = if (ordinal + 1 > Alliance.values().size - 2) 0 else ordinal + 1
            return Alliance.values()[index]
        }
    }

    data class Virus(var alliance: Alliance, val current: GridPoint, val next: GridPoint) {
        var stepProgress = 0F
        val visited = arrayListOf<GridPoint>()

        init {
            visited.add(current)
        }

        fun isOnConnection(): Boolean {
            return current.line.i != next.line.i
        }

        fun distanceLine(): Int {
            return Math.max(current.line.i, next.line.i) - Math.min(current.line.i, next.line.i)
        }

        override fun toString() = "($current-$next):$alliance"
    }

    open class GridPoint(var i: Int = 0, var line: BaseLine) {
        fun getHackPoint(): HackPoint? {
            return line.getHackPoint(i)
        }

        override fun toString() = "(${line.i},$i)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GridPoint

            if (i != other.i) return false
            if (line != other.line) return false

            return true
        }

        override fun hashCode(): Int {
            var result = i
            result = 31 * result + line.hashCode()
            return result
        }
    }

    val level = Level()

    class BaseLine(val i: Int = 0) {
        val hackPoints = arrayListOf<HackPoint>()

        fun getHackPoint(i: Int): HackPoint? {
            return hackPoints.find { it.i == i }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as BaseLine
            if (i != other.i) return false
            return true
        }

        override fun hashCode(): Int {
            return i
        }
    }

    class Connection(var a: HackPoint, var b: HackPoint) {
        init {
            a.connection = this
            b.connection = this
        }

        fun otherLine(x: HackPoint): HackPoint {
            return if (a.line.i == x.line.i) b else a
        }

        fun distance(): Int {
            return Math.max(a.line.i, b.line.i) - Math.min(a.line.i, b.line.i)
        }

        override fun toString(): String {
            return "$a -- $b"
        }
    }

    class HackPoint(i: Int, line: BaseLine, var state: State = Blocked, var alliance: Alliance = Alliance.Green) : GridPoint(i, line) {
        var seleced = false
        var connection: Connection? = null

        fun isConnected() = connection != null

        fun nextOnConnection(): HackPoint {
            if (connection != null) {
                return connection!!.otherLine(this)
            }
            throw IllegalStateException()
        }

        enum class State { Blocked, Change, Line;

            fun next(): State {
                val index = if (ordinal + 1 > State.values().size - 1) 0 else ordinal + 1
                return State.values()[index]
            }
        }

        override fun toString(): String = "(${line.i},$i $state)"
    }

    class Level(lines: Int = 5)
}