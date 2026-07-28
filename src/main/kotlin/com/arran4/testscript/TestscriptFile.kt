package com.arran4.testscript

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class TestscriptFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TestscriptLanguage.INSTANCE) {
    override fun getFileType(): FileType = TestscriptFileType.INSTANCE

    override fun toString(): String = "Testscript File"
}
