package com.arran4.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.ui.Messages
import java.io.IOException

class ExtractFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return

        val offset = editor.caretModel.offset
        val element = psiFile.findElementAt(offset)

        val (header, content) = TxtarFileEntryUtil.findFileEntry(element)

        if (header == null) {
            Messages.showErrorDialog(project, "No file header found", "Error")
            return
        }

        val filename = extractFilename(header.text)
        val contentStr = content?.text ?: ""

        val descriptor = FileSaverDescriptor("Save File", "Save extracted content to a file")
        val dialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project)
        val virtualFileWrapper = dialog.save(null as VirtualFile?, filename)

        if (virtualFileWrapper != null) {
            val file = virtualFileWrapper.file
            try {
                file.writeText(contentStr)
            } catch (ex: IOException) {
                Messages.showErrorDialog(project, "Error saving file: ${ex.message}", "Error")
            }
        }
    }

    private fun extractFilename(headerText: String): String {
        var text = headerText
        if (text.endsWith("\n")) {
            text = text.substring(0, text.length - 1)
        }
        if (text.endsWith("\r")) {
             text = text.substring(0, text.length - 1)
        }

        if (text.startsWith("-- ") && text.endsWith(" --")) {
            return text.substring(3, text.length - 3)
        }
        return "unknown"
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
