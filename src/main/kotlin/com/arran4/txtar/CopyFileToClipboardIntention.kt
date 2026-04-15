package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection

class CopyFileToClipboardIntention : IntentionAction {
    override fun getText(): String = "Copy file to clipboard"
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

        if (header != null || content != null) {
            val hText = header?.text ?: ""
            val cText = content?.text ?: ""
            CopyPasteManager.getInstance().setContents(StringSelection(hText + cText))
        }
    }
    override fun startInWriteAction(): Boolean = false
}
