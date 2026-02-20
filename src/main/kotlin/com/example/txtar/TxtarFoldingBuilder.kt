package com.arran4.txtar

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement

class TxtarFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()
        
        var child = root.firstChild
        while (child != null) {
            val type = child.node.elementType
            if (type == TxtarElementTypes.COMMENT_BLOCK || type == TxtarElementTypes.FILE_ENTRY) {
                if (child.textLength > 0) {
                     descriptors.add(FoldingDescriptor(child, child.textRange))
                }
            }
            child = child.nextSibling
        }
        
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        val type = node.elementType
        if (type == TxtarElementTypes.COMMENT_BLOCK) return "..."
        if (type == TxtarElementTypes.FILE_ENTRY) {
            val header = node.findChildByType(TxtarElementTypes.HEADER)
            return header?.text ?: "..."
        }
        return null
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
