package go

import java.util.UUID

/**
 * Information about an individual cell on a Go board.
 *
 * If [status] is `null`, the cell is empty.
 */
@Suppress("EqualsOrHashCode")
data class Cell (
    internal var status: Color? = null,
    internal var groupUUID: UUID? = null
) {
    /**
     * Returns `true` if [status] is `null`.
     */
    val empty get() = status == null

    /**
     * Returns the color of the stone on this cell.
     * Only use this function if you are certain the cell is not [empty].
     *
     * If [empty] is true, [NullPointerException] is thrown.
     */
    val color get() = status!!

    /**
     * Returns `true` if both cells have the same status -- i.e. both empty, both black, or both white.
     */
    override fun equals(other: Any?) =
        when (other) {
            is Cell -> this.status == other.status
            else -> false
        }
}