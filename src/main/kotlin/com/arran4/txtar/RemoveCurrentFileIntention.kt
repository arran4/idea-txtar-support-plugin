package com.arran4.txtar

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class RemoveCurrentFileIntention : IntentionAction {
    override fun getText(): String = "Remove this file"
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
            WriteCommandAction.runWriteCommandAction(project) {
                content?.delete()
                header?.delete()
            }
        }
    }
    override fun startInWriteAction(): Boolean = false
}
