package com.arran4.testscript

import com.intellij.psi.TokenType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TestscriptLexerTest {

    private fun lex(input: String): List<Pair<Any?, String>> {
        val lexer = TestscriptLexer()
        lexer.start(input, 0, input.length, 0)
        val tokens = mutableListOf<Pair<Any?, String>>()
        while (lexer.tokenType != null) {
            val text = input.substring(lexer.tokenStart, lexer.tokenEnd)
            tokens.add(Pair(lexer.tokenType, text))
            lexer.advance()
        }
        return tokens
    }

    @Test
    fun testPhaseHeader() {
        val tokens = lex("# Phase 1\nexec cmd")
        assertEquals(TestscriptTokenTypes.PHASE_HEADER, tokens[0].first)
        assertEquals("# Phase 1", tokens[0].second)
        assertEquals(TokenType.WHITE_SPACE, tokens[1].first)
        assertEquals("\n", tokens[1].second)
        assertEquals(TestscriptTokenTypes.COMMAND, tokens[2].first)
        assertEquals("exec", tokens[2].second)
        assertEquals(TokenType.WHITE_SPACE, tokens[3].first)
        assertEquals(" ", tokens[3].second)
        assertEquals(TestscriptTokenTypes.ARGUMENT, tokens[4].first)
        assertEquals("cmd", tokens[4].second)
    }

    @Test
    fun testIndentedComment() {
        val tokens = lex("  # Comment")
        assertEquals(TokenType.WHITE_SPACE, tokens[0].first)
        assertEquals("  ", tokens[0].second)
        assertEquals(TestscriptTokenTypes.COMMENT, tokens[1].first)
        assertEquals("# Comment", tokens[1].second)
    }

    @Test
    fun testSimpleCommand() {
        val tokens = lex("exec program arg1 arg2")
        assertEquals(TestscriptTokenTypes.COMMAND, tokens[0].first)
        assertEquals("exec", tokens[0].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[1].first)
        assertEquals(TestscriptTokenTypes.ARGUMENT, tokens[2].first)
        assertEquals("program", tokens[2].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[3].first)
        assertEquals(TestscriptTokenTypes.ARGUMENT, tokens[4].first)
        assertEquals("arg1", tokens[4].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[5].first)
        assertEquals(TestscriptTokenTypes.ARGUMENT, tokens[6].first)
        assertEquals("arg2", tokens[6].second)
    }

    @Test
    fun testConditionAndNegation() {
        val tokens = lex("[short] [exec:git] ! grep 'foo' bar")
        assertEquals(TestscriptTokenTypes.CONDITION, tokens[0].first)
        assertEquals("[short]", tokens[0].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[1].first)

        assertEquals(TestscriptTokenTypes.CONDITION, tokens[2].first)
        assertEquals("[exec:git]", tokens[2].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[3].first)

        assertEquals(TestscriptTokenTypes.NEGATION, tokens[4].first)
        assertEquals("!", tokens[4].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[5].first)

        assertEquals(TestscriptTokenTypes.COMMAND, tokens[6].first)
        assertEquals("grep", tokens[6].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[7].first)

        assertEquals(TestscriptTokenTypes.QUOTED_STRING, tokens[8].first)
        assertEquals("'foo'", tokens[8].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[9].first)

        assertEquals(TestscriptTokenTypes.ARGUMENT, tokens[10].first)
        assertEquals("bar", tokens[10].second)
    }

    @Test
    fun testEnvironmentVariables() {
        val tokens = lex("echo \$NAME \${VAR} \${VAR@R}")
        assertEquals(TestscriptTokenTypes.COMMAND, tokens[0].first)
        assertEquals("echo", tokens[0].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[1].first)

        assertEquals(TestscriptTokenTypes.VARIABLE, tokens[2].first)
        assertEquals("\$NAME", tokens[2].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[3].first)

        assertEquals(TestscriptTokenTypes.VARIABLE, tokens[4].first)
        assertEquals("\${VAR}", tokens[4].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[5].first)

        assertEquals(TestscriptTokenTypes.VARIABLE, tokens[6].first)
        assertEquals("\${VAR@R}", tokens[6].second)
    }

    @Test
    fun testEscapedSingleQuotes() {
        val tokens = lex("echo 'Don''t'")
        assertEquals(TestscriptTokenTypes.COMMAND, tokens[0].first)
        assertEquals("echo", tokens[0].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[1].first)

        assertEquals(TestscriptTokenTypes.QUOTED_STRING, tokens[2].first)
        assertEquals("'Don''t'", tokens[2].second)
    }

    @Test
    fun testInlineComment() {
        val tokens = lex("echo# comment")
        assertEquals(TestscriptTokenTypes.COMMAND, tokens[0].first)
        assertEquals("echo", tokens[0].second)

        assertEquals(TestscriptTokenTypes.COMMENT, tokens[1].first)
        assertEquals("# comment", tokens[1].second)
    }

    @Test
    fun testUnterminatedString() {
        val tokens = lex("echo 'hello")
        assertEquals(TestscriptTokenTypes.COMMAND, tokens[0].first)
        assertEquals("echo", tokens[0].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[1].first)

        assertEquals(TokenType.BAD_CHARACTER, tokens[2].first)
        assertEquals("'hello", tokens[2].second)
    }

    @Test
    fun testQuotedCondition() {
        val tokens = lex("'[short]' exec")
        assertEquals(TestscriptTokenTypes.QUOTED_STRING, tokens[0].first)
        assertEquals("'[short]'", tokens[0].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[1].first)

        assertEquals(TestscriptTokenTypes.COMMAND, tokens[2].first)
        assertEquals("exec", tokens[2].second)
    }

    @Test
    fun testTxtarFileHeader() {
        val script = "exec cmd\n-- file1.txt --\ncontent\n-- file2.txt --\ncontent2"
        val tokens = lex(script)

        assertEquals(TestscriptTokenTypes.COMMAND, tokens[0].first)
        assertEquals("exec", tokens[0].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[1].first)

        assertEquals(TestscriptTokenTypes.ARGUMENT, tokens[2].first)
        assertEquals("cmd", tokens[2].second)

        assertEquals(TokenType.WHITE_SPACE, tokens[3].first)
        assertEquals("\n", tokens[3].second)

        assertEquals(TestscriptTokenTypes.TXTARFILES, tokens[4].first)
        assertEquals("-- file1.txt --\ncontent\n-- file2.txt --\ncontent2", tokens[4].second)
    }

    @Test
    fun testTxtarFileHeaderOnFirstLine() {
        val script = "-- file1.txt --\ncontent"
        val tokens = lex(script)

        assertEquals(1, tokens.size)
        assertEquals(TestscriptTokenTypes.TXTARFILES, tokens[0].first)
        assertEquals("-- file1.txt --\ncontent", tokens[0].second)
    }

    @Test
    fun testIndentedHeaderNotTxtar() {
        val script = "  -- file1.txt --"
        val tokens = lex(script)

        assertEquals(TokenType.WHITE_SPACE, tokens[0].first)
        assertEquals("  ", tokens[0].second)

        assertEquals(TestscriptTokenTypes.COMMAND, tokens[1].first)
        assertEquals("--", tokens[1].second)
    }
}
