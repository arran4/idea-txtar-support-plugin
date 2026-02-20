package com.arran4.txtar

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

object TxtarFileEntryUtil {
    fun findFileEntry(element: PsiElement?): Pair<PsiElement?, PsiElement?> {
        if (element == null) return null to null

        var target: PsiElement? = element
        while (target != null) {
            val type = target.node.elementType
            if (type == TxtarElementTypes.HEADER || type == TxtarElementTypes.FILE_CONTENT) {
                break
            }
            target = target.parent
            if (target is PsiFile) {
                target = null
                break
            }
        }

        if (target == null) return null to null

        var header: PsiElement? = null
        var content: PsiElement? = null

        if (target.node.elementType == TxtarElementTypes.HEADER) {
            header = target
            var next = header.nextSibling
            while (next != null) {
                if (next.node.elementType == TxtarElementTypes.FILE_CONTENT) {
                    content = next
                    break
                }
                if (next.node.elementType == TxtarElementTypes.HEADER) {
                    break
                }
                next = next.nextSibling
            }
        } else {
            content = target
            var prev = content.prevSibling
            while (prev != null) {
                if (prev.node.elementType == TxtarElementTypes.HEADER) {
                    header = prev
                    break
                }
                if (prev.node.elementType == TxtarElementTypes.FILE_CONTENT) {
                    break
                }
                prev = prev.prevSibling
            }
        }

        return header to content
    }
}
