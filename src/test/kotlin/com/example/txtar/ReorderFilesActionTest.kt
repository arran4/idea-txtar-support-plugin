package com.example.txtar

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.example.txtar.ReorderFilesAction.Companion.parseFile
import com.example.txtar.ReorderFilesAction.Companion.buildText

class ReorderFilesActionTest : BasePlatformTestCase() {

    fun testParseSimple() {
        val content = "-- file1.txt --\ncontent1\n-- file2.txt --\ncontent2"
        val file = myFixture.configureByText("test.txtar", content)

        val (comment, entries) = parseFile(file)

        assertEquals("", comment)
        assertEquals(2, entries.size)

        assertEquals("file1.txt", entries[0].filename)
        assertEquals("-- file1.txt --\n", entries[0].headerText)
        assertEquals("content1\n", entries[0].contentText)

        assertEquals("file2.txt", entries[1].filename)
        assertEquals("-- file2.txt --\n", entries[1].headerText)
        assertEquals("content2", entries[1].contentText)
    }

    fun testParseWithComment() {
        val content = "comment block\n-- file1.txt --\ncontent1"
        val file = myFixture.configureByText("test.txtar", content)

        val (comment, entries) = parseFile(file)

        assertEquals("comment block\n", comment)
        assertEquals(1, entries.size)
        assertEquals("-- file1.txt --\n", entries[0].headerText)
        assertEquals("content1", entries[0].contentText)
    }

    fun testRebuild() {
        val comment = "comment\n"
        val entries = listOf(
            TxtarEntry("-- file2.txt --\n", "content2\n"),
            TxtarEntry("-- file1.txt --\n", "content1")
        )

        val result = buildText(comment, entries)
        val expected = "comment\n-- file2.txt --\ncontent2\n-- file1.txt --\ncontent1"

        assertEquals(expected, result)
    }

    fun testParseEmptyContent() {
        val content = "-- file1.txt --\n-- file2.txt --\n"
        val file = myFixture.configureByText("test.txtar", content)

        val (comment, entries) = parseFile(file)

        assertEquals("", comment)
        assertEquals(2, entries.size)
        assertEquals("-- file1.txt --\n", entries[0].headerText)
        assertEquals("", entries[0].contentText)
        assertEquals("-- file2.txt --\n", entries[1].headerText)
        assertEquals("", entries[1].contentText)
    }
}
