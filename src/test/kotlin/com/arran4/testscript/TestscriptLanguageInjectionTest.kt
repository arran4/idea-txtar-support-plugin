package com.arran4.testscript

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.InjectionTestFixture
import com.intellij.testFramework.fixtures.injectionForHost

class TestscriptLanguageInjectionTest : BasePlatformTestCase() {

    fun testTxtarInjectionInTestscript() {
        myFixture.configureByText("test.txt", """
            exec cmd
            -- foo.json --
            { "foo": "bar" }
        """.trimIndent())
        val injectionFixture = InjectionTestFixture(myFixture)
        injectionFixture
            .assertInjected(
                injectionForHost("-- foo.json --\n{ \"foo\": \"bar\" }").hasLanguage("Txtar")
            )
    }

    fun testNestedLanguageInjectionInTestscript() {
        myFixture.configureByText("test.txt", """
            exec cmd
            -- foo.json --
            { "foo": "bar" }
            
            -- bar.xml --
            <xml>test</xml>
        """.trimIndent())
        val injectionFixture = InjectionTestFixture(myFixture)
        injectionFixture
            .assertInjected(
                injectionForHost("-- foo.json --\n{ \"foo\": \"bar\" }\n\n-- bar.xml --\n<xml>test</xml>").hasLanguage("Txtar")
            )
    }
}
