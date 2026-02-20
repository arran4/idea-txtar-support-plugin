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
            var i = 0

            if (i < children.size && children[i].node.elementType == TxtarElementTypes.COMMENT_BLOCK) {
                commentBlock = children[i].text
                i++
            }

            while (i < children.size) {
                val element = children[i]
                if (element.node.elementType == TxtarElementTypes.HEADER) {
                    val headerText = element.text
                    i++
                    var contentText = ""

                    // Look for content, skipping unknown elements
                    while (i < children.size) {
                        val type = children[i].node.elementType
                        if (type == TxtarElementTypes.FILE_CONTENT) {
                            contentText = children[i].text
                            i++
                            break
                        } else if (type == TxtarElementTypes.HEADER) {
                            // Next header found, so no content for this entry
                            break
                        } else {
                            // Skip unknown elements (e.g. whitespace if any)
                            i++
                        }
                    }
                    entries.add(TxtarEntry(headerText, contentText))
                } else {
                    // Skip unknown elements
                    i++
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
