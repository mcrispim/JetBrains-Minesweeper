package minesweeper

const val mineChar = 'X'
const val markedChar = '*'
const val emptyChar = '/'
const val unexploredChar = '.'

class Minefield(private val rows: Int, private val cols: Int, nMines: Int) {
    val cells = Array(rows + 1) { Array<Char>(cols + 1) { unexploredChar } }
    private val emptyCells = mutableSetOf<Pair<Int, Int>>()
    val minedCells = mutableSetOf<Pair<Int, Int>>()
    var nMarkedRight = 0
    var nMarkedWrong = 0
    var unexplored = rows * cols

    init {
        // create a set with the empty cells
        for (i in 1..rows) {
            for (j in 1..cols) {
                emptyCells.add(Pair(i, j))
            }
        }
        // create the mines
        repeat (nMines) {
            val cell = emptyCells.random()
            val (row, col) = cell
            cells[row][col] = unexploredChar
            emptyCells.remove(cell)
            minedCells.add(cell)
        }
    }

    private fun minesAround(row: Int, col: Int): Int {
        var mines = 0
        for (cell in cellsAround(row, col)) {
            if (cell in minedCells) {                   // cell contains a mine
                mines++
            }
        }
        return mines
    }

    private fun cellsAround(row: Int, col: Int): MutableSet<Pair<Int, Int>> {
        val nearCells = mutableSetOf<Pair<Int, Int>>()
        val rowMin = if (row > 1) row - 1 else row
        val rowMax = if (row < rows) row + 1 else row
        val colMin = if (col > 1) col - 1 else col
        val colMax = if (col < cols) col + 1 else col
        var mines = 0
        for (i in rowMin..rowMax) {
            for (j in colMin..colMax) {
                if (i == row && j == col) {
                    continue
                }
                nearCells.add(Pair(i, j))
            }
        }
        return nearCells
    }

    fun print() {
        println()
        println(" │123456789│")
        println("—│—————————│")
        for (row in 1..rows) {
            print("${row}│")
            for (col in 1..cols) {
                print(cells[row][col])
            }
            println("│")
        }
        println("—│—————————│")
    }

    fun mark(row: Int, col: Int) {
        if (cells[row][col] == unexploredChar) {
            cells[row][col] = markedChar
            if (Pair(row, col) in minedCells) {
                nMarkedRight++
            } else {
                nMarkedWrong++
            }
        } else if (cells[row][col] == markedChar) {
            cells[row][col] = unexploredChar
            if (Pair(row, col) in minedCells) {
                nMarkedRight--
            } else {
                nMarkedWrong--
            }
        }
    }

    fun free(row: Int, col: Int) {
        unexplored--
        val around = minesAround(row, col)
        if (around == 0) {
            cells[row][col] = emptyChar
            for ((r, c) in cellsAround(row, col)) {
                if (cells[r][c] == unexploredChar || cells[r][c] == markedChar) {
                    free(r, c)
                }
            }
        } else {
            cells[row][col] = ('0'.code + around).toChar()
        }
    }
}

fun main() {
    println("How many mines do you want on the field? ")
    val mines = readLine()!!.toInt()
    val field = Minefield( 9, 9, mines)
    var boom = false
    field.print()
    while (field.nMarkedRight != mines  && !boom && field.unexplored > mines) {
        print("Set/unset mines marks or claim a cell as free: ")
        val colRowComm = readLine()!!.split(' ')
        val col = colRowComm[0].toInt()
        val row = colRowComm[1].toInt()
        when (colRowComm[2]) {
            "mine" -> {
                field.mark(row, col)
            }
            "free" -> {
                if (Pair(row, col) in field.minedCells) {
                    boom = true
                    for ((r, c) in field.minedCells) {
                        field.cells[r][c] = mineChar
                    }
                } else {
                    field.free(row, col)
                }
            }
        }
        field.print()
    }
    if (boom) {
        println("You stepped on a mine and failed!")
    } else {
        println("Congratulations! You found all the mines!")
    }
}