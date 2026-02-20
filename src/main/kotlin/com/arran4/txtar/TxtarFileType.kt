package com.arran4.txtar

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class TxtarFileType : LanguageFileType(TxtarLanguage.INSTANCE) {
    companion object {
        val INSTANCE = TxtarFileType()
    }

    override fun getName(): String = "Txtar"

    override fun getDescription(): String = "Txtar file"

    override fun getDefaultExtension(): String = "txtar"

    override fun getIcon(): Icon = TxtarIcons.FILE
}
