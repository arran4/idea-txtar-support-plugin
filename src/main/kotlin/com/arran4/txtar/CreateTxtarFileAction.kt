package com.arran4.txtar

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import java.io.IOException

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

    override fun postProcess(createdElement: PsiFile, templateName: String?, customProperties: Map<String, String>?) {
        super.postProcess(createdElement, templateName, customProperties)

        val project = createdElement.project
        ApplicationManager.getApplication().invokeLater {
            if (!createdElement.isValid) return@invokeLater

            val descriptor = FileChooserDescriptor(true, true, false, false, false, true)
            descriptor.title = "Select Files to Include"

            val files = FileChooser.chooseFiles(descriptor, project, null)
            if (files.isEmpty()) return@invokeLater

            val content = StringBuilder()
            // Add a newline to separate from template content if needed
            content.append("\n")

            for (file in files) {
                processFile(file, "", content)
            }

            WriteCommandAction.runWriteCommandAction(project) {
                val documentManager = PsiDocumentManager.getInstance(project)
                val document = documentManager.getDocument(createdElement) ?: return@runWriteCommandAction
                document.insertString(document.textLength, content.toString())
                documentManager.commitDocument(document)
            }
        }
    }

    private fun processFile(file: VirtualFile, pathPrefix: String, content: StringBuilder) {
        if (file.isDirectory) {
            for (child in file.children) {
                processFile(child, if (pathPrefix.isEmpty()) file.name else "$pathPrefix/${file.name}", content)
            }
        } else {
            try {
                val relativePath = if (pathPrefix.isEmpty()) file.name else "$pathPrefix/${file.name}"
                content.append("-- $relativePath --\n")
                // Use VfsUtil.loadText to read file content
                content.append(VfsUtil.loadText(file))
                if (!content.endsWith("\n")) {
                    content.append("\n")
                }
            } catch (e: IOException) {
                // Ignore errors reading files
            }
        }
    }
}
