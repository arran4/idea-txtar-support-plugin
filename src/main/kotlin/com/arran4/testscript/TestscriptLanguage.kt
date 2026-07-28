package com.arran4.testscript

import com.intellij.lang.Language

class TestscriptLanguage : Language("Testscript") {
    companion object {
        val INSTANCE = TestscriptLanguage()
    }
}