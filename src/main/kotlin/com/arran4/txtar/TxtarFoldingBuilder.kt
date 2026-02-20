package com.arran4.txtar

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser

class TxtarFoldingBuilder : FoldingBuilderEx(), DumbAware {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()

        SyntaxTraverser.psiTraverser(root).forEach { element ->
            val type = element.node?.elementType ?: return@forEach
            if (type != TxtarElementTypes.COMMENT_BLOCK && type != TxtarElementTypes.FILE_ENTRY) return@forEach

            val rawRange = element.textRange
            if (rawRange.length <= 0) return@forEach

            val range = trimTrailingNewline(rawRange, document)
            if (range.length <= 0) return@forEach

            descriptors.add(FoldingDescriptor(element, range))
        }

        return descriptors.toTypedArray()
    }

    /**
     * Trims folding so that:
     * - it excludes the trailing line break
     */
    private fun trimTrailingNewline(range: TextRange, document: Document): TextRange {
        val text = document.charsSequence
        val start = range.startOffset
        var end = range.endOffset

        if (end > start && end <= document.textLength) {
            val lastCharIndex = end - 1
            if (text[lastCharIndex] == '\n') {
                end--
                if (end > start && text[end - 1] == '\r') {
                    end--
                }
            }
        }

        return TextRange(start, end)
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
