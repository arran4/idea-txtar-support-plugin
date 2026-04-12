package com.arran4.txtar

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.openapi.diagnostic.Logger
import java.io.IOException

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

            val descriptor = FileChooserDescriptor(true, true, false, false, false, true)
            descriptor.title = "Select Files to Include"

            val files = FileChooser.chooseFiles(descriptor, project, null)
            if (files.isEmpty()) return@invokeLater

            val content = StringBuilder()
            // Add a newline to separate from template content if needed
            content.append("\n")

            ProgressManager.getInstance().runProcessWithProgressSynchronously({
                for (file in files) {
                    processFile(file, "", content)
                }
            }, "Reading Files to Append", true, project)

            WriteCommandAction.runWriteCommandAction(project) {
                val documentManager = PsiDocumentManager.getInstance(project)
                val document = documentManager.getDocument(createdElement) ?: return@runWriteCommandAction
                document.insertString(document.textLength, content.toString())
                documentManager.commitDocument(document)
            }
        }
    }

    private fun processFile(file: VirtualFile, pathPrefix: String, content: StringBuilder) {
        val relativePath = if (pathPrefix.isEmpty()) file.name else "$pathPrefix/${file.name}"
        if (file.isDirectory) {
            for (child in file.children) {
                processFile(child, relativePath, content)
            }
        } else {
            try {
                content.append("-- $relativePath --\n")
                // Use VfsUtil.loadText to read file content
                content.append(VfsUtil.loadText(file))
                if (!content.endsWith("\n")) {
                    content.append("\n")
                }
            } catch (e: IOException) {
                Logger.getInstance(CreateTxtarFileAction::class.java).warn("Error reading file to append", e)
            }
        }
    }
}
