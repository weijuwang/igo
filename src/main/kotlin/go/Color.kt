package go

/**
 * A stone color, either [BLACK] or [WHITE].
 */
enum class Color (val charRepresentation: Char, val kanji: Char) {
    BLACK('X', kanji = '黒'),
    WHITE('O', kanji = '白');

    /**
     * The color of a player's opponent.
     * ```
     * assert_eq(!Color.BLACK, Color.WHITE)
     * assert_eq(!Color.WHITE, Color.BLACK)
     * ```
     */
    operator fun not() =
        when (this) {
            BLACK -> WHITE
            WHITE -> BLACK
        }

    infix fun isOpponentOf(player: Color)
        = this == !player
}