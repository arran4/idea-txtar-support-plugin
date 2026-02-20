package com.arran4.txtar

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

class CreateTxtarFileAction : CreateFileFromTemplateAction(
    "Txtar File",
    "Create new Txtar file",
    TxtarIcons.FILE
), DumbAware {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("New Txtar File")
            .addKind("Txtar File", TxtarIcons.FILE, "Txtar File")
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Create Txtar File $newName"
    }
}
