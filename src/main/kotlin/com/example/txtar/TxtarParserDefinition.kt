package com.arran4.txtar

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
import com.intellij.extapi.psi.ASTWrapperPsiElement

class TxtarParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(TxtarLanguage.INSTANCE)
    }

    override fun createLexer(project: Project?): Lexer = TxtarLexer()

    override fun createParser(project: Project?): PsiParser = PsiParser { root, builder ->
        val rootMarker = builder.mark()
        
        // Initial comment block
        if (builder.tokenType == TxtarElementTypes.COMMENT) {
            val commentMarker = builder.mark()
            while (builder.tokenType == TxtarElementTypes.COMMENT) {
                builder.advanceLexer()
            }
            commentMarker.done(TxtarElementTypes.COMMENT_BLOCK)
        }
        
        while (!builder.eof()) {
            if (builder.tokenType == TxtarElementTypes.HEADER) {
                builder.advanceLexer()
                
                if (builder.tokenType == TxtarElementTypes.CONTENT) {
                    val contentMarker = builder.mark()
                    while (builder.tokenType == TxtarElementTypes.CONTENT) {
                        builder.advanceLexer()
                    }
                    contentMarker.done(TxtarElementTypes.FILE_CONTENT)
                }
            } else {
                // Should be unreachable if lexer is correct, but safe fallback
                builder.advanceLexer()
            }
        }
        rootMarker.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode): PsiElement = ASTWrapperPsiElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = TxtarFile(viewProvider)
}
