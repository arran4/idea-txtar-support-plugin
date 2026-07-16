package com.arran4.txtar

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class ColorSettingsPage : ColorSettingsPage {
    private companion object {
        val DESCRIPTORS: Array<AttributesDescriptor> = arrayOf(
            AttributesDescriptor("Comment", TxtarSyntaxHighlighter.COMMENT),
            AttributesDescriptor("Header", TxtarSyntaxHighlighter.HEADER),
            AttributesDescriptor("Content", TxtarSyntaxHighlighter.CONTENT),
        )
    }

    override fun getIcon(): Icon = TxtarIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = TxtarSyntaxHighlighter()

    override fun getDemoText(): @NonNls String = """
            Some configuration files
            -- api.yaml --
            api:
                version: v1.2.3
                paths:
                    - "/"
                    -"/posts"
            -- api.json --
            {
                "api": {
                    "version": "v1.2.3",
                    "paths": [
                        "/",
                        "/posts",
                    ]
                }
            }
        """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String?, TextAttributesKey?>? = null

    override fun getAttributeDescriptors(): Array<out AttributesDescriptor?> = DESCRIPTORS

    override fun getColorDescriptors(): Array<out ColorDescriptor?> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): @NlsContexts.ConfigurableName String = "Txtar"
}