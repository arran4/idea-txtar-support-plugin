package com.github.arran4.idea.txtar

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class TxtarFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TxtarLanguage.INSTANCE) {
    override fun getFileType(): FileType = TxtarFileType.INSTANCE

    override fun toString(): String = "Txtar File"
}
