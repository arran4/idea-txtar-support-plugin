package com.arran4.txtar

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class TxtarLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset = 0
    private var endOffset = 0
    private var currentOffset = 0
    private var currentTokenStart = 0
    private var currentTokenEnd = 0
    private var currentState = 0 // 0: Start/Comment, 1: After First Header (Content)
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.currentOffset = startOffset
        this.currentState = initialState
        advance()
    }

    override fun getState(): Int = currentState

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = currentTokenStart

    override fun getTokenEnd(): Int = currentTokenEnd

    override fun advance() {
        if (currentOffset >= endOffset) {
            tokenType = null
            return
        }

        currentTokenStart = currentOffset
        
        // Find end of line
        var lineEnd = currentOffset
        while (lineEnd < endOffset && buffer[lineEnd] != '\n') {
            lineEnd++
        }
        // Include \n in the token if present
        if (lineEnd < endOffset && buffer[lineEnd] == '\n') {
            lineEnd++
        }
        
        currentTokenEnd = lineEnd
        val lineText = buffer.subSequence(currentTokenStart, currentTokenEnd)
        
        // Check if header
        val isHeader = isHeaderLine(lineText)
        
        if (isHeader) {
            tokenType = TxtarElementTypes.HEADER
            currentState = 1 // Seen at least one header
        } else {
            if (currentState == 0) {
                tokenType = TxtarElementTypes.COMMENT
            } else {
                tokenType = TxtarElementTypes.CONTENT
            }
        }

        currentOffset = currentTokenEnd
    }

    private fun isHeaderLine(text: CharSequence): Boolean {
        // Must start with "-- " and end with " --" (ignoring newline)
        
        var len = text.length
        if (len == 0) return false
        
        // Strip trailing newline
        if (text[len - 1] == '\n') {
            len--
            if (len > 0 && text[len - 1] == '\r') {
                len--
            }
        }
        
        if (len < 6) return false // "--  --" is 7 chars minimum? No, "-- " (3) + name + " --" (3).
        // Wait, name can be empty? "--  --" -> name is space?
        // Go docs: "-- filename --"
        // Let's assume filename is at least 1 char. So length >= 7.
        // If filename can be empty, then "--  --" is valid?
        // Let's assume standard behavior.
        
        if (len < 7) return false
        
        // Check prefix "-- "
        if (text[0] != '-' || text[1] != '-' || text[2] != ' ') return false
        
        // Check suffix " --"
        if (text[len - 3] != ' ' || text[len - 2] != '-' || text[len - 1] != '-') return false
        
        return true
    }
    
    override fun getBufferSequence(): CharSequence = buffer
    
    override fun getBufferEnd(): Int = endOffset
}
