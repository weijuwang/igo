package go

import java.util.UUID

/**
 * A game of Go.
 *
 * TODO Ko, superko
 */
data class Game (val ruleset: Ruleset, val width: Int, val height: Int) {
    /**
     * The status of all cells on the board.
     *
     * Index with row number first, then column (`cells[x][y]`).
     */
    private val cells = Array(width) { Array(height) { Cell() } }

    /**
     * The number of pieces of each color that has been captured.
     */
    private val capturedStones = mutableMapOf(
        Color.BLACK to 0,
        Color.WHITE to 0
    )

    /**
     * The list of moves that have been made on the board so far.
     * `null` signifies a pass.
     */
    private val moveList = mutableListOf<Coordinate?>()

    /**
     * The player whose turn it currently is.
     */
    val playerToMove get() =
        if(moveList.size % 2 == 0)
            Color.BLACK
        else
            Color.WHITE

    /**
     * The set of all groups on the board, updated automatically by `move()` and `pass()`.
     */
    private val groups = mutableMapOf<UUID, Group>()

    /**
     * Returns the cell at [coordinate].
     */
    operator fun get(coordinate: Coordinate) =
        cells[coordinate.row][coordinate.column]

    /**
     * Returns the set of liberties a stone would have if placed at `coordinate`.
     */
    private fun libertiesOf(coordinate: Coordinate) =
        coordinate
            .adjacentCoordinates(width, height)
            .filter { this[it].empty }
            .toMutableSet()

    /**
     * Removes a stone from the board and updates necessary information.
     */
    private fun removeStone(coordinate: Coordinate) {
        // Add it back as a liberty to any adjacent groups
        coordinate
            .adjacentCoordinates(width, height)
            .map { groups[this[it].groupUUID]!! }
            .forEach {
                it.liberties += coordinate
            }

        val removedCell = this[coordinate]
        val removedStoneGroupUUID = removedCell.groupUUID

        // Remove it from its group
        groups[removedStoneGroupUUID]!!
            .coordinates -= coordinate

        // If the group has no more liberties, remove the group
        if(groups[removedStoneGroupUUID]!!.liberties.isEmpty()) {
            groups.remove(removedStoneGroupUUID)
        }

        // Remove it from the board
        removedCell.groupUUID = null
        removedCell.status = null
    }

    /**
     * Removes an entire group from the board and updates necessary information.
     *
     * If the group does not exist, [NullPointerException] is thrown.
     */
    private fun removeGroup(groupUUID: UUID) {
        groups[groupUUID]!!.coordinates
            .forEach { removeStone(it) }
    }

