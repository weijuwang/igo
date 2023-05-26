package go

/**
 * Constructs a coordinate from two integers.
 */
infix fun Int.xy(y: Int) =
    Coordinate(this, y)