package com.example.dessertclicker.ui

import com.example.dessertclicker.data.Datasource
import org.junit.Assert.assertEquals
import org.junit.Test

class DessertViewModelTest {
    private val desserts = Datasource.dessertList
    private val viewModel = DessertViewModel(desserts)

    @Test
    fun dessertViewModel_Initialization_FirstDessertShown() {
        val uiState = viewModel.dessertUiState.value
        assertEquals(0, uiState.revenue)
        assertEquals(0, uiState.dessertsSold)
        assertEquals(desserts.first().imageId, uiState.currentDessertImageId)
        assertEquals(desserts.first().price, uiState.currentDessertPrice)
    }

    @Test
    fun dessertViewModel_DessertClicked_RevenueAndSoldUpdated() {
        viewModel.onDessertClicked()

        val uiState = viewModel.dessertUiState.value
        assertEquals(desserts.first().price, uiState.revenue)
        assertEquals(1, uiState.dessertsSold)
    }

    @Test
    fun dessertViewModel_EnoughDessertsSold_NextDessertShown() {
        // Second dessert starts being produced after desserts[1].startProductionAmount sales
        repeat(desserts[1].startProductionAmount) { viewModel.onDessertClicked() }

        val uiState = viewModel.dessertUiState.value
        assertEquals(desserts.first().price * desserts[1].startProductionAmount, uiState.revenue)
        assertEquals(1, uiState.currentDessertIndex)
        assertEquals(desserts[1].imageId, uiState.currentDessertImageId)
        assertEquals(desserts[1].price, uiState.currentDessertPrice)
    }
}
