package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.CameraScanScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ViHand Grade", appName)
  }

  @Test
  fun `camera scan screen renders controls and viewfinder`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        CameraScanScreen(
          onCapture = {},
          onClose = {}
        )
      }
    }

    composeTestRule.onNodeWithTag("camera_scan_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cam_ratio_switcher").assertIsDisplayed()
    composeTestRule.onNodeWithTag("shutter_btn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("close_camera_btn").assertIsDisplayed()
  }
}
