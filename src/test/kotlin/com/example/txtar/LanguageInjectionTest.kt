package com.arran4.txtar

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.InjectionTestFixture
import com.intellij.testFramework.fixtures.injectionForHost
import java.io.File

class LanguageInjectionTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String {
        return File("testdata").absolutePath
    }

    fun testLanguageInjection() {
        myFixture.configureByText("test.txtar", """
            -- foo.json --
            { "foo": "bar" }
            
            -- foo.xml --
            <bar>foo</bar>
        """.trimIndent())
        val injectionFixture = InjectionTestFixture(myFixture)
        injectionFixture
            .assertInjected(
                injectionForHost("{ \"foo\": \"bar\" }\n\n").hasLanguage("JSON"),
                injectionForHost("<bar>foo</bar>").hasLanguage("XML"),
            )
    }
}