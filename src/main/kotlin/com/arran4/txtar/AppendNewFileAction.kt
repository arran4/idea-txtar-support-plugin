package com.arran4.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages

class AppendNewFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        
        val filename = Messages.showInputDialog(project, "Enter filename:", "Append New File", Messages.getQuestionIcon())
        if (filename.isNullOrBlank()) return
        
        WriteCommandAction.runWriteCommandAction(project) {
            val textLength = document.textLength
            val prefix = if (textLength > 0 && document.charsSequence[textLength - 1] != '\n') "\n" else ""
            val textToAppend = "$prefix-- $filename --\n"
            document.insertString(textLength, textToAppend)
            
            // Move caret to end
            editor.caretModel.moveToOffset(document.textLength)
        }
    }
}
