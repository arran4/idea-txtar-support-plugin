package com.arran4.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import javax.swing.JComponent
import java.awt.BorderLayout
import javax.swing.JPanel
import java.awt.Dimension

class EditDescriptionDialog(project: Project?, initialNotes: String) : DialogWrapper(project, true) {
    private val textArea = JBTextArea(initialNotes)

    init {
        title = "Edit Notes/Description"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())
        val scrollPane = JBScrollPane(textArea)
        scrollPane.preferredSize = Dimension(400, 300)
        panel.add(scrollPane, BorderLayout.CENTER)
        return panel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return textArea
    }

    fun getNotes(): String {
        return textArea.text
    }
}

class EditDescriptionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        val file = e.getData(CommonDataKeys.PSI_FILE) as? TxtarFile ?: return

        var firstHeaderOffset = document.textLength
        for (child in file.children) {
            if (child.node.elementType == TxtarElementTypes.FILE_ENTRY || child.node.elementType == TxtarElementTypes.HEADER) {
                firstHeaderOffset = child.textRange.startOffset
                break
            }
        }

        val currentNotes = document.getText(com.intellij.openapi.util.TextRange(0, firstHeaderOffset))

        val dialog = EditDescriptionDialog(project, currentNotes)
        if (dialog.showAndGet()) {
            val newNotes = dialog.getNotes()
            WriteCommandAction.runWriteCommandAction(project) {
                var notesToInsert = newNotes
                if (notesToInsert.isNotEmpty() && !notesToInsert.endsWith("\n") && firstHeaderOffset < document.textLength) {
                    notesToInsert += "\n"
                }
                // Also, if previous notes had a trailing newline but new ones don't, we might need to ensure there's a separation.
                document.replaceString(0, firstHeaderOffset, notesToInsert)
            }
        }
    }
}
