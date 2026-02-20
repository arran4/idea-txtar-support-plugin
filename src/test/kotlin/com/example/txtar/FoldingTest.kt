package com.arran4.txtar

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

class FoldingTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String {
        return File("testdata").absolutePath
    }

    fun testFolding() {
        myFixture.testFolding(testDataPath + "/folding/test_folding.txtar")
    }
}
