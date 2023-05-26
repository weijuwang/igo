package go

/**
 * The result of a game and all associated information.
 */
data class GameResult (
    val scoresWithoutKomi: Map<Color, Int>,
    val winner: Color
)