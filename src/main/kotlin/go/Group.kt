package go

/**
 * A connected group/string of stones.
 */
internal data class Group(
    val color: Color,
    internal val coordinates: MutableSet<Coordinate>,
    internal val liberties: MutableSet<Coordinate>
)
