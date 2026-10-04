package com.example.lunchtray.test

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.lunchtray.LunchTrayApp
import com.example.lunchtray.LunchTrayScreen
import com.example.lunchtray.R
import com.example.lunchtray.datasource.DataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LunchTrayScreenNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupLunchTrayNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            LunchTrayApp(navController = navController)
        }
    }

    @Test
    fun lunchTrayNavHost_verifyStartDestination() {
        assertCurrentRoute(LunchTrayScreen.Start)
    }

    @Test
    fun lunchTrayNavHost_verifyBackNavigationNotShownOnStartScreen() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).assertDoesNotExist()
    }

    @Test
    fun lunchTrayNavHost_clickStartOrder_navigatesToEntreeScreen() {
        onNodeWithStringId(R.string.start_order).performClick()
        assertCurrentRoute(LunchTrayScreen.Entree)
    }

    @Test
    fun lunchTrayNavHost_fullOrder_navigatesThroughAllScreensToCheckout() {
        navigateToCheckout()
        assertCurrentRoute(LunchTrayScreen.Checkout)
    }

    @Test
    fun lunchTrayNavHost_clickUpOnSideDishScreen_navigatesToEntreeScreen() {
        navigateToSideDish()
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).performClick()
        assertCurrentRoute(LunchTrayScreen.Entree)
    }

    @Test
    fun lunchTrayNavHost_clickCancelOnAccompanimentScreen_navigatesToStartScreen() {
        navigateToAccompaniment()
        onNodeWithStringId(R.string.cancel).performScrollTo().performClick()
        assertCurrentRoute(LunchTrayScreen.Start)
    }

    @Test
    fun lunchTrayNavHost_clickSubmitOnCheckoutScreen_navigatesToStartScreen() {
        navigateToCheckout()
        onNodeWithStringId(R.string.submit).performScrollTo().performClick()
        assertCurrentRoute(LunchTrayScreen.Start)
    }

    private fun navigateToSideDish() {
        onNodeWithStringId(R.string.start_order).performClick()
        composeTestRule.onNodeWithText(DataSource.entreeMenuItems.first().name).performClick()
        onNodeWithStringId(R.string.next).performScrollTo().performClick()
    }

    private fun navigateToAccompaniment() {
        navigateToSideDish()
        composeTestRule.onNodeWithText(DataSource.sideDishMenuItems.first().name).performClick()
        onNodeWithStringId(R.string.next).performScrollTo().performClick()
    }

    private fun navigateToCheckout() {
        navigateToAccompaniment()
        composeTestRule.onNodeWithText(DataSource.accompanimentMenuItems.first().name)
            .performClick()
        onNodeWithStringId(R.string.next).performScrollTo().performClick()
    }

    // Menu buttons show their labels in uppercase, so the text is matched ignoring case
    private fun onNodeWithStringId(@StringRes id: Int): SemanticsNodeInteraction =
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(id), ignoreCase = true)

    private fun assertCurrentRoute(expected: LunchTrayScreen) {
        assertEquals(expected.name, navController.currentBackStackEntry?.destination?.route)
    }
}
