package com.arran4.txtar

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.CollectionListModel
import com.intellij.ui.JBSplitter
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.datatransfer.DataFlavor
import java.io.IOException
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import com.intellij.ui.AnActionButton
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.icons.AllIcons

class TxtarEntriesDialog(
    private val project: Project,
    initialDescription: String,
    initialEntries: List<TxtarEntry>
) : DialogWrapper(project, true) {

    var descriptionText: String = initialDescription
        private set

    private val entriesModel = CollectionListModel(initialEntries.map { it.clone() })
    private val entriesList = JBList(entriesModel)

    private val descriptionArea = JBTextArea(initialDescription)
    private val contentPreviewArea = JBTextArea()
    private val filenameField = JTextField()

    init {
        title = "Edit Txtar Entries"
        setOKButtonText("Save")
        init()
    }

    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel(BorderLayout(JBUI.scale(5), JBUI.scale(5)))

        // Top: Description
        val descriptionPanel = JPanel(BorderLayout())
        descriptionPanel.border = JBUI.Borders.emptyBottom(10)
        descriptionPanel.add(JLabel("Description:"), BorderLayout.NORTH)
        descriptionArea.rows = 4
        descriptionArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) { descriptionText = descriptionArea.text }
            override fun removeUpdate(e: DocumentEvent?) { descriptionText = descriptionArea.text }
            override fun changedUpdate(e: DocumentEvent?) { descriptionText = descriptionArea.text }
        })
        descriptionPanel.add(JBScrollPane(descriptionArea), BorderLayout.CENTER)

        mainPanel.add(descriptionPanel, BorderLayout.NORTH)

        // Bottom: Splitter for entries list and editor
        val splitter = JBSplitter(false, 0.4f)
        splitter.firstComponent = createListPanel()
        splitter.secondComponent = createEditorPanel()

        mainPanel.add(splitter, BorderLayout.CENTER)
        mainPanel.preferredSize = Dimension(JBUI.scale(800), JBUI.scale(600))

        return mainPanel
    }

    private fun createListPanel(): JComponent {
        entriesList.cellRenderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                if (value is TxtarEntry) {
                    text = value.filename.ifEmpty { "<unnamed>" }
                }
                return this
            }
        }

        entriesList.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                updateEditorPanel()
            }
        }

        val decorator = ToolbarDecorator.createDecorator(entriesList)
            .setAddAction {
                val descriptor = FileChooserDescriptor(true, false, false, false, false, true)
                descriptor.title = "Select Files to Append"
                val files = FileChooser.chooseFiles(descriptor, project, null)

                for (file in files) {
                    if (file.fileType.isBinary) {
                        val result = Messages.showYesNoDialog(
                            project,
                            "File '${file.name}' appears to be binary. Are you sure you want to add it?",
                            "Warning",
                            Messages.getWarningIcon()
                        )
                        if (result != Messages.YES) continue
                    }

                    try {
                        val content = com.intellij.openapi.vfs.VfsUtil.loadText(file)
                        val entry = TxtarEntry("-- ${file.name} --", content)
                        entriesModel.add(entry)
                    } catch (ex: IOException) {
                        Messages.showErrorDialog(project, "Error reading file ${file.name}: ${ex.message}", "Error")
                    }
                }
                if (files.isNotEmpty()) {
                    entriesList.selectedIndex = entriesModel.size - 1
                }
            }
            .setRemoveAction {
                val selectedIndices = entriesList.selectedIndices
                if (selectedIndices.isNotEmpty()) {
                    for (index in selectedIndices.sortedArray().reversedArray()) {
                        entriesModel.remove(index)
                    }
                }
            }
            .setMoveUpAction {
                val selectedIndex = entriesList.selectedIndex
                if (selectedIndex > 0) {
                    val entry = entriesModel.getElementAt(selectedIndex)
                    entriesModel.remove(selectedIndex)
                    entriesModel.add(selectedIndex - 1, entry)
                    entriesList.selectedIndex = selectedIndex - 1
                }
            }
            .setMoveDownAction {
                val selectedIndex = entriesList.selectedIndex
                if (selectedIndex < entriesModel.size - 1 && selectedIndex != -1) {
                    val entry = entriesModel.getElementAt(selectedIndex)
                    entriesModel.remove(selectedIndex)
                    entriesModel.add(selectedIndex + 1, entry)
                    entriesList.selectedIndex = selectedIndex + 1
                }
            }
            .addExtraAction(object : AnActionButton("Add Empty File", AllIcons.General.Add) {
                override fun actionPerformed(e: AnActionEvent) {
                    val name = Messages.showInputDialog(project, "Enter filename:", "Add Empty File", Messages.getQuestionIcon())
                    if (!name.isNullOrBlank()) {
                        entriesModel.add(TxtarEntry("-- $name --", ""))
                        entriesList.selectedIndex = entriesModel.size - 1
                    }
                }
            })
            .addExtraAction(object : AnActionButton("Add from Clipboard", AllIcons.Actions.MenuPaste) {
                override fun actionPerformed(e: AnActionEvent) {
                    val clipboardContent = CopyPasteManager.getInstance().getContents<String>(DataFlavor.stringFlavor)
                    if (clipboardContent.isNullOrEmpty()) {
                        Messages.showInfoMessage(project, "Clipboard is empty", "Info")
                        return
                    }
                    val name = Messages.showInputDialog(project, "Enter filename:", "Add From Clipboard", Messages.getQuestionIcon())
                    if (!name.isNullOrBlank()) {
                        entriesModel.add(TxtarEntry("-- $name --", clipboardContent))
                        entriesList.selectedIndex = entriesModel.size - 1
                    }
                }
            })

        val panel = decorator.createPanel()
        panel.border = JBUI.Borders.customLine(JBUI.CurrentTheme.CustomFrameDecorations.separatorForeground(), 1, 1, 1, 0)
        return panel
    }

    private fun createEditorPanel(): JComponent {
        val panel = JPanel(BorderLayout(JBUI.scale(5), JBUI.scale(5)))
        panel.border = JBUI.Borders.emptyLeft(5)

        val headerPanel = JPanel(BorderLayout())
        headerPanel.add(JLabel("Filename:"), BorderLayout.WEST)

        filenameField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) { updateSelectedEntry() }
            override fun removeUpdate(e: DocumentEvent?) { updateSelectedEntry() }
            override fun changedUpdate(e: DocumentEvent?) { updateSelectedEntry() }
        })
        headerPanel.add(filenameField, BorderLayout.CENTER)
        panel.add(headerPanel, BorderLayout.NORTH)

        contentPreviewArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) { updateSelectedEntry() }
            override fun removeUpdate(e: DocumentEvent?) { updateSelectedEntry() }
            override fun changedUpdate(e: DocumentEvent?) { updateSelectedEntry() }
        })
        panel.add(JBScrollPane(contentPreviewArea), BorderLayout.CENTER)

        // Initially disable editor if nothing is selected
        setEditorEnabled(false)

        return panel
    }

    private var isUpdatingEditor = false

    private fun updateEditorPanel() {
        val selectedValue = entriesList.selectedValue
        isUpdatingEditor = true
        if (selectedValue != null) {
            setEditorEnabled(true)
            filenameField.text = selectedValue.filename
            contentPreviewArea.text = selectedValue.contentText
        } else {
            setEditorEnabled(false)
            filenameField.text = ""
            contentPreviewArea.text = ""
        }
        isUpdatingEditor = false
    }

    private fun updateSelectedEntry() {
        if (isUpdatingEditor) return
        val selectedIndex = entriesList.selectedIndex
        if (selectedIndex >= 0) {
            val entry = entriesModel.getElementAt(selectedIndex)
            entry.filename = filenameField.text
            entry.contentText = contentPreviewArea.text
            // Notify list model to update rendering if filename changed
            entriesModel.setElementAt(entry, selectedIndex)
        }
    }

    private fun setEditorEnabled(enabled: Boolean) {
        filenameField.isEnabled = enabled
        contentPreviewArea.isEnabled = enabled
    }

    fun getEntries(): List<TxtarEntry> {
        return entriesModel.items
    }
}
