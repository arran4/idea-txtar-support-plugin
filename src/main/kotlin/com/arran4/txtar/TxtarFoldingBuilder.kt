package com.arran4.txtar

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
            if (type == TxtarElementTypes.COMMENT_BLOCK || type == TxtarElementTypes.FILE_ENTRY) {
                if (child.textLength > 0) {
                     var range = child.textRange
                     if (type == TxtarElementTypes.FILE_ENTRY) {
                         var endOffset = range.endOffset
                         while (endOffset > range.startOffset && endOffset <= document.textLength) {
                             val char = document.charsSequence[endOffset - 1]
                             if (char == '\n' || char == '\r') {
                                 endOffset--
                             } else {
                                 break
                             }
                         }
                         if (endOffset < range.endOffset) {
                             range = TextRange(range.startOffset, endOffset)
                         }
                     }
                     descriptors.add(FoldingDescriptor(child, range))
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
            return header?.text?.trimEnd { it == '\n' || it == '\r' } ?: "..."
        }
        return null
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
