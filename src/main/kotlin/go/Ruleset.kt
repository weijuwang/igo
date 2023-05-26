package go

/**
 * TODO Rulesets
 * Each [Ruleset] defines the following characteristics of a rule set:
 * - [score]: Scoring method
 * - [komi]: Komi w/o tiebreaker half point
 * - [suicideAllowed]: Whether suicide is allowed
 * - [drawsAllowed]: Whether draws are allowed; if `false`, White wins ties, equivalent to adding 0.5 to [komi]
 */
enum class Ruleset (
    val komi: Int,
    val suicideAllowed: Boolean,
    val drawsAllowed: Boolean
) {
    AGA(
        komi = 7,
        suicideAllowed = false,
        drawsAllowed = false
    ),
    Japanese(
        komi = 6,
        suicideAllowed = false,
        drawsAllowed = false
    ),
    Chinese(
        komi = 7,
        suicideAllowed = false,
        drawsAllowed = false
    ),
    NewZealand(
        komi = 7,
        suicideAllowed = true,
        drawsAllowed = true
    ),
    TrompTaylor(
        komi = 0,
        suicideAllowed = true,
        drawsAllowed = true
    )

    //abstract fun score(position: Position): GameResult
}