    /**
     * Places the next stone at [moveCoordinate].
     * Returns whether the move was legal.
     */
    fun move(moveCoordinate: Coordinate) =
        if (this[moveCoordinate].empty) {
            // Place the stone
            this[moveCoordinate].status = playerToMove

            // Update [groups]
            for ((adjacentGroupUUID, adjacentGroup) in groups) {
                if (!(
                    adjacentGroup.color == playerToMove
                    && moveCoordinate !in adjacentGroup.coordinates
                    && adjacentGroup.coordinates.any { it isAdjacentTo moveCoordinate }
                )) continue

                // If the stone is not yet part of a group
                if (this[moveCoordinate].groupUUID == null) {
                    // Make the cell's group ID the adjacent group's ID
                    this[moveCoordinate].groupUUID = adjacentGroupUUID
                    adjacentGroup.coordinates += moveCoordinate
                } else {
                    /* Otherwise merge the stone's group into the adjacent group */
                    val thisCellGroupUUID = this[moveCoordinate].groupUUID

                    // For each cell in the old group
                    for (coordinate in groups[thisCellGroupUUID]!!.coordinates) {
                        // Update the cell's group ID to be the adjacent group's ID
                        this[coordinate].groupUUID = adjacentGroupUUID
                        // Add the coordinate to the adjacent group
                        adjacentGroup.coordinates += coordinate
                    }

                    // Add liberties from old group
                    adjacentGroup.liberties += groups[thisCellGroupUUID]!!.liberties

                    // Remove the old group
                    groups.remove(thisCellGroupUUID)
                }

                // Placing the stone takes away a liberty
                adjacentGroup.liberties -= moveCoordinate
            }

            // If the stone still does not have a group at this point (meaning there are no adjacent groups), create a new group for it
            if (this[moveCoordinate].groupUUID == null) {
                this[moveCoordinate].groupUUID = UUID.randomUUID()
                groups[this[moveCoordinate].groupUUID!!] =
                    Group(
                        color = playerToMove,
                        coordinates = mutableSetOf(moveCoordinate),
                        liberties = mutableSetOf()
                    )
            }

            // Remove the placed stone's coordinate as a liberty from all groups
            // We only need to run through opposite-colored groups because we already went through adjacent groups of the same color
            groups
                .filter { (_, group) -> group.color isOpponentOf playerToMove }
                .map { (_, group) ->
                    group.liberties -= moveCoordinate
                }

            // Add new liberties
            groups[this[moveCoordinate].groupUUID!!]!!.liberties += libertiesOf(moveCoordinate)

            groups
                .filter { (_, capturedGroup) ->
                    capturedGroup.color isOpponentOf playerToMove
                    && capturedGroup.liberties.isEmpty()
                }
                .forEach { (capturedGroupUUID, capturedGroup) ->
                    // Update captured stones count
                    capturedStones[!playerToMove] = capturedStones[!playerToMove]!! + capturedGroup.coordinates.size
                    removeGroup(capturedGroupUUID)
                }

            this[moveCoordinate].groupUUID!!.let { groupUUID ->
                // Check if the move was suicide
                if (groups[groupUUID]!!.liberties.isEmpty()) {
                    removeGroup(groupUUID)

                    if (!ruleset.suicideAllowed) {
                        return false
                    }
                }
            }

            moveList += moveCoordinate
            true
        } else false

    /**
     * Pass and place no stones.
     */
    fun pass() {
        moveList += null

        // TODO two-pass to end game
    }

    /**
     * Print debug info, assuming a 19x19 board.
     */
    internal fun debugPrint(showGroups: Boolean = true) {
        fun printlnIndent(text: Any, indent: Int, indentSize: Int = 2) {
            println(" ".repeat(indent * indentSize) + text)
        }

        println("-".repeat(80))
        println("${width}x$height　${ruleset.name}ルール　${playerToMove.kanji}番　${moveList.size}手　アゲハマ：黒${capturedStones[Color.BLACK]}白${capturedStones[Color.WHITE]}")

        println(JapaneseLanguage.space.toString().repeat(12) + JapaneseLanguage.numeral(10).toString().repeat(9))
        println(JapaneseLanguage.space.toString().repeat(2) +
            ((1..10) + (1..9))
                .joinToString("") { JapaneseLanguage.numeral(it).toString() }
        )
        println(
            (0 until height).joinToString("\n") { row ->
                (
                    if (row >= 10)
                        JapaneseLanguage.numeral(10)
                    else
                        JapaneseLanguage.space
                ).toString() +
                JapaneseLanguage.numeral(row % 10 + 1).toString() +
                (0 until width).joinToString("") { column ->
                    when (this[column xy row].status) {
                        null -> JapaneseLanguage.dot
                        else -> this[column xy row].color.kanji
                    }.toString()
                }
            }
        )

        println(moveList.joinToString(" "))

        if (showGroups) {
            for (color in Color.values()) {
                for ((groupUUID, group) in groups.filter { (_, group) -> group.color == color }) {
                    printlnIndent(
                        "$groupUUID ${group.coordinates.size}${color.kanji}石：${
                            group.coordinates.joinToString(" ")
                        }", 1
                    )
                    printlnIndent(
                        "${group.liberties.size}呼吸点：${
                            group.liberties.joinToString(" ")
                        }", 2
                    )
                }
            }
        }
        println("-".repeat(80))
    }

    internal fun makeMoves(vararg moves: Coordinate?) {
        for (move in moves) {
            if (move == null) {
                pass()
            } else {
                move(move)
            }
        }
    }
}