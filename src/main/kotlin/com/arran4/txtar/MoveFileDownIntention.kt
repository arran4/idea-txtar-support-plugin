package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class MoveFileDownIntention : IntentionAction {
    override fun getText(): String = "Move this file down"
    override fun getFamilyName(): String = "Txtar"
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file !is TxtarFile || editor == null) return false
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        val (header, content) = TxtarFileEntryUtil.findFileEntry(element)
        if (header == null && content == null) return false

        // Can only move down if it's not the last entry
        val (_, entries) = EditTxtarEntriesAction.parseFile(file)
        val targetHeader = header?.text ?: return false
        val index = entries.indexOfFirst { it.headerText == targetHeader }
        return index >= 0 && index < entries.size - 1
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is TxtarFile || editor == null) return
        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        val (header, _) = TxtarFileEntryUtil.findFileEntry(element)
        val targetHeader = header?.text ?: return

        val (description, entries) = EditTxtarEntriesAction.parseFile(file)
        val index = entries.indexOfFirst { it.headerText == targetHeader }
        if (index >= 0 && index < entries.size - 1) {
            val mutableEntries = entries.toMutableList()
            val entry = mutableEntries.removeAt(index)
            mutableEntries.add(index + 1, entry)
            val newText = EditTxtarEntriesAction.buildText(description, mutableEntries)
            WriteCommandAction.runWriteCommandAction(project) {
                file.viewProvider.document?.setText(newText)
            }
        }
    }
    override fun startInWriteAction(): Boolean = false
}
