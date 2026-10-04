package com.example.mycity.ui

import androidx.lifecycle.ViewModel
import com.example.mycity.data.LocalPlacesDataProvider
import com.example.mycity.model.Category
import com.example.mycity.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Screens of the app. On compact and medium windows they are destinations of the NavHost,
 * on expanded windows all of them are visible at once.
 */
enum class CityScreen {
    Categories, Places, Details
}

/**
 * UI state of the app.
 *
 * [isShowingPlaces] and [isShowingDetails] remember which screen the user is on, so that the
 * position is restored when the layout changes (e.g. a phone is rotated and the window size class
 * changes from compact to medium).
 */
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

    /**
     * Opens the list of places of [category]. The first place becomes selected, so the details
     * pane on large screens is never empty.
     */
    fun selectCategory(category: Category) {
        _uiState.update {
            stateForCategory(category).copy(isShowingPlaces = true)
        }
    }

    /**
     * Opens the details of [place].
     */
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

    /**
     * Called by the navigation host whenever a screen becomes visible (including system back
     * navigation), so the state always reflects what the user sees.
     */
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
