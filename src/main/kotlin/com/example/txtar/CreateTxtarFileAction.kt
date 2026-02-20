package com.example.txtar

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

class CreateTxtarFileAction : CreateFileFromTemplateAction(
    "Txtar File",
    "Creates a new Txtar file",
    TxtarIcons.FILE
) {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle("New Txtar File")
            .addKind("Txtar File", TxtarIcons.FILE, "Txtar File")
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String = "Txtar File"
}
