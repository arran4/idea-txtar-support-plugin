package com.arran4.txtar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiFile

class ReorderFilesAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isEnabledAndVisible = psiFile is TxtarFile
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? TxtarFile ?: return

        // 1. Parse
        val (commentBlock, entries) = parseFile(psiFile)

        // 2. Show Dialog
        val dialog = ReorderFilesDialog(project, entries)
        if (dialog.showAndGet()) {
            val newEntries = dialog.getEntries()

            // 3. Write
            val newText = buildText(commentBlock, newEntries)

            WriteCommandAction.runWriteCommandAction(project) {
                val document = psiFile.viewProvider.document
                document?.setText(newText)
            }
        }
    }

    companion object {
        fun parseFile(psiFile: PsiFile): Pair<String, List<TxtarEntry>> {
            var commentBlock = ""
            val entries = mutableListOf<TxtarEntry>()

            val children = psiFile.children
            for (child in children) {
                if (child.node.elementType == TxtarElementTypes.COMMENT_BLOCK) {
                    commentBlock = child.text
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
            return Pair(commentBlock, entries)
        }

        fun buildText(commentBlock: String, entries: List<TxtarEntry>): String {
            val sb = StringBuilder()
            sb.append(commentBlock)
            for (entry in entries) {
                sb.append(entry.headerText)
                sb.append(entry.contentText)
            }
            return sb.toString()
        }
    }
}
