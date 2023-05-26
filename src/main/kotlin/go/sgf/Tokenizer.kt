package go.sgf

/**
 * An SGF tokenizer. Whitespace between all tokens is ignored.
 *
 * There are seven token types inherited from the [Token] interface.
 * [OpenParen], [CloseParen], and [Semicolon] always refer to the same literal each -- the names should be self-explanatory.
 * [PropIdent] and [PropValue] stand for values.
 * [EndOfString] and [InvalidToken] always appear at the end of a token list.
 */
object Tokenizer {
    /**
     * A generic token.
     *
     * All [Token]s should implement a `toString()` method that returns what the raw SGF representation of that token would be.
     */
    interface Token

    /**
     * A generic token that only matches the character [matchedLiteral].
     */
    private abstract class LiteralCharToken(
        /**
         * The character in the SGF text that this token represents.
         */
        val matchedLiteral: Char
    ) : Token {
        override fun toString() = matchedLiteral.toString()
    }

    /**
     * A property key or ident (`propIdent`).
     */
    private data class PropIdent(val name: String) : Token {
        override fun toString() = name
    }

    /**
     * A property value (`propValue`), which is enclosed in brackets in the raw SGF.
     *
     * [content] does not include the original brackets, only what is inside of them.
     */
    private data class PropValue(val content: String) : Token {
        override fun toString() = "[${
            content
                // Escape backslashes and ending brackets
                .replace("\\", "\\\\")
                .replace("]", "\\]")
        }]"
    }

    private class OpenParen : LiteralCharToken('(')
    private class CloseParen : LiteralCharToken(')')
    private class Semicolon : LiteralCharToken(';')

    /**
     * Signifies that the end of the string was reached and there were no errors.
     */
    private class EndOfString : Token {
        override fun toString() = ""
    }

    /**
     * Signifies that no token could be matched, giving the [index] in the string at which the error occurred.
     */
    private class InvalidToken(val index: Int) : Token {
        override fun toString() = ""
    }

    fun tokenize(text: String): List<Token> {
        var currPos = 0
        val tokens = mutableListOf<Token>()

        fun getNextChar(): Char? =
            if (currPos >= text.length)
                null
            else {
                val output = text[currPos]
                currPos++
                output
            }

        tokenizerLoop@ while (true) {
            val tokenStartIndex = currPos

            fun invalidToken() {
                tokens += InvalidToken(tokenStartIndex)
            }

            when (val tokenFirstChar = getNextChar()) {
                '(' -> tokens += OpenParen()
                ')' -> tokens += CloseParen()
                ';' -> tokens += Semicolon()
                '[' -> {
                    var escaped = false
                    var content = ""

                    propValue@ while (true) {
                        when(val nextContentChar = getNextChar()) {
                            null -> {
                                invalidToken()
                                break@tokenizerLoop
                            }
                            '\\' -> {
                                if (escaped)
                                    content += '\\'
                                escaped = !escaped
                            }
                            ']' ->
                                if (escaped)
                                    content += ']'
                                else
                                    break@propValue
                            else ->
                                if (escaped) {
                                    // Invalid escape because the only two legal escapes were covered above
                                    invalidToken()
                                    break@tokenizerLoop
                                } else {
                                    content += nextContentChar
                                }
                        }
                    }

                    tokens += PropValue(content)
                }
                null -> {
                    tokens += EndOfString()
                    break@tokenizerLoop
                }
                else -> {
                    if (tokenFirstChar.isWhitespace())
                        continue

                    if (tokenFirstChar.isUpperCase()) {
                        var ident = tokenFirstChar.toString()

                        propIdent@ while(true) {
                            val nextIdentChar = getNextChar()
                            if (nextIdentChar == null) {
                                invalidToken()
                                break@tokenizerLoop
                            } else if (nextIdentChar.isUpperCase()) {
                                ident += nextIdentChar
                            } else {
                                currPos--
                                break@propIdent
                            }
                        }

                        tokens += PropIdent(ident)
                    }
                }
            }
        }

        return tokens
    }
}