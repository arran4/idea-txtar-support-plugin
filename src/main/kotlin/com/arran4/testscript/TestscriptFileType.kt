package com.arran4.testscript

import com.intellij.openapi.fileTypes.LanguageFileType

class TestscriptFileType : LanguageFileType(TestscriptLanguage.INSTANCE) {
    companion object {
        val INSTANCE = TestscriptFileType()
    }

    override fun getName() = "Testscript"

    override fun getDescription() = "Testscript file"

    override fun getDefaultExtension() = "txt"

    override fun getIcon() = TestscriptIcons.FILE

}