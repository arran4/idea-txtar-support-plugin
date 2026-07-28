package com.arran4.testscript

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class TestscriptParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(TestscriptLanguage.INSTANCE)
    }

    override fun createLexer(project: Project?): Lexer = TestscriptLexer()

    override fun createParser(project: Project?): PsiParser = PsiParser { root, builder ->
        val rootMarker = builder.mark()
        while (!builder.eof()) {
            if (builder.tokenType == TestscriptTokenTypes.TXTARFILES) {
                val txtarMarker = builder.mark()
                builder.advanceLexer()
                txtarMarker.done(TestscriptTokenTypes.TXTARFILES)
            } else {
                builder.advanceLexer()
            }
        }
        rootMarker.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = TokenSet.create(
        TestscriptTokenTypes.COMMENT,
        TestscriptTokenTypes.PHASE_HEADER
    )

    override fun getStringLiteralElements(): TokenSet = TokenSet.create(
        TestscriptTokenTypes.QUOTED_STRING
    )

    override fun createElement(node: ASTNode): PsiElement {
        if (node.elementType == TestscriptTokenTypes.TXTARFILES) {
            return TestscriptTxtarFilesElement(node)
        }
        return ASTWrapperPsiElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile = TestscriptFile(viewProvider)
}
