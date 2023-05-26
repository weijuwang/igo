package go

import kotlin.math.abs

/**
 * A two-dimensional coordinate point.
 */
data class Coordinate (val column: Int, val row: Int) {
    /**
     * Returns whether two coordinates are orthogonally adjacent.
     */
    infix fun isAdjacentTo(other: Coordinate) =
        (this.row == other.row && abs(this.column - other.column) == 1)
        || (this.column == other.column && abs(this.row - other.row) == 1)

    /**
     * Returns all orthogonally adjacent coordinates that have positive [row]s and [column]s and are within the specified bounds.
     *
     * TODO Change [Coordinate.adjacentCoordinates] to be precomputed
     */
    fun adjacentCoordinates(width: Int, height: Int) =
        arrayOf(
            0 xy -1,
            0 xy +1,
            -1 xy 0,
            +1 xy 0
        )
            // Apply offsets
            .map { this + it }
            // Remove coordinates that are out-of-bounds
            .filter { it.row in 0..height && it.column in 0..width }

    /**
     * Returns the sum of two coordinates by adding their [row] and [column] values.
     */
    operator fun plus(other: Coordinate) =
        Coordinate(this.row + other.row, this.column + other.column)

    override fun toString() =
        "($column,$row)"
}
