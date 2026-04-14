package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.openapi.ui.Messages

class CalculateFileSizeIntention : IntentionAction {
    override fun getText(): String = "Calculate file size"

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

        val (_, content) = TxtarFileEntryUtil.findFileEntry(element)

        val size = content?.textLength ?: 0
        Messages.showInfoMessage(project, "Size: $size bytes", "File Size")
    }

    override fun startInWriteAction(): Boolean = false
}
