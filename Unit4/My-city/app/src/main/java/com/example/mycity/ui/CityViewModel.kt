package com.example.mycity.ui

import androidx.lifecycle.ViewModel
import com.example.mycity.data.LocalPlacesDataProvider
import com.example.mycity.model.Category
import com.example.mycity.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class CityScreen {
    Categories, Places, Details
}

data class CityUiState(
    val categories: List<Category> = Category.entries,
    val currentCategory: Category = Category.entries.first(),
    val places: List<Place> = emptyList(),
    val currentPlace: Place? = null,
    val isShowingPlaces: Boolean = false,
    val isShowingDetails: Boolean = false,
)

class CityViewModel(
    private val dataProvider: LocalPlacesDataProvider = LocalPlacesDataProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(stateForCategory(Category.entries.first()))
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    fun selectCategory(category: Category) {
        _uiState.update {
            stateForCategory(category).copy(isShowingPlaces = true)
        }
    }

    fun selectPlace(place: Place) {
        _uiState.update {
            it.copy(
                currentCategory = place.category,
                places = dataProvider.getPlaces(place.category),
                currentPlace = place,
                isShowingPlaces = true,
                isShowingDetails = true,
            )
        }
    }

    fun onScreenShown(screen: CityScreen) {
        _uiState.update {
            it.copy(
                isShowingPlaces = screen != CityScreen.Categories,
                isShowingDetails = screen == CityScreen.Details,
            )
        }
    }

    private fun stateForCategory(category: Category): CityUiState {
        val places = dataProvider.getPlaces(category)
        return CityUiState(
            currentCategory = category,
            places = places,
            currentPlace = places.firstOrNull(),
        )
    }
}
