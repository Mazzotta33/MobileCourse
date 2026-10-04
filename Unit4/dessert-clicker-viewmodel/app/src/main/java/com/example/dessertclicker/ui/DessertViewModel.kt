/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dessertclicker.ui

import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel that holds the Dessert Clicker app data and logic. The state survives configuration
 * changes, so the Activity no longer has to save it with rememberSaveable.
 */
class DessertViewModel(
    private val desserts: List<Dessert> = Datasource.dessertList
) : ViewModel() {

    private val _dessertUiState = MutableStateFlow(DessertUiState())
    val dessertUiState: StateFlow<DessertUiState> = _dessertUiState.asStateFlow()

    /**
     * Sells the current dessert: updates revenue and sold count and picks the next dessert to show.
     */
    fun onDessertClicked() {
        _dessertUiState.update { currentState ->
            val dessertsSold = currentState.dessertsSold + 1
            val nextDessertIndex = determineDessertIndex(dessertsSold)
            currentState.copy(
                currentDessertIndex = nextDessertIndex,
                revenue = currentState.revenue + currentState.currentDessertPrice,
                dessertsSold = dessertsSold,
                currentDessertImageId = desserts[nextDessertIndex].imageId,
                currentDessertPrice = desserts[nextDessertIndex].price
            )
        }
    }

    /**
     * Determine which dessert to show.
     */
    private fun determineDessertIndex(dessertsSold: Int): Int {
        var dessertIndex = 0
        for (index in desserts.indices) {
            if (dessertsSold >= desserts[index].startProductionAmount) {
                dessertIndex = index
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more
                // desserts, you'll start producing more expensive desserts as determined by
                // startProductionAmount. We know to break as soon as we see a dessert whose
                // "startProductionAmount" is greater than the amount sold.
                break
            }
        }
        return dessertIndex
    }
}
