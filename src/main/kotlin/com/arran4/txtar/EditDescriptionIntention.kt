package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiFile
import javax.swing.JComponent
import javax.swing.JTextArea

class EditDescriptionIntention : IntentionAction {
    override fun getText(): String = "Edit the description (in it's own editor)"
    override fun getFamilyName(): String = "Txtar"
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file !is TxtarFile || editor == null) return false
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        // Ensure we are inside the description/comment block, OR at the very top of the file
        if (element == null) return false
        return element.node.elementType == TxtarElementTypes.COMMENT_BLOCK || element.node.elementType == TxtarElementTypes.COMMENT || offset == 0
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is TxtarFile) return

        val (description, entries) = EditTxtarEntriesAction.parseFile(file)
        val textArea = JTextArea(description)
        textArea.rows = 10
        textArea.columns = 50
        val scrollPane = com.intellij.ui.components.JBScrollPane(textArea)

        val dialog = object : DialogWrapper(project, true) {
            init {
                title = "Edit Description"
                init()
            }
            override fun createCenterPanel(): JComponent = scrollPane
        }

        if (dialog.showAndGet()) {
            val newText = EditTxtarEntriesAction.buildText(textArea.text, entries)
            WriteCommandAction.runWriteCommandAction(project) {
                file.viewProvider.document?.setText(newText)
            }
        }
    }
    override fun startInWriteAction(): Boolean = false
}
