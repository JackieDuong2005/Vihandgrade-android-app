package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.SampleEssays
import com.example.ui.screens.CameraScanScreen
import com.example.ui.screens.GradingResultScreen
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.test.performClick
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

  @Test
  fun `grading result screen renders 3 tabs and can switch modes`() {
    val sample = SampleEssays.allSamples[0]
    composeTestRule.setContent {
      MyApplicationTheme {
        GradingResultScreen(
          result = sample,
          selectedErrorId = null,
          onSelectError = {},
          onGradeAnother = {}
        )
      }
    }

    // Verify all 3 tabs are present and visible
    composeTestRule.onNodeWithTag("tab_mode_canvas").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tab_mode_diff").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tab_mode_skills").assertIsDisplayed()

    // Test clicking tabs to switch modes smoothly
    composeTestRule.onNodeWithTag("tab_mode_diff").performClick()
    composeTestRule.onNodeWithTag("tab_mode_diff").assertIsDisplayed()

    composeTestRule.onNodeWithTag("tab_mode_skills").performClick()
    composeTestRule.onNodeWithTag("tab_mode_skills").assertIsDisplayed()

    composeTestRule.onNodeWithTag("tab_mode_canvas").performClick()
    composeTestRule.onNodeWithTag("tab_mode_canvas").assertIsDisplayed()
  }
}
