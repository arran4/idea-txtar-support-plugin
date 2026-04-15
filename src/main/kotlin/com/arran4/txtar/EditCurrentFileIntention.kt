package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiFile
import javax.swing.JComponent
import javax.swing.JTextArea

class EditCurrentFileIntention : IntentionAction {
    override fun getText(): String = "Edit just this file (dedicated editor)"
    override fun getFamilyName(): String = "Txtar"
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file !is TxtarFile || editor == null) return false
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        val (header, content) = TxtarFileEntryUtil.findFileEntry(element)
        return header != null || content != null
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is TxtarFile || editor == null) return
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        val (header, content) = TxtarFileEntryUtil.findFileEntry(element)
        if (header == null) return

        val textArea = JTextArea(content?.text ?: "")
        textArea.rows = 15
        textArea.columns = 50
        val scrollPane = com.intellij.ui.components.JBScrollPane(textArea)

        val dialog = object : DialogWrapper(project, true) {
            init {
                title = "Edit ${header.text.trim()}"
                init()
            }
            override fun createCenterPanel(): JComponent = scrollPane
        }

        if (dialog.showAndGet()) {
            WriteCommandAction.runWriteCommandAction(project) {
                val (description, entries) = EditTxtarEntriesAction.parseFile(file)
                val targetHeader = header.text
                val matchedEntry = entries.find { it.headerText == targetHeader }
                if (matchedEntry != null) {
                    matchedEntry.contentText = textArea.text
                    val newText = EditTxtarEntriesAction.buildText(description, entries)
                    file.viewProvider.document?.setText(newText)
                }
            }
        }
    }
    override fun startInWriteAction(): Boolean = false
}
