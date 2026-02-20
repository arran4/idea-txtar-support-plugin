package com.github.arran4.idea.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.Messages
import java.awt.datatransfer.DataFlavor

class AppendFromClipboardAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        
        val clipboardContent = CopyPasteManager.getInstance().getContents<String>(DataFlavor.stringFlavor)
        if (clipboardContent.isNullOrEmpty()) {
             Messages.showInfoMessage(project, "Clipboard is empty", "Info")
             return
        }
        
        val filename = Messages.showInputDialog(project, "Enter filename:", "Append From Clipboard", Messages.getQuestionIcon())
        if (filename.isNullOrBlank()) return
        
        WriteCommandAction.runWriteCommandAction(project) {
            val textLength = document.textLength
            val prefix = if (textLength > 0 && document.charsSequence[textLength - 1] != '\n') "\n" else ""
            val textToAppend = "$prefix-- $filename --\n$clipboardContent"
            document.insertString(textLength, textToAppend)
            
             // Move caret to end
            editor.caretModel.moveToOffset(document.textLength)
        }
    }
}
