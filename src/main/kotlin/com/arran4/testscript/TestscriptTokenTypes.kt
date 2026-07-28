package com.arran4.testscript

import com.intellij.psi.tree.IElementType

class TestscriptTokenType(debugName: String) : IElementType(debugName, TestscriptLanguage.INSTANCE) {
    override fun toString(): String {
        return "TestscriptTokenType." + super.toString()
    }
}

object TestscriptTokenTypes {
    val PHASE_HEADER: IElementType = TestscriptTokenType("PHASE_HEADER")
    val COMMENT: IElementType = TestscriptTokenType("COMMENT")
    val CONDITION: IElementType = TestscriptTokenType("CONDITION")
    val NEGATION: IElementType = TestscriptTokenType("NEGATION")
    val COMMAND: IElementType = TestscriptTokenType("COMMAND")
    val ARGUMENT: IElementType = TestscriptTokenType("ARGUMENT")
    val QUOTED_STRING: IElementType = TestscriptTokenType("QUOTED_STRING")
    val VARIABLE: IElementType = TestscriptTokenType("VARIABLE")
    val TXTARFILES: IElementType = TestscriptTokenType("TXTARFILES")
}
