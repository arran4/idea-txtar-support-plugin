package com.arran4.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiFile

class EditTxtarEntriesAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabledAndVisible = psiFile is TxtarFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? TxtarFile ?: return

        // 1. Parse
        val (description, entries) = parseFile(psiFile)

        // 2. Show Dialog
        val dialog = TxtarEntriesDialog(project, description, entries)
        if (dialog.showAndGet()) {
            val newDescription = dialog.descriptionText
            val newEntries = dialog.getEntries()

            // 3. Write
            val newText = buildText(newDescription, newEntries)

            WriteCommandAction.runWriteCommandAction(project) {
                val document = psiFile.viewProvider.document
                document?.setText(newText)
            }
        }
    }

    companion object {
        fun parseFile(psiFile: PsiFile): Pair<String, List<TxtarEntry>> {
            var description = ""
            val entries = mutableListOf<TxtarEntry>()

            val children = psiFile.children
            for (child in children) {
                if (child.node.elementType == TxtarElementTypes.COMMENT_BLOCK) {
                    description = child.text
                } else if (child.node.elementType == TxtarElementTypes.FILE_ENTRY) {
                    var headerText = ""
                    var contentText = ""
                    var grandChild = child.firstChild
                    while (grandChild != null) {
                        if (grandChild.node.elementType == TxtarElementTypes.HEADER) {
                            headerText = grandChild.text
                        } else if (grandChild.node.elementType == TxtarElementTypes.FILE_CONTENT) {
                            contentText = grandChild.text
                        }
                        grandChild = grandChild.nextSibling
                    }
                    if (headerText.isNotEmpty()) {
                        entries.add(TxtarEntry(headerText, contentText))
                    }
                }
            }
            return Pair(description, entries)
        }

        fun buildText(description: String, entries: List<TxtarEntry>): String {
            val sb = StringBuilder()
            if (description.isNotEmpty()) {
                sb.append(description)
                if (!description.endsWith("\n")) {
                    sb.append("\n")
                }
            }
            for (entry in entries) {
                if (sb.isNotEmpty() && !sb.endsWith("\n")) {
                    sb.append("\n")
                }
                sb.append(entry.headerText)
                sb.append(entry.contentText)
            }
            return sb.toString()
        }
    }
}
