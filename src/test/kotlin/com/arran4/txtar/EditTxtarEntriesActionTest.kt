package com.arran4.txtar

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.DataContext

class EditTxtarEntriesActionTest : BasePlatformTestCase() {
    fun testActionIsEnabledOnTxtarFile() {
        val file = myFixture.configureByText(TxtarFileType.INSTANCE, "This is a test\n")
        val action = EditTxtarEntriesAction()

        val presentation = Presentation()
        val event = AnActionEvent.createFromDataContext("Place", presentation, DataContext { dataId ->
            when (dataId) {
                CommonDataKeys.PSI_FILE.name -> file
                CommonDataKeys.PROJECT.name -> project
                else -> null
            }
        })

        action.update(event)

        assertTrue("Action should be enabled and visible", presentation.isEnabledAndVisible)
    }
}
