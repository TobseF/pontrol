package com.mygdx.game

import com.mygdx.game.GameState.Alliance.Neutral
import com.mygdx.game.GameState.HackPoint.State.Free

class GameState {
    var lives = 0
    var position = 0f
    val lines: Array<BaseLine>
    val viruses = arrayListOf<Virus>()

    fun size() = lines.size

    init {
        lines = Array(5, { BaseLine(it) })
    }

    enum class Alliance { Red, Blue, Green, Neutral }

    data class Virus(val alliance: Alliance, val current: GridPoint, val next: GridPoint) {
        var stepProgess = 0F
        val visited = arrayListOf<GridPoint>()

        init {
            visited.add(current)
        }
    }

    open class GridPoint(val i: Int = 0, val line: BaseLine)

    val level = Level()

    class BaseLine(val i: Int = 0) {
        val hackPoints = arrayListOf<HackPoint>()
    }

    class Connection(a: HackPoint, b: HackPoint)

    class HackPoint(i: Int, line: BaseLine, val state: State = Free, val alliance: Alliance = Neutral) : GridPoint(i, line) {
        enum class State { Free, Blocked, Line }
    }

    class Level(lines: Int = 5)
}