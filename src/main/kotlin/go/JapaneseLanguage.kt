package go

object JapaneseLanguage {
    /**
     * A Japanese space, which is slightly wider than the ASCII space.
     */
    const val space = '　'

    /**
     * A dot separator.
     */
    const val dot = '・'

    /**
     * List of kanji numerals.
     */
    private val numerals = listOf(
        '零',
        '一',
        '二',
        '三',
        '四',
        '五',
        '六',
        '七',
        '八',
        '九',
        '十'
    )

    /**
     * Returns the kanji for `n`.
     *
     * Constraints: `n in (0..10)`
     */
    fun numeral(n: Int)
        = numerals[n]
}