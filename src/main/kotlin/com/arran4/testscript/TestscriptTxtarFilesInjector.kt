package com.arran4.testscript

import com.arran4.txtar.TxtarLanguage
import com.intellij.lang.injection.general.Injection
import com.intellij.lang.injection.general.LanguageInjectionContributor
import com.intellij.lang.injection.general.SimpleInjection
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType

class TestscriptTxtarFilesInjector : LanguageInjectionContributor {
    override fun getInjection(context: PsiElement): Injection? {
        val type = context.elementType ?: return null
        if (type != TestscriptTokenTypes.TXTARFILES) return null
        return SimpleInjection(TxtarLanguage.INSTANCE, "", "", null)
    }
}
