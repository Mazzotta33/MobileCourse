package com.example.mycity.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mycity.R
import com.example.mycity.model.Category
import com.example.mycity.model.Place
import com.example.mycity.ui.theme.MyCityTheme

/**
 * Root composable. The layout depends on the window width:
 * - compact: NavHost with three screens (categories -> places -> details);
 * - medium: categories in a navigation rail, NavHost with places -> details;
 * - expanded: categories in a permanent navigation drawer, places and details side by side.
 */
@Composable
fun MyCityApp(
    windowSize: WindowWidthSizeClass,
    viewModel: CityViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (windowSize) {
        WindowWidthSizeClass.Expanded -> CityListAndDetailApp(
            uiState = uiState,
            onCategoryClick = viewModel::selectCategory,
            onPlaceClick = viewModel::selectPlace,
        )

        WindowWidthSizeClass.Medium -> CityNavigationApp(
            uiState = uiState,
            viewModel = viewModel,
            showCategoriesInRail = true,
        )

        else -> CityNavigationApp(
            uiState = uiState,
            viewModel = viewModel,
            showCategoriesInRail = false,
        )
    }
}

/**
 * Compact and medium layouts, where screens are switched by the Navigation component
 */
@Composable
private fun CityNavigationApp(
    uiState: CityUiState,
    viewModel: CityViewModel,
    showCategoriesInRail: Boolean,
    navController: NavHostController = rememberNavController(),
) {
    val startScreen = if (showCategoriesInRail) CityScreen.Places else CityScreen.Categories
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route?.let { CityScreen.valueOf(it) }
        ?: startScreen

    val title = when (currentScreen) {
        CityScreen.Categories -> stringResource(R.string.app_name)
        else -> stringResource(uiState.currentCategory.title)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        if (showCategoriesInRail) {
            CategoriesNavigationRail(
                categories = uiState.categories,
                currentCategory = uiState.currentCategory,
                onCategoryClick = { category ->
                    viewModel.selectCategory(category)
                    navController.popBackStack(CityScreen.Places.name, inclusive = false)
                },
                modifier = Modifier.testTag(stringResource(R.string.navigation_rail)),
            )
        }
        Scaffold(
            topBar = {
                CityTopAppBar(
                    title = title,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    onNavigateUp = { navController.navigateUp() },
                )
            },
            modifier = Modifier.weight(1f),
        ) { innerPadding ->
            val contentPadding = innerPadding.withVerticalSpacing()
            NavHost(
                navController = navController,
                startDestination = startScreen.name,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(route = CityScreen.Categories.name) {
                    CategoriesScreen(
                        categories = uiState.categories,
                        onCategoryClick = { category ->
                            viewModel.selectCategory(category)
                            navController.navigate(CityScreen.Places.name)
                        },
                        contentPadding = contentPadding,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                composable(route = CityScreen.Places.name) {
                    PlacesList(
                        places = uiState.places,
                        onPlaceClick = { place ->
                            viewModel.selectPlace(place)
                            navController.navigate(CityScreen.Details.name)
                        },
                        contentPadding = contentPadding,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                composable(route = CityScreen.Details.name) {
                    uiState.currentPlace?.let { place ->
                        PlaceDetails(
                            place = place,
                            contentPadding = contentPadding,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }

    val initialState = remember { uiState }
    var positionRestored by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!positionRestored) {
            positionRestored = true
            if (!showCategoriesInRail && initialState.isShowingPlaces) {
                navController.navigate(CityScreen.Places.name)
            }
            if (initialState.isShowingDetails && initialState.currentPlace != null) {
                navController.navigate(CityScreen.Details.name)
            }
        }
    }
    LaunchedEffect(currentScreen) {
        viewModel.onScreenShown(currentScreen)
    }
}

@Composable
private fun CityListAndDetailApp(
    uiState: CityUiState,
    onCategoryClick: (Category) -> Unit,
    onPlaceClick: (Place) -> Unit,
) {
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(modifier = Modifier.width(320.dp)) {
                CategoriesDrawerContent(
                    categories = uiState.categories,
                    currentCategory = uiState.currentCategory,
                    onCategoryClick = onCategoryClick,
                )
            }
        },
        modifier = Modifier.testTag(stringResource(R.string.navigation_drawer)),
    ) {
        Scaffold(
            topBar = {
                CityTopAppBar(
                    title = stringResource(uiState.currentCategory.title),
                    canNavigateBack = false,
                    onNavigateUp = {},
                )
            }
        ) { innerPadding ->
            val contentPadding = innerPadding.withVerticalSpacing()
            Row(modifier = Modifier.fillMaxSize()) {
                PlacesList(
                    places = uiState.places,
                    onPlaceClick = onPlaceClick,
                    selectedPlace = uiState.currentPlace,
                    contentPadding = contentPadding,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
                uiState.currentPlace?.let { place ->
                    PlaceDetails(
                        place = place,
                        contentPadding = contentPadding,
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .testTag(stringResource(R.string.details_pane)),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoriesNavigationRail(
    categories: List<Category>,
    currentCategory: Category,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(modifier = modifier) {
        Spacer(Modifier.height(8.dp))
        categories.forEach { category ->
            NavigationRailItem(
                selected = category == currentCategory,
                onClick = { onCategoryClick(category) },
                icon = { Icon(imageVector = category.icon, contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(category.shortTitle),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

@Composable
private fun CategoriesDrawerContent(
    categories: List<Category>,
    currentCategory: Category,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(12.dp)) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Text(
            text = stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        )
        categories.forEach { category ->
            NavigationDrawerItem(
                selected = category == currentCategory,
                onClick = { onCategoryClick(category) },
                icon = { Icon(imageVector = category.icon, contentDescription = null) },
                label = { Text(text = stringResource(category.title)) },
            )
        }
    }
}

/**
 * Scaffold padding plus some space above the first and below the last item of a list
 */
@Composable
private fun PaddingValues.withVerticalSpacing(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(layoutDirection),
        top = calculateTopPadding() + 16.dp,
        end = calculateEndPadding(layoutDirection),
        bottom = calculateBottomPadding() + 16.dp,
    )
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun MyCityAppCompactPreview() {
    MyCityTheme {
        MyCityApp(windowSize = WindowWidthSizeClass.Compact)
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun MyCityAppMediumPreview() {
    MyCityTheme {
        MyCityApp(windowSize = WindowWidthSizeClass.Medium)
    }
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
fun MyCityAppExpandedPreview() {
    MyCityTheme {
        MyCityApp(windowSize = WindowWidthSizeClass.Expanded)
    }
}
