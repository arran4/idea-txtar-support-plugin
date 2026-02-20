package com.example.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

class CalculateFileSizeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        val offset = editor.caretModel.offset
        val element = psiFile.findElementAt(offset)

        val (_, content) = TxtarFileEntryUtil.findFileEntry(element)

        val size = content?.textLength ?: 0
        Messages.showInfoMessage(project, "Size: $size bytes", "File Size")
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
