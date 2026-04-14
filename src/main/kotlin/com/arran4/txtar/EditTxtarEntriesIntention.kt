package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.openapi.command.WriteCommandAction

class EditTxtarEntriesIntention : IntentionAction {
    override fun getText(): String = "Edit txtar entries..."

    override fun getFamilyName(): String = "Txtar"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return file is TxtarFile
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is TxtarFile) return

        // 1. Parse
        val (description, entries) = EditTxtarEntriesAction.parseFile(file)

        // 2. Show Dialog
        val dialog = TxtarEntriesDialog(project, description, entries)
        if (dialog.showAndGet()) {
            val newDescription = dialog.descriptionText
            val newEntries = dialog.getEntries()

            // 3. Write
            val newText = EditTxtarEntriesAction.buildText(newDescription, newEntries)

            WriteCommandAction.runWriteCommandAction(project) {
                val document = file.viewProvider.document
                document?.setText(newText)
            }
        }
    }

    override fun startInWriteAction(): Boolean = false
}
