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
        val element = psiFile.findElementAt(offset)

        val (header, content) = TxtarFileEntryUtil.findFileEntry(element)

        if (header != null || content != null) {
            WriteCommandAction.runWriteCommandAction(project) {
                content?.delete()
                header?.delete()
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        e.presentation.isEnabledAndVisible = false

        if (project != null && editor != null && psiFile != null) {
             val offset = editor.caretModel.offset
             val element = psiFile.findElementAt(offset)
             val (header, content) = TxtarFileEntryUtil.findFileEntry(element)
             if (header != null || content != null) {
                 e.presentation.isEnabledAndVisible = true
             }
        }
    }
}
