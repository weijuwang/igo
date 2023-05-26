import go.xy



fun main(args: Array<String>) {
    /*
    val game = go.Game(go.Ruleset.NewZealand, 19, 19)
    game.makeMoves(
        3 xy 3,
        null,
        2 xy 3,
        3 xy 4,
        null,
        3 xy 2,
        null,
        1 xy 3,
        //null,
        //4 xy 3,
        //null,
        //2 xy 4,
        //null,
        //2 xy 2
    )

    game.debugPrint()
    */

    val text =
        """
            (; FF[4] C[root]
            (; C[a]; C[b]
            (; C[c])
            (; C[d]; C[e])
            )
            (;C[f]
            (; C[g]; C[h]; C[i])
            (; C[j])
            )
            )
        """.trimIndent()

    println(go.sgf.Tokenizer.tokenize(text)
        .joinToString("")
    )
}