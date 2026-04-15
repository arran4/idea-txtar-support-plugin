package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.openapi.ui.Messages

class CreateNewEmptyFileIntention : IntentionAction {
    override fun getText(): String = "Create new empty file"
    override fun getFamilyName(): String = "Txtar"
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return file is TxtarFile && editor != null
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is TxtarFile || editor == null) return
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        val (header, _) = TxtarFileEntryUtil.findFileEntry(element)

        val name = Messages.showInputDialog(project, "Enter filename:", "Create New Empty File", Messages.getQuestionIcon())
        if (name.isNullOrBlank()) return

        val (description, entries) = EditTxtarEntriesAction.parseFile(file)
        val mutableEntries = entries.toMutableList()
        val newEntry = TxtarEntry("-- $name --\n", "")

        if (header != null) {
            val targetHeader = header.text
            val index = mutableEntries.indexOfFirst { it.headerText == targetHeader }
            if (index >= 0) {
                mutableEntries.add(index + 1, newEntry)
            } else {
                mutableEntries.add(newEntry)
            }
        } else {
            mutableEntries.add(newEntry)
        }

        val newText = EditTxtarEntriesAction.buildText(description, mutableEntries)
        WriteCommandAction.runWriteCommandAction(project) {
            file.viewProvider.document?.setText(newText)
        }
    }
    override fun startInWriteAction(): Boolean = false
}
