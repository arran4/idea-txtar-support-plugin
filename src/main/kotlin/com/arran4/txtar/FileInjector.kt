package com.arran4.txtar

import com.intellij.lang.LanguageUtil
import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.lang.injection.general.SimpleInjection
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType

class FileInjector : LanguageInjectionContributor {
    override fun getInjection(context: PsiElement): Injection? {
        val type = context.elementType ?: return null
        if (type != TxtarElementTypes.FILE_CONTENT) return null
        val header = context.parent.firstChild.takeIf { it.elementType == TxtarElementTypes.HEADER } ?: return null
        val text = header.text ?: return null
        val stripped = text.trim().removePrefix("-- ").removeSuffix(" --").trim()
        val fileName = stripped.substringAfterLast('/')
        val extension = stripped.substringAfterLast('.')
        val ftManager = FileTypeManager.getInstance()
        val fileType = ftManager.findFileTypeByName(fileName) ?: ftManager.getFileTypeByExtension(extension)
        val language =  LanguageUtil.getFileTypeLanguage(fileType) ?: return null
        return SimpleInjection(language, "", "", null)
    }
}