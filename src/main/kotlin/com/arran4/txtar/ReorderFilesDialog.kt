package com.arran4.txtar

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JComponent
import javax.swing.JList

data class TxtarEntry(val headerText: String, val contentText: String) {
    val filename: String by lazy {
        val text = headerText.trim()
        if (text.startsWith("-- ") && text.endsWith(" --")) {
            text.substring(3, text.length - 3).trim()
        } else {
            text
        }
    }
}

class ReorderFilesDialog(project: Project, entries: List<TxtarEntry>) : DialogWrapper(project, true) {
    private val model = CollectionListModel(entries)
    private val list = JBList(model)

    init {
        title = "Reorder Files"
        init()
    }

    override fun createCenterPanel(): JComponent {
        list.cellRenderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                if (value is TxtarEntry) {
                    text = value.filename
                }
                return this
            }
        }

        return ToolbarDecorator.createDecorator(list)
            .disableAddAction()
            .disableRemoveAction()
            .createPanel()
    }

    fun getEntries(): List<TxtarEntry> {
        return model.items
    }
}
