package com.arran4.testscript

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.psi.ElementManipulators
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.impl.source.tree.injected.InjectionBackgroundSuppressor

class TestscriptTxtarFilesElement(node: ASTNode) : ASTWrapperPsiElement(node), PsiLanguageInjectionHost, InjectionBackgroundSuppressor {
    override fun isValidHost(): Boolean {
        return true
    }

    override fun updateText(text: String): PsiLanguageInjectionHost? {
        return ElementManipulators.handleContentChange(this, text)
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost?> {
        return LiteralTextEscaper.createSimple(this)
    }

    internal class Manipulator : AbstractElementManipulator<TestscriptTxtarFilesElement>() {
        override fun handleContentChange(
            element: TestscriptTxtarFilesElement,
            range: TextRange,
            newContent: String?
        ): TestscriptTxtarFilesElement? {
            val oldText = element.text
            val newText =
                oldText.substring(0, range.startOffset) + (newContent ?: "") + oldText.substring(range.endOffset)
            val dummyFile = com.intellij.psi.PsiFileFactory.getInstance(element.project)
                .createFileFromText("dummy.txt", TestscriptLanguage.INSTANCE, "exec cmd\n$newText")
            val newElement = com.intellij.psi.util.PsiTreeUtil.findChildOfType(dummyFile, TestscriptTxtarFilesElement::class.java)
            return if (newElement != null) {
                element.replace(newElement) as TestscriptTxtarFilesElement
            } else {
                element
            }
        }
    }
}
