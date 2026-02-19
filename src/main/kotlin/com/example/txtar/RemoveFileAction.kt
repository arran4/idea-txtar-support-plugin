package com.example.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiElement

class RemoveFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        
        val offset = editor.caretModel.offset
        val element = psiFile.findElementAt(offset) ?: return
        
        // Find HEADER or FILE_CONTENT
        var target: PsiElement? = element
        while (target != null) {
            val type = target.node.elementType
            if (type == TxtarElementTypes.HEADER || type == TxtarElementTypes.FILE_CONTENT) {
                break
            }
            target = target.parent
            if (target is com.intellij.psi.PsiFile) {
                target = null
                break
            }
        }
        
        if (target == null) return
        
        // Identify the pair
        var header: PsiElement? = null
        var content: PsiElement? = null
        
        if (target.node.elementType == TxtarElementTypes.HEADER) {
            header = target
            val next = header.nextSibling
            if (next != null && next.node.elementType == TxtarElementTypes.FILE_CONTENT) {
                content = next
            }
        } else {
            content = target
            val prev = content.prevSibling
            if (prev != null && prev.node.elementType == TxtarElementTypes.HEADER) {
                header = prev
            }
        }
        
        // Remove
        WriteCommandAction.runWriteCommandAction(project) {
            content?.delete()
            header?.delete()
        }
    }

    override fun update(e: AnActionEvent) {
        // Enable only if caret is on a file entry
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        
        e.presentation.isEnabledAndVisible = false
        
        if (project != null && editor != null && psiFile != null) {
             val offset = editor.caretModel.offset
             val element = psiFile.findElementAt(offset)
             if (element != null) {
                 var target: PsiElement? = element
                 while (target != null) {
                    val type = target.node.elementType
                    if (type == TxtarElementTypes.HEADER || type == TxtarElementTypes.FILE_CONTENT) {
                        e.presentation.isEnabledAndVisible = true
                        break
                    }
                    target = target.parent
                    if (target is com.intellij.psi.PsiFile) break
                 }
             }
        }
    }
}
