package com.example.txtar

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class TxtarFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()
        
        var child = root.firstChild
        while (child != null) {
            val type = child.node.elementType
            if (type == TxtarElementTypes.COMMENT_BLOCK) {
                if (child.textLength > 0) {
                     descriptors.add(FoldingDescriptor(child, child.textRange))
                }
            } else if (type == TxtarElementTypes.FILE_ENTRY) {
                val header = child.node.findChildByType(TxtarElementTypes.HEADER)
                val fileContent = child.node.findChildByType(TxtarElementTypes.FILE_CONTENT)

                if (header != null && fileContent != null && fileContent.textLength > 0) {
                    var rangeStart = fileContent.textRange.startOffset
                    if (header.text.endsWith("\n")) {
                         rangeStart--
                    }
                    descriptors.add(FoldingDescriptor(child, TextRange(rangeStart, fileContent.textRange.endOffset)))
                }
            }
            child = child.nextSibling
        }
        
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        val type = node.elementType
        if (type == TxtarElementTypes.COMMENT_BLOCK) return "..."
        if (type == TxtarElementTypes.FILE_ENTRY) return "..."
        return null
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
