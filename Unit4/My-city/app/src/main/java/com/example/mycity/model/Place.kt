package com.example.mycity.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.Tour
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mycity.R

/**
 * Category of recommendations shown on the first screen
 */
enum class Category(
    @StringRes val title: Int,
    @StringRes val shortTitle: Int,
    @StringRes val description: Int,
    val icon: ImageVector,
) {
    Sights(
        title = R.string.category_sights,
        shortTitle = R.string.category_sights_short,
        description = R.string.category_sights_description,
        icon = Icons.Filled.Tour,
    ),
    Museums(
        title = R.string.category_museums,
        shortTitle = R.string.category_museums_short,
        description = R.string.category_museums_description,
        icon = Icons.Filled.Museum,
    ),
    Parks(
        title = R.string.category_parks,
        shortTitle = R.string.category_parks_short,
        description = R.string.category_parks_description,
        icon = Icons.Filled.Park,
    ),
    Food(
        title = R.string.category_food,
        shortTitle = R.string.category_food_short,
        description = R.string.category_food_description,
        icon = Icons.Filled.RestaurantMenu,
    ),
    Theaters(
        title = R.string.category_theaters,
        shortTitle = R.string.category_theaters_short,
        description = R.string.category_theaters_description,
        icon = Icons.Filled.TheaterComedy,
    ),
}

/**
 * A recommended place (or, for [Category.Food], a dish) in the city
 */
data class Place(
    val id: Int,
    val category: Category,
    @StringRes val name: Int,
    @StringRes val shortDescription: Int,
    @StringRes val address: Int,
    @StringRes val description: Int,
    val icon: ImageVector,
)
