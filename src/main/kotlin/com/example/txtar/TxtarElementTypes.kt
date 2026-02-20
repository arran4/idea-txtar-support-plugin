package com.example.txtar

import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType

object TxtarElementTypes {
    val HEADER: IElementType = TxtarTokenType("HEADER")
    val CONTENT: IElementType = TxtarTokenType("CONTENT")
    val COMMENT: IElementType = TxtarTokenType("COMMENT")
    val NL: IElementType = TxtarTokenType("NL")
    
    val COMMENT_BLOCK: IElementType = TxtarTokenType("COMMENT_BLOCK")
    val FILE_CONTENT: IElementType = TxtarTokenType("FILE_CONTENT")
    val FILE_ENTRY: IElementType = TxtarTokenType("FILE_ENTRY")
}
