package com.arran4.txtar

import com.intellij.psi.tree.IElementType

object TxtarElementTypes {
    val HEADER: IElementType = TxtarTokenType("HEADER")
    val CONTENT: IElementType = TxtarTokenType("CONTENT")
    val COMMENT: IElementType = TxtarTokenType("COMMENT")

    val COMMENT_BLOCK: IElementType = TxtarTokenType("COMMENT_BLOCK")
    val FILE_CONTENT: IElementType = TxtarTokenType("FILE_CONTENT")
    val FILE_ENTRY: IElementType = TxtarTokenType("FILE_ENTRY")
}
