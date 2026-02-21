package com.arran4.txtar

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class RemoveFileActionTest : BasePlatformTestCase() {

    fun testRemoveFirstFile() {
        myFixture.configureByText("test.txtar", """
            -- file1 --
            content1
            -- file2 --
            content2
        """.trimIndent())

        // Place caret on file1
        myFixture.editor.caretModel.moveToOffset(myFixture.file.text.indexOf("file1"))

        myFixture.testAction(RemoveFileAction())

        myFixture.checkResult("""
            -- file2 --
            content2
        """.trimIndent())
    }

    fun testRemoveMiddleFile() {
        myFixture.configureByText("test.txtar", """
            -- file1 --
            content1
            -- file2 --
            content2
            -- file3 --
            content3
        """.trimIndent())

        // Place caret on file2
        myFixture.editor.caretModel.moveToOffset(myFixture.file.text.indexOf("file2"))

        myFixture.testAction(RemoveFileAction())

        myFixture.checkResult("""
            -- file1 --
            content1
            -- file3 --
            content3
        """.trimIndent())
    }

    fun testRemoveLastFile() {
        myFixture.configureByText("test.txtar", """
            -- file1 --
            content1
            -- file2 --
            content2
        """.trimIndent())

        // Place caret on file2
        myFixture.editor.caretModel.moveToOffset(myFixture.file.text.indexOf("file2"))

        myFixture.testAction(RemoveFileAction())

        // Expect a trailing newline because content1 has one
        myFixture.checkResult("-- file1 --\ncontent1\n")
    }
}
