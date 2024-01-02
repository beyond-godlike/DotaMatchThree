package com.example.dotamatchthree.presentation.ui

import android.graphics.Point
import com.example.dotamatchthree.data.Constants.cellWidth
import com.example.dotamatchthree.data.Constants.drawX
import com.example.dotamatchthree.data.Constants.drawY
import com.example.dotamatchthree.data.Hero
import com.example.dotamatchthree.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel<GameState>(
    initialState = GameState.IDLE
)  {
    var board: Array<Array<Hero>>  = Array(9) { row ->
        Array(9) { col ->
            Hero(row.toFloat(), col.toFloat(), 0)
        }
    }
    private var topBoard: Array<Hero> =  Array(9) { row ->
        Hero(0.0f, row.toFloat(), 0)
    }

    private val search: ArrayList<ArrayList<Point>> = ArrayList()

    var oldX = 0f
    var oldY = 0f
    var posI = 0
    var posJ = 0
    var direction: String? = null
    var newPosI = 0
    var newPosJ = 0

    var move = false
    private var swapIndex = 8
    private var dropStop = true

    private var level = arrayOf(
        intArrayOf(7, 8, 9, 10, 4, 3, 1, 2, 4),
        intArrayOf(6, 1, 2, 4, 1, 3, 11, 11, 6),
        intArrayOf(3, 4, 2, 5, 2, 3, 3, 1, 2),
        intArrayOf(2, 1, 3, 3, 1, 5, 4, 5, 4),
        intArrayOf(2, 2, 5, 1, 1, 2, 6, 11, 2),
        intArrayOf(11, 3, 6, 1, 1, 3, 11, 2, 2),
        intArrayOf(3, 6, 4, 2, 2, 4, 1, 11, 11),
        intArrayOf(2, 1, 6, 3, 3, 6, 5, 3, 4),
        intArrayOf(1, 6, 11, 2, 2, 3, 3, 5, 11)
    )

    init {
        for (i in level.indices) {
            for (j in level[0].indices) {
                level[i][j] = generateNewJewels()
            }
        }

        for (i in level.indices) {
            for (j in level[0].indices) {
                board[i][j] = Hero(
                    drawX + cellWidth * j,
                    drawY + cellWidth * i,
                    level[i][j]
                )
            }
        }

        updateState(GameState.UPDATE)
    }

    private fun generateNewJewels(): Int {
        return (Math.random() * 11 + 1).toInt()
    }


    fun updateGame() {
        when (state.value) {
            GameState.SWAPPING -> swap()
            GameState.CHECKSWAPPING -> {
                fillCrushing()
                if (search.isEmpty()) {
                    swap()
                } else updateState(GameState.CRUSHING)
            }


            GameState.CRUSHING -> {
                var i = 0
                while (i < search.size) {
                    var j = 0
                    while (j < search[i].size) {
                        board[search[i][j].x][search[i][j].y].color = 0
                        j++
                    }
                    search.removeAt(i)
                    i--
                    i++
                }

                if (search.isEmpty()) {
                    updateState(GameState.UPDATE)
                }
            }

            GameState.UPDATE -> {
                drop()
                fillTopBoard()
                fillCrushing()
                if (search.isEmpty()) {
                    if (!checkDrop()) {
                        updateState(GameState.IDLE)
                    }
                } else {
                    updateState(GameState.CRUSHING)
                }
                dropStop = false
            }
            GameState.IDLE ->{}
        }
    }

    private fun swap() {
        if (swapIndex > 0) {
            when (direction) {
                "right" -> {
                    board[posI][posJ + 1].posX -= cellWidth / 8
                    board[posI][posJ].posX += cellWidth / 8
                }

                "left" -> {
                    board[posI][posJ - 1].posX += cellWidth / 8
                    board[posI][posJ].posX -= cellWidth / 8
                }

                "up" -> {
                    board[posI - 1][posJ].posY += cellWidth / 8
                    board[posI][posJ].posY -= cellWidth / 8
                }

                "down" -> {
                    board[posI + 1][posJ].posY -= cellWidth / 8
                    board[posI][posJ].posY += cellWidth / 8
                }
            }
            swapIndex--
        } else {
            val j: Hero = board[posI][posJ]
            board[posI][posJ] = board[newPosI][newPosJ]
            board[newPosI][newPosJ] = j
            board[posI][posJ].posX = ((posJ * cellWidth + drawX))
            board[posI][posJ].posY = ((posI * cellWidth + drawY))
            board[newPosI][newPosJ].posX = ((newPosJ * cellWidth + drawX))
            board[newPosI][newPosJ].posY = ((newPosI * cellWidth + drawY))
            swapIndex = 8
            if(state.value == GameState.SWAPPING) {
                updateState(GameState.CHECKSWAPPING)
            }
            else {
                updateState(GameState.IDLE)
            }
        }
    }

    private fun fillCrushing() {
        search.clear()
        for (i in board.indices) {
            var j = 0
            while (j < board[0].size) {
                if (board[i][j].color > 0) {
                    var k = j + 1
                    while (k < board.size && board[i][k].color == board[i][j].color) {
                        k++
                    }
                    if (k - j >= 3) {
                        for (m in j until k) {
                            search.add(ArrayList())
                            search[search.size - 1].add(Point(i, m))
                        }
                    }
                    j = k - 1
                }
                j++
            }

        }
        run {
            var i = 0
            while (i < board.size) {
                var j = 0
                while (j < board[0].size) {
                    if (board[i][j].color > 0) {
                        var k = 0
                        while (i + k < board.size && board[i][j].color == board[i + k][j].color) {
                            k++
                        }
                        if (k >= 3) {
                            search.add(ArrayList())
                            for (m in 0 until k) {
                                search[search.size - 1].add(Point(i + m, j))
                            }
                            i += k - 1
                        }
                    }
                    j++
                }
                i++
            }
        }
        var i = 0
        while (i < search.size) {
            if (!allowCrushing(search[i])) {
                search.removeAt(i)
                i--
            }
            i++
        }
    }

    private fun allowCrushing(points: ArrayList<Point>): Boolean {
        var allow = true
        for (i in 0 until  points.size) {
            if (points[i].x < board.size - 1) {
                if (board[points[i].x + 1][points[i].y].color == 0) allow = false
            }
        }
        return allow
    }

    private fun checkDrop(): Boolean {
        var drop = false
        for (jewels in board) {
            for (j in jewels) {
                if (j.color == 0) {
                    drop = true
                    break
                }
            }
        }
        return drop
    }

    private fun fillTopBoard() {
        for (j in topBoard.indices) {
            if (topBoard[j].color == 0) {
                topBoard[j].color = generateNewJewels()
                if (j > 0) {
                    if (topBoard[j].color == topBoard[j - 1].color) {
                        topBoard[j].color = topBoard[j].color % 11 + 1
                    }
                }
            }
        }
    }

    private fun drop() {
        for (k in topBoard.indices) {
            if (board[0][k].color == 0) {
                topBoard[k].posY += cellWidth / 8
                if (drawY.toInt() - topBoard[k].posY < cellWidth / 8) {
                    board[0][k].color = topBoard[k].color
                    topBoard[k].color = 0
                    topBoard[k].posY = board[0][k].posY - cellWidth
                    topBoard[k].posX = drawX.toInt() + k * cellWidth
                    dropStop = true
                }
            }
        }

        for (i in 0 until board.size - 1) {
            for (j in board[0].indices) {
                if (board[i][j].color > 0) {
                    if (board[i + 1][j].color == 0) {
                        board[i][j].posY += cellWidth / 8
                        if (drawY.toInt() + (i + 1) * cellWidth - board[i][j].posY < cellWidth / 8) {
                            board[i + 1][j].color = board[i][j].color
                            board[i][j].color = 0
                            board[i][j].posY = drawY + i * cellWidth
                            board[i][j].posX = drawX + j * cellWidth
                            dropStop = true
                        }
                    }
                }
            }
        }
    }


}
