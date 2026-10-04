package com.example.mycity

import com.example.mycity.data.LocalPlacesDataProvider
import com.example.mycity.model.Category
import com.example.mycity.ui.CityScreen
import com.example.mycity.ui.CityViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CityViewModelTest {
    private val viewModel = CityViewModel()

    @Test
    fun dataProvider_everyCategory_hasAtLeastThreePlaces() {
        Category.entries.forEach { category ->
            assertTrue(LocalPlacesDataProvider.getPlaces(category).size >= 3)
        }
    }

    @Test
    fun cityViewModel_Initialization_FirstCategoryAndPlaceSelected() {
        val uiState = viewModel.uiState.value
        val firstCategory = Category.entries.first()

        assertEquals(firstCategory, uiState.currentCategory)
        assertEquals(LocalPlacesDataProvider.getPlaces(firstCategory), uiState.places)
        assertEquals(uiState.places.first(), uiState.currentPlace)
        assertFalse(uiState.isShowingPlaces)
        assertFalse(uiState.isShowingDetails)
    }

    @Test
    fun cityViewModel_CategorySelected_PlacesOfCategoryShown() {
        viewModel.selectCategory(Category.Food)

        val uiState = viewModel.uiState.value
        assertEquals(Category.Food, uiState.currentCategory)
        assertTrue(uiState.places.all { it.category == Category.Food })
        assertEquals(uiState.places.first(), uiState.currentPlace)
        assertTrue(uiState.isShowingPlaces)
        assertFalse(uiState.isShowingDetails)
    }

    @Test
    fun cityViewModel_PlaceSelected_DetailsShown() {
        val place = LocalPlacesDataProvider.getPlaces(Category.Theaters)[2]

        viewModel.selectPlace(place)

        val uiState = viewModel.uiState.value
        assertEquals(place, uiState.currentPlace)
        assertEquals(Category.Theaters, uiState.currentCategory)
        assertTrue(uiState.isShowingDetails)
    }

    @Test
    fun cityViewModel_BackToCategories_ScreenFlagsReset() {
        viewModel.selectPlace(LocalPlacesDataProvider.allPlaces.last())

        viewModel.onScreenShown(CityScreen.Places)
        assertTrue(viewModel.uiState.value.isShowingPlaces)
        assertFalse(viewModel.uiState.value.isShowingDetails)

        viewModel.onScreenShown(CityScreen.Categories)
        assertFalse(viewModel.uiState.value.isShowingPlaces)
        // The selected place is kept, so the details pane on large screens stays filled
        assertEquals(LocalPlacesDataProvider.allPlaces.last(), viewModel.uiState.value.currentPlace)
    }
}
