package com.example.txtar

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class TxtarLexerTest {
    @Test
    fun testLexer() {
        val content = "-- file1 --\ncontent1\n-- file2 --\ncontent2"
        val lexer = TxtarLexer()
        lexer.start(content, 0, content.length, 0)
        
        // Header 1
        assertEquals(TxtarElementTypes.HEADER, lexer.tokenType)
        var tokenText = content.subSequence(lexer.tokenStart, lexer.tokenEnd).toString()
        assertEquals("-- file1 --\n", tokenText)
        lexer.advance()
        
        // Content 1
        assertEquals(TxtarElementTypes.CONTENT, lexer.tokenType)
        tokenText = content.subSequence(lexer.tokenStart, lexer.tokenEnd).toString()
        assertEquals("content1\n", tokenText)
        lexer.advance()
        
        // Header 2
        assertEquals(TxtarElementTypes.HEADER, lexer.tokenType)
        tokenText = content.subSequence(lexer.tokenStart, lexer.tokenEnd).toString()
        assertEquals("-- file2 --\n", tokenText)
        lexer.advance()
        
        // Content 2
        assertEquals(TxtarElementTypes.CONTENT, lexer.tokenType)
        tokenText = content.subSequence(lexer.tokenStart, lexer.tokenEnd).toString()
        assertEquals("content2", tokenText)
        lexer.advance()
        
        assertNull(lexer.tokenType)
    }

    @Test
    fun testComment() {
        val content = "comment\n-- file1 --\ncontent1"
        val lexer = TxtarLexer()
        lexer.start(content, 0, content.length, 0)
        
        // Comment
        assertEquals(TxtarElementTypes.COMMENT, lexer.tokenType)
        var tokenText = content.subSequence(lexer.tokenStart, lexer.tokenEnd).toString()
        assertEquals("comment\n", tokenText)
        lexer.advance()
        
        // Header
        assertEquals(TxtarElementTypes.HEADER, lexer.tokenType)
        tokenText = content.subSequence(lexer.tokenStart, lexer.tokenEnd).toString()
        assertEquals("-- file1 --\n", tokenText)
        lexer.advance()
        
        // Content
        assertEquals(TxtarElementTypes.CONTENT, lexer.tokenType)
        tokenText = content.subSequence(lexer.tokenStart, lexer.tokenEnd).toString()
        assertEquals("content1", tokenText)
        lexer.advance()
        
        assertNull(lexer.tokenType)
    }
}
