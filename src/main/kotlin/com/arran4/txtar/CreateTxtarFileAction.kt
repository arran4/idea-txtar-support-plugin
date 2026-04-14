package com.arran4.txtar

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile

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

    override fun postProcess(createdElement: PsiFile, templateName: String?, customProperties: Map<String, String>?) {
        super.postProcess(createdElement, templateName, customProperties)

        val project = createdElement.project
        ApplicationManager.getApplication().invokeLater {
            if (!createdElement.isValid) return@invokeLater

            val dialog = TxtarEntriesDialog(project, "", emptyList())
            if (dialog.showAndGet()) {
                val newDescription = dialog.descriptionText
                val newEntries = dialog.getEntries()

                val content = EditTxtarEntriesAction.buildText(newDescription, newEntries)

                WriteCommandAction.runWriteCommandAction(project) {
                    val documentManager = PsiDocumentManager.getInstance(project)
                    val document = documentManager.getDocument(createdElement) ?: return@runWriteCommandAction

                    // Replace the template content with the dialog content
                    document.setText(content)
                    documentManager.commitDocument(document)
                }
            }
        }
    }
}
