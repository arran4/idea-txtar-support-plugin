package com.example.txtar

import com.intellij.psi.tree.IElementType

class TxtarTokenType(debugName: String) : IElementType(debugName, TxtarLanguage.INSTANCE) {
    override fun toString(): String {
        return "TxtarTokenType." + super.toString()
    }
}
