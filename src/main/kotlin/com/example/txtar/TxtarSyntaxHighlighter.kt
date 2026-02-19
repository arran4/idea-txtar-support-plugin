package com.example.txtar

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

class TxtarSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val HEADER = createTextAttributesKey("TXTAR_HEADER", DefaultLanguageHighlighterColors.KEYWORD)
        val COMMENT = createTextAttributesKey("TXTAR_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        val CONTENT = createTextAttributesKey("TXTAR_CONTENT", HighlighterColors.TEXT)
        
        private val HEADER_KEYS = arrayOf(HEADER)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val CONTENT_KEYS = arrayOf(CONTENT)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = TxtarLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            TxtarElementTypes.HEADER -> HEADER_KEYS
            TxtarElementTypes.COMMENT -> COMMENT_KEYS
            TxtarElementTypes.CONTENT -> CONTENT_KEYS
            else -> EMPTY_KEYS
        }
    }
}
