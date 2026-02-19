package com.example.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException

class AppendFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        
        val descriptor = FileChooserDescriptor(true, false, false, false, false, true)
        descriptor.title = "Select Files to Append"
        
        val files = FileChooser.chooseFiles(descriptor, project, null)
        if (files.isEmpty()) return
        
        WriteCommandAction.runWriteCommandAction(project) {
            for (file in files) {
                try {
                    val content = String(file.contentsToByteArray(), Charsets.UTF_8)
                    val filename = file.name
                    
                    val textLength = document.textLength
                    // Check if document is empty or ends with newline
                    val needsNewline = textLength > 0 && document.charsSequence[textLength - 1] != '\n'
                    val prefix = if (needsNewline) "\n" else ""
                    
                    // Also check if the file content itself ends with newline. 
                    // If not, we don't necessarily append one, but it's good practice for txtar readability.
                    // But we should preserve content exactly.
                    // However, if we append multiple files, subsequent files need a newline separator before their header.
                    // The loop handles this by checking `document` state before each append.
                    
                    val textToAppend = "$prefix-- $filename --\n$content"
                    document.insertString(document.textLength, textToAppend)
                } catch (ex: IOException) {
                    Messages.showErrorDialog(project, "Error reading file ${file.name}: ${ex.message}", "Error")
                }
            }
        }
    }
}
