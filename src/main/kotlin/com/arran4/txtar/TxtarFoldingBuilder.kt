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

            val range = expandToWholeLines(rawRange, document)
            if (range.length <= 0) return@forEach

            descriptors.add(FoldingDescriptor(element, range))
        }

        return descriptors.toTypedArray()
    }

    /**
     * Expands folding so that:
     * - it includes the line break BEFORE the element (if present)
     * - it includes the line break AFTER the element (if present)
     *
     * This keeps IntelliJ's folding test markup in the multiline form:
     * <fold ...>
     * ...
     * </fold>
     */
    private fun expandToWholeLines(range: TextRange, document: Document): TextRange {
        val text = document.charsSequence
        var start = range.startOffset.coerceIn(0, document.textLength)
        var end = range.endOffset.coerceIn(0, document.textLength)

        // include preceding line break (prefer grabbing "\r\n" together)
        if (start > 0) {
            val prev = text[start - 1]
            if (prev == '\n') {
                start--
                if (start > 0 && text[start - 1] == '\r') start--
            } else if (prev == '\r') {
                start--
            }
        }

        // include following line break (prefer grabbing "\r\n" together)
        if (end < document.textLength) {
            val next = text[end]
            if (next == '\r') {
                end++
                if (end < document.textLength && text[end] == '\n') end++
            } else if (next == '\n') {
                end++
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
