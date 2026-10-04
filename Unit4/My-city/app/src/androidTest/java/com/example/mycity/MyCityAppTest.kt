package com.example.mycity

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.mycity.data.LocalPlacesDataProvider
import com.example.mycity.model.Category
import com.example.mycity.ui.MyCityApp
import com.example.mycity.ui.theme.MyCityTheme
import org.junit.Rule
import org.junit.Test

class MyCityAppTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setApp(windowSize: WindowWidthSizeClass) {
        composeTestRule.setContent {
            MyCityTheme {
                MyCityApp(windowSize = windowSize)
            }
        }
    }

    private fun string(@StringRes id: Int) = composeTestRule.activity.getString(id)

    private fun onNodeWithStringId(@StringRes id: Int): SemanticsNodeInteraction =
        composeTestRule.onNodeWithText(string(id))

    @Test
    fun compactDevice_categoryAndPlaceClicked_detailsShownAndBackReturnsToList() {
        setApp(WindowWidthSizeClass.Compact)
        val place = LocalPlacesDataProvider.getPlaces(Category.Museums).first()

        // Categories screen without Up button
        onNodeWithStringId(Category.Museums.title).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(string(R.string.back_button))
            .assertDoesNotExist()

        // Category -> places
        onNodeWithStringId(Category.Museums.title).performClick()
        onNodeWithStringId(place.shortDescription).assertIsDisplayed()

        // Place -> details
        onNodeWithStringId(place.name).performClick()
        onNodeWithStringId(place.address).assertIsDisplayed()

        // Up -> places
        composeTestRule.onNodeWithContentDescription(string(R.string.back_button)).performClick()
        onNodeWithStringId(place.shortDescription).assertIsDisplayed()
    }

    @Test
    fun mediumDevice_usesNavigationRail() {
        setApp(WindowWidthSizeClass.Medium)
        composeTestRule.onNodeWithTag(string(R.string.navigation_rail)).assertIsDisplayed()
    }

    @Test
    fun mediumDevice_railCategoryClicked_placesOfCategoryShown() {
        setApp(WindowWidthSizeClass.Medium)
        val place = LocalPlacesDataProvider.getPlaces(Category.Theaters).first()

        onNodeWithStringId(Category.Theaters.shortTitle).performClick()

        onNodeWithStringId(place.name).assertIsDisplayed()
    }

    /**
     * Needs a device that is at least 840dp wide (tablet emulator, or a phone emulator with
     * `adb shell wm size 2560x1600` and `adb shell wm density 240`), otherwise the details pane
     * does not fit on the screen.
     */
    @Test
    fun expandedDevice_usesNavigationDrawerAndShowsListAndDetails() {
        setApp(WindowWidthSizeClass.Expanded)
        val place = LocalPlacesDataProvider.getPlaces(Category.Food)[1]

        composeTestRule.onNodeWithTag(string(R.string.navigation_drawer)).assertExists()
        composeTestRule.onAllNodesWithText(string(Category.Food.title)).onFirst().performClick()
        onNodeWithStringId(place.name).performClick()

        // The list is still visible next to the details pane
        onNodeWithStringId(place.shortDescription).assertIsDisplayed()
        composeTestRule.onNodeWithTag(string(R.string.details_pane)).assertIsDisplayed()
        onNodeWithStringId(place.description).assertExists()
    }
}
