package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.DataFlavor

class PasteFileFromClipboardIntention : IntentionAction {
    override fun getText(): String = "Paste file from clipboard"
    override fun getFamilyName(): String = "Txtar"
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file !is TxtarFile || editor == null) return false
        val clipboardContent = CopyPasteManager.getInstance().getContents<String>(DataFlavor.stringFlavor)
        return !clipboardContent.isNullOrEmpty()
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is TxtarFile || editor == null) return
        val clipboardContent = CopyPasteManager.getInstance().getContents<String>(DataFlavor.stringFlavor) ?: return

        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        val (header, _) = TxtarFileEntryUtil.findFileEntry(element)

        val name = com.intellij.openapi.ui.Messages.showInputDialog(project, "Enter filename:", "Paste From Clipboard", com.intellij.openapi.ui.Messages.getQuestionIcon())
        if (name.isNullOrBlank()) return

        val (description, entries) = EditTxtarEntriesAction.parseFile(file)
        val mutableEntries = entries.toMutableList()
        val newEntry = TxtarEntry("-- $name --\n", clipboardContent)

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
