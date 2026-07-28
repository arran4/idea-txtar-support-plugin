package com.arran4.testscript

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class TestscriptLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset = 0
    private var endOffset = 0
    private var currentOffset = 0
    private var currentTokenStart = 0
    private var currentTokenEnd = 0
    private var tokenType: IElementType? = null

    // Line stage: 0 = expecting condition / negation / command, 1 = expecting command, 2 = expecting argument, 3 = txtar files
    private var lineStage = 0
    private var currentWordRole: WordRole = WordRole.ARGUMENT

    companion object {
        const val STATE_TXTAR_FILES = 3
    }

    enum class WordRole {
        CONDITION,
        NEGATION,
        COMMAND,
        ARGUMENT
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.currentOffset = startOffset
        this.lineStage = initialState
        this.currentWordRole = WordRole.ARGUMENT
        advance()
    }

    override fun getState(): Int = lineStage

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = currentTokenStart

    override fun getTokenEnd(): Int = currentTokenEnd

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = endOffset

    override fun advance() {
        if (currentOffset >= endOffset) {
            tokenType = null
            return
        }

        currentTokenStart = currentOffset

        if (lineStage == STATE_TXTAR_FILES) {
            currentTokenEnd = endOffset
            tokenType = TestscriptTokenTypes.TXTARFILES
            currentOffset = endOffset
            return
        }

        val isColumnZero = currentOffset == 0 || buffer[currentOffset - 1] == '\n'
        if (isColumnZero && isHeaderLine(currentOffset)) {
            lineStage = STATE_TXTAR_FILES
            currentTokenEnd = endOffset
            tokenType = TestscriptTokenTypes.TXTARFILES
            currentOffset = endOffset
            return
        }

        // 1. Check for whitespace (including newlines)
        if (isWhitespace(buffer[currentOffset])) {
            var ptr = currentOffset
            var sawNewline = false
            while (ptr < endOffset && isWhitespace(buffer[ptr])) {
                if (buffer[ptr] == '\n') {
                    sawNewline = true
                }
                ptr++
            }
            currentTokenEnd = ptr
            tokenType = TokenType.WHITE_SPACE
            if (sawNewline) {
                lineStage = 0
            }
            currentOffset = currentTokenEnd
            return
        }

        // 2. Check for Phase Line (# in column 0) or Comment Line (# after col 0)
        if (buffer[currentOffset] == '#') {
            var ptr = currentOffset
            while (ptr < endOffset && buffer[ptr] != '\n' && buffer[ptr] != '\r') {
                ptr++
            }
            currentTokenEnd = ptr
            tokenType = if (isColumnZero) {
                TestscriptTokenTypes.PHASE_HEADER
            } else {
                TestscriptTokenTypes.COMMENT
            }
            currentOffset = currentTokenEnd
            return
        }

        // 3. We are at a word (or piece of word) on an executable line.
        val isStartOfWord = isColumnZero || isWhitespace(buffer[currentOffset - 1])
        val wordEnd = findWordEnd(currentOffset)

        if (isStartOfWord) {
            val wordValue = computeWordValue(currentOffset, wordEnd)
            currentWordRole = determineWordRole(wordValue)
        }

        // Process token starting at currentOffset inside the current word
        val ch = buffer[currentOffset]

        if (ch == '\'') {
            val quoteEnd = scanQuotedPiece(currentOffset)
            if (quoteEnd == -1) {
                var ptr = currentOffset + 1
                while (ptr < endOffset && buffer[ptr] != '\n' && buffer[ptr] != '\r') {
                    ptr++
                }
                currentTokenEnd = ptr
                tokenType = TokenType.BAD_CHARACTER
            } else {
                currentTokenEnd = quoteEnd
                tokenType = TestscriptTokenTypes.QUOTED_STRING
            }
        } else if (ch == '$' && isVariableStart(currentOffset)) {
            val varEnd = scanVariable(currentOffset)
            currentTokenEnd = varEnd
            tokenType = TestscriptTokenTypes.VARIABLE
        } else {
            var ptr = currentOffset
            while (ptr < wordEnd) {
                val c = buffer[ptr]
                if (c == '\'' || (c == '$' && isVariableStart(ptr))) {
                    break
                }
                ptr++
            }
            currentTokenEnd = ptr
            tokenType = when (currentWordRole) {
                WordRole.CONDITION -> TestscriptTokenTypes.CONDITION
                WordRole.NEGATION -> TestscriptTokenTypes.NEGATION
                WordRole.COMMAND -> TestscriptTokenTypes.COMMAND
                WordRole.ARGUMENT -> TestscriptTokenTypes.ARGUMENT
            }
        }

        currentOffset = currentTokenEnd
    }

    private fun determineWordRole(wordValue: String): WordRole {
        return when (lineStage) {
            0 -> {
                if (wordValue.startsWith("[") && wordValue.endsWith("]")) {
                    WordRole.CONDITION
                } else if (wordValue == "!") {
                    lineStage = 1
                    WordRole.NEGATION
                } else {
                    lineStage = 2
                    WordRole.COMMAND
                }
            }
            1 -> {
                lineStage = 2
                WordRole.COMMAND
            }
            else -> {
                WordRole.ARGUMENT
            }
        }
    }

    private fun isHeaderLine(start: Int): Boolean {
        var lineEnd = start
        while (lineEnd < endOffset && buffer[lineEnd] != '\n' && buffer[lineEnd] != '\r') {
            lineEnd++
        }
        val len = lineEnd - start
        if (len < 7) return false
        if (buffer[start] != '-' || buffer[start + 1] != '-' || buffer[start + 2] != ' ') return false
        if (buffer[lineEnd - 3] != ' ' || buffer[lineEnd - 2] != '-' || buffer[lineEnd - 1] != '-') return false
        return true
    }

    private fun isWhitespace(c: Char): Boolean = c == ' ' || c == '\t' || c == '\r' || c == '\n'

    private fun findWordEnd(start: Int): Int {
        var ptr = start
        var inQuote = false
        while (ptr < endOffset) {
            val c = buffer[ptr]
            if (inQuote) {
                if (c == '\'') {
                    if (ptr + 1 < endOffset && buffer[ptr + 1] == '\'') {
                        ptr += 2
                        continue
                    } else {
                        inQuote = false
                    }
                } else if (c == '\n' || c == '\r') {
                    break
                }
            } else {
                if (c == '\'') {
                    inQuote = true
                } else if (isWhitespace(c) || c == '#') {
                    break
                }
            }
            ptr++
        }
        return ptr
    }

    private fun computeWordValue(start: Int, end: Int): String {
        val sb = StringBuilder()
        var ptr = start
        var inQuote = false
        while (ptr < end) {
            val c = buffer[ptr]
            if (inQuote) {
                if (c == '\'') {
                    if (ptr + 1 < end && buffer[ptr + 1] == '\'') {
                        sb.append('\'')
                        ptr += 2
                        continue
                    } else {
                        inQuote = false
                    }
                } else {
                    sb.append(c)
                }
            } else {
                if (c == '\'') {
                    inQuote = true
                } else {
                    sb.append(c)
                }
            }
            ptr++
        }
        return sb.toString()
    }

    private fun scanQuotedPiece(start: Int): Int {
        var ptr = start + 1
        while (ptr < endOffset) {
            val c = buffer[ptr]
            if (c == '\'') {
                if (ptr + 1 < endOffset && buffer[ptr + 1] == '\'') {
                    ptr += 2
                    continue
                } else {
                    return ptr + 1
                }
            } else if (c == '\n' || c == '\r') {
                return -1
            }
            ptr++
        }
        return -1
    }

    private fun isVariableStart(offset: Int): Boolean {
        if (buffer[offset] != '$') return false
        if (offset + 1 >= endOffset) return false
        val next = buffer[offset + 1]
        if (next == '{') return true
        return isVarNameChar(next)
    }

    private fun isVarNameChar(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c == '_'
    }

    private fun scanVariable(start: Int): Int {
        if (start + 1 < endOffset && buffer[start + 1] == '{') {
            var ptr = start + 2
            while (ptr < endOffset && buffer[ptr] != '}' && buffer[ptr] != '\n' && buffer[ptr] != '\r') {
                ptr++
            }
            if (ptr < endOffset && buffer[ptr] == '}') {
                return ptr + 1
            }
            return ptr
        } else {
            var ptr = start + 1
            while (ptr < endOffset && isVarNameChar(buffer[ptr])) {
                ptr++
            }
            return ptr
        }
    }
}
