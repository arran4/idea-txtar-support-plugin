package com.arran4.testscript

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class TestscriptSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val PHASE_HEADER = createTextAttributesKey("TESTSCRIPT_PHASE_HEADER", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val COMMENT = createTextAttributesKey("TESTSCRIPT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val CONDITION = createTextAttributesKey("TESTSCRIPT_CONDITION", DefaultLanguageHighlighterColors.METADATA)
        val NEGATION = createTextAttributesKey("TESTSCRIPT_NEGATION", DefaultLanguageHighlighterColors.KEYWORD)
        val COMMAND = createTextAttributesKey("TESTSCRIPT_COMMAND", DefaultLanguageHighlighterColors.KEYWORD)
        val ARGUMENT = createTextAttributesKey("TESTSCRIPT_ARGUMENT", DefaultLanguageHighlighterColors.IDENTIFIER)
        val QUOTED_STRING = createTextAttributesKey("TESTSCRIPT_QUOTED_STRING", DefaultLanguageHighlighterColors.STRING)
        val VARIABLE = createTextAttributesKey("TESTSCRIPT_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
        val TXTARFILES = createTextAttributesKey("TESTSCRIPT_TXTARFILES", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR)

        private val PHASE_HEADER_KEYS = arrayOf(PHASE_HEADER)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val CONDITION_KEYS = arrayOf(CONDITION)
        private val NEGATION_KEYS = arrayOf(NEGATION)
        private val COMMAND_KEYS = arrayOf(COMMAND)
        private val ARGUMENT_KEYS = arrayOf(ARGUMENT)
        private val QUOTED_STRING_KEYS = arrayOf(QUOTED_STRING)
        private val VARIABLE_KEYS = arrayOf(VARIABLE)
        private val TXTARFILES_KEYS = arrayOf(TXTARFILES)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = TestscriptLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            TestscriptTokenTypes.PHASE_HEADER -> PHASE_HEADER_KEYS
            TestscriptTokenTypes.COMMENT -> COMMENT_KEYS
            TestscriptTokenTypes.CONDITION -> CONDITION_KEYS
            TestscriptTokenTypes.NEGATION -> NEGATION_KEYS
            TestscriptTokenTypes.COMMAND -> COMMAND_KEYS
            TestscriptTokenTypes.ARGUMENT -> ARGUMENT_KEYS
            TestscriptTokenTypes.QUOTED_STRING -> QUOTED_STRING_KEYS
            TestscriptTokenTypes.VARIABLE -> VARIABLE_KEYS
            TestscriptTokenTypes.TXTARFILES -> TXTARFILES_KEYS
            else -> EMPTY_KEYS
        }
    }
}
