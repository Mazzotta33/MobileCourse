# Моя Казань (My City) — самостоятельная работа Unit 4

Итоговое практическое приложение четвёртого юнита. Задание курса: сделать гид по выбранному городу с категориями мест, списком, подробностями, навигацией и адаптивным интерфейсом. Город в задании не указан, здесь выбрана **Казань**.

В файле три части: **что делает приложение**, **как оно устроено (с кодом)** и **словарь терминов**.

---

## 1. Что делает приложение

Три уровня данных: **категория → список мест → подробности места**.

- 5 категорий: достопримечательности, музеи, парки, еда, театры.
- 24 рекомендации: в «Еде», «Музеях», «Достопримечательностях» и «Театрах» по 5, в «Парках» 4 (в категории «Еда» вместо мест — блюда).
- У места: название, краткое описание, адрес и полное описание.
- Вместо фотографий используются векторные иконки на цветном градиенте категории.
- Светлая и тёмная тема (палитра на основе зелёного цвета флага Татарстана, красный как акцент).
- Приложение подстраивается под ширину окна: телефон, складной экран и планшет показывают разные раскладки.

Технические параметры: `minSdk = 24`, `compileSdk = 34`, Compose, Material 3, `navigation-compose:2.8.5`, `lifecycle-viewmodel-compose:2.8.7`, `material3-window-size-class`, `material-icons-extended`.

## 2. Структура проекта

```
app/src/
├── main/java/com/example/mycity/
│   ├── MainActivity.kt                    calculateWindowSizeClass, запуск UI
│   ├── model/Place.kt                     Category (enum) и Place (data class)
│   ├── data/LocalPlacesDataProvider.kt    24 места, getPlaces(category)
│   ├── ui/CityViewModel.kt                CityScreen, CityUiState, CityViewModel
│   ├── ui/MyCityApp.kt                    выбор раскладки, NavHost, rail, drawer
│   ├── ui/CityScreens.kt                  экраны и карточки
│   └── ui/theme/                          Color, Theme, градиенты категорий
├── test/.../CityViewModelTest.kt          локальные тесты (JVM)
└── androidTest/.../MyCityAppTest.kt       UI-тесты (эмулятор)
```

Слои (архитектура из курса):

```
data (LocalPlacesDataProvider)  →  ViewModel (CityViewModel, StateFlow)  →  UI (MyCityApp, CityScreens)
                                      ↑ события: selectCategory, selectPlace, onScreenShown
```

Состояние течёт **вниз** (от `ViewModel` к UI), события — **вверх** (от UI к `ViewModel`). Это **однонаправленный поток данных (UDF)**.

---

## 3. Разбор кода

### 3.1. Точка входа — `MainActivity`

```kotlin
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MyCityTheme {
                Surface {
                    val windowSize = calculateWindowSizeClass(this)
                    MyCityApp(windowSize = windowSize.widthSizeClass)
                }
            }
        }
    }
}
```

- `calculateWindowSizeClass(this)` — вычисляет **класс размера окна** по текущим размерам Activity. Пересчитывается при повороте, раскрытии складного экрана, изменении размера окна.
- `windowSize.widthSizeClass` — берём только ширину: `Compact` (до 600 dp), `Medium` (600–840 dp), `Expanded` (от 840 dp).
- `Surface` — контейнер с цветом фона из темы.
- В `MyCityApp` передаётся только класс ширины, а не весь `WindowSizeClass`: composable не зависит от Activity и легко тестируется.

### 3.2. Модель — `Category` и `Place`

```kotlin
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
    Museums(...), Parks(...), Food(...), Theaters(...)
}

data class Place(
    val id: Int,
    val category: Category,
    @StringRes val name: Int,
    @StringRes val shortDescription: Int,
    @StringRes val address: Int,
    @StringRes val description: Int,
    val icon: ImageVector,
)
```

- `enum class` с параметрами: у каждой категории свои название, короткое название (для узкой `NavigationRail`), описание и иконка.
- `Category.entries` — список всех значений enum (замена `values()` в современном Kotlin). Используется в `ViewModel` и тестах.
- `Place` — `data class`, поле `category` связывает место с категорией.
- Все тексты лежат в `strings.xml`, в модели только ссылки `@StringRes`.

### 3.3. Данные — `LocalPlacesDataProvider`

```kotlin
object LocalPlacesDataProvider {

    val allPlaces: List<Place> = listOf(
        // Sights
        Place(
            id = 1,
            category = Category.Sights,
            name = R.string.kremlin_name,
            shortDescription = R.string.kremlin_short,
            address = R.string.kremlin_address,
            description = R.string.kremlin_description,
            icon = Icons.Filled.Fort,
        ),
        // ... ещё 23 места
    )

    fun getPlaces(category: Category): List<Place> =
        allPlaces.filter { it.category == category }
}
```

- `object` — синглтон, один экземпляр на приложение.
- `allPlaces` — все места, `id` уникальный (по нему потом строится `key` в списке).
- `getPlaces(category)` — **метод**, который отдаёт места нужной категории через `filter`.
- Источник данных локальный (в памяти). Сеть и база данных появятся в следующих юнитах, поэтому код получения данных вынесен в отдельный объект: его можно заменить, не трогая UI.

### 3.4. Состояние — `CityScreen`, `CityUiState`

```kotlin
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
```

- `CityScreen` — **маршруты** навигации. Имя enum (`CityScreen.Places.name`) используется как строка маршрута.
- `CityUiState` — **UI State**: единый объект, который целиком описывает, что сейчас должно быть на экране.
  - `currentCategory`, `places`, `currentPlace` — что выбрано;
  - `currentPlace: Place?` — тип `Place?` допускает `null` (место ещё не выбрано);
  - `isShowingPlaces`, `isShowingDetails` — на каком экране находится пользователь. Нужны, чтобы **восстановить позицию**, если раскладка сменилась (например, поворот телефона из Compact в Medium).
- Значения по умолчанию задают начальное состояние.

### 3.5. `CityViewModel`

```kotlin
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
```

**Термины и приёмы**
- `ViewModel` — класс Jetpack, который хранит состояние UI и **переживает пересоздание Activity** (поворот экрана). Composable сами состояние не хранят, а читают его отсюда.
- `MutableStateFlow` — изменяемый поток, который всегда хранит последнее значение. Приватный `_uiState` менять может только `ViewModel`.
- `StateFlow` + `asStateFlow()` — **публичный только для чтения** вариант. Так UI не может изменить состояние напрямую. Приём называется **backing property**: приватная изменяемая `_uiState` и публичная неизменяемая `uiState`.
- `_uiState.update { ... }` — **атомарно** заменяет значение: лямбда получает старое состояние и возвращает новое. Безопасно при вызове из нескольких потоков.
- `copy(...)` — метод `data class`: создаёт копию, изменяя только указанные поля. Само состояние неизменяемо (**immutable**), каждый раз создаётся новый объект.
- Конструктор принимает `dataProvider` со значением по умолчанию, поэтому в тестах можно подставить свой провайдер (**внедрение зависимости**).

**Методы**
- `selectCategory(category)` — пользователь выбрал категорию. Состояние пересобирается через `stateForCategory`, первое место автоматически становится выбранным (иначе на большом экране панель подробностей была бы пустой), `isShowingPlaces = true`.
- `selectPlace(place)` — пользователь выбрал место. Обновляются категория, список и текущее место, оба флага становятся `true`.
- `onScreenShown(screen)` — вызывается из UI каждый раз, когда экран реально сменился. Благодаря этому флаги правильно обновляются и при системной кнопке «назад», а не только при клике по нашим кнопкам.
- `stateForCategory(category)` — приватный вспомогательный метод, собирает `CityUiState` для категории; `firstOrNull()` безопасно возвращает `null`, если список пуст.

### 3.6. Выбор раскладки — `MyCityApp`

```kotlin
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
```

- `viewModel()` — функция из `lifecycle-viewmodel-compose`: создаёт или возвращает уже существующую `CityViewModel`, привязанную к Activity.
- `collectAsState()` — превращает `StateFlow` в состояние Compose. Когда `uiState` в `ViewModel` меняется, UI перерисовывается.
- `when` — выбор раскладки по `WindowWidthSizeClass`.
- `viewModel::selectCategory` — **ссылка на метод**. Вместо лямбды `{ viewModel.selectCategory(it) }` передаётся сам метод.
- Состояние поднято (**state hoisting**): `CityListAndDetailApp` не знает про `ViewModel`, он получает `uiState` и функции-обработчики. Такие composable проще тестировать и показывать в превью.

### 3.7. Компактная и средняя раскладки — `CityNavigationApp`

Тут основная работа с навигацией.

```kotlin
val startScreen = if (showCategoriesInRail) CityScreen.Places else CityScreen.Categories
val backStackEntry by navController.currentBackStackEntryAsState()
val currentScreen = backStackEntry?.destination?.route?.let { CityScreen.valueOf(it) }
    ?: startScreen
```

- `rememberNavController()` — создаёт `NavHostController` и запоминает его.
- `startScreen` — на Medium категории вынесены в `NavigationRail`, поэтому стартовый экран сразу «Места».
- `currentBackStackEntryAsState()` — состояние текущей записи в **back stack** (стеке экранов). Из неё достаём `route` и превращаем в `CityScreen` через `valueOf`. Оператор `?:` (элвис) подставляет запасное значение, если записи ещё нет.

Сам `NavHost`:

```kotlin
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
```

- `NavHost` — контейнер, который показывает экран по текущему маршруту.
- `composable(route) { ... }` — регистрирует экран для маршрута.
- `navController.navigate(route)` — переход вперёд: экран кладётся в back stack.
- `navController.navigateUp()` / `popBackStack(...)` — переход назад.
- Порядок в обработчике важен: сначала `viewModel.selectCategory(...)` обновляет данные, потом `navigate` открывает экран.
- `uiState.currentPlace?.let { ... }` — **безопасный вызов**: блок выполнится, только если место не `null`.

Верхняя панель и кнопка «назад»:

```kotlin
CityTopAppBar(
    title = title,
    canNavigateBack = navController.previousBackStackEntry != null,
    onNavigateUp = { navController.navigateUp() },
)
```

`previousBackStackEntry != null` означает, что есть куда вернуться, значит стрелку «назад» показываем.

В medium-раскладке слева стоит `NavigationRail`:

```kotlin
CategoriesNavigationRail(
    categories = uiState.categories,
    currentCategory = uiState.currentCategory,
    onCategoryClick = { category ->
        viewModel.selectCategory(category)
        navController.popBackStack(CityScreen.Places.name, inclusive = false)
    },
)
```

`popBackStack(route, inclusive = false)` убирает из стека всё выше «Мест». Без этого, если пользователь был на подробностях и выбрал другую категорию в rail, он остался бы на старых подробностях.

**Сохранение позиции при смене раскладки**

```kotlin
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
```

- `LaunchedEffect(key)` — запускает **побочный эффект** (корутину) при первом появлении composable и заново при смене ключа. Внутри можно вызывать `navigate`, чего нельзя делать прямо в теле composable.
- Первый `LaunchedEffect(Unit)` выполняется один раз и открывает тот экран, на котором пользователь был в предыдущей раскладке (например, на Expanded он смотрел подробности, потом окно сузилось).
- `positionRestored` в `rememberSaveable` защищает от повторного выполнения после пересоздания.
- Второй `LaunchedEffect(currentScreen)` каждый раз, когда сменяется экран, сообщает об этом `ViewModel`. Благодаря этому флаги `isShowingPlaces` и `isShowingDetails` всегда соответствуют реальности.

### 3.8. Расширенная раскладка — `CityListAndDetailApp`

```kotlin
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
    Scaffold(topBar = { ... }) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize()) {
            PlacesList(
                places = uiState.places,
                onPlaceClick = onPlaceClick,
                selectedPlace = uiState.currentPlace,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            uiState.currentPlace?.let { place ->
                PlaceDetails(
                    place = place,
                    modifier = Modifier.weight(1.3f).fillMaxHeight()
                        .testTag(stringResource(R.string.details_pane)),
                )
            }
        }
    }
}
```

- `PermanentNavigationDrawer` — постоянная боковая панель, она всегда видна и не выезжает. Подходит для экранов от 840 dp.
- Здесь **нет `NavHost`**: все три уровня на экране одновременно, поэтому навигация не нужна.
- Это паттерн **list-detail** (список и детали рядом): `Row` делит ширину в пропорции `1f` и `1.3f` через `Modifier.weight`.
- `selectedPlace` подсвечивает выбранную карточку в списке.
- `Modifier.testTag(...)` — метка, по которой UI-тест находит элемент.

### 3.9. Экраны — `CityScreens.kt`

**Верхняя панель**

```kotlin
CenterAlignedTopAppBar(
    title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    navigationIcon = {
        if (canNavigateBack) {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
        }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ),
)
```

- `Icons.AutoMirrored.Filled.ArrowBack` — стрелка, которая сама разворачивается в языках с письмом справа налево.
- `TextOverflow.Ellipsis` — длинный заголовок обрезается многоточием.
- `contentDescription` у стрелки используется TalkBack и UI-тестом.

**Иллюстрация вместо фото**

```kotlin
@Composable
fun CityIllustration(
    category: Category,
    icon: ImageVector,
    iconSize: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(category.gradient()),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize),
        )
    }
}
```

Градиент задан в `Theme.kt` как **функция-расширение** (extension function) для `Category`:

```kotlin
fun Category.gradient(): Brush = Brush.linearGradient(
    when (this) {
        Category.Sights -> listOf(Color(0xFF0E7C66), Color(0xFF2BB38A))
        Category.Museums -> listOf(Color(0xFF3949AB), Color(0xFF7986CB))
        Category.Parks -> listOf(Color(0xFF2E7D32), Color(0xFF8BC34A))
        Category.Food -> listOf(Color(0xFFD84315), Color(0xFFFFA000))
        Category.Theaters -> listOf(Color(0xFFAD1457), Color(0xFFEF5350))
    }
)
```

Благодаря расширению можно писать `category.gradient()`, хотя сам `enum Category` ничего про UI не знает. У каждой категории свой цвет, поэтому категории и их места визуально связаны.

**Экран категорий**

```kotlin
LazyColumn(
    modifier = modifier,
    contentPadding = contentPadding,
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    item { Text(text = stringResource(R.string.app_subtitle), ...) }
    items(categories, key = { it.name }) { category ->
        CategoryCard(
            category = category,
            placesCount = LocalPlacesDataProvider.getPlaces(category).size,
            onClick = { onCategoryClick(category) },
            modifier = Modifier.widthIn(max = ContentMaxWidth).padding(horizontal = 16.dp),
        )
    }
}
```

- `item { }` — один элемент (подзаголовок), `items(list) { }` — по элементу на запись.
- `Modifier.widthIn(max = 720.dp)` — ограничивает ширину карточек, чтобы на планшете они не растягивались на весь экран.
- В карточке показывается число мест через **plurals** (строки с формами множественного числа):

```kotlin
Text(text = pluralStringResource(R.plurals.places_count, placesCount, placesCount), ...)
```

В `strings.xml`:

```xml
<plurals name="places_count">
    <item quantity="one">%d место</item>
    <item quantity="few">%d места</item>
    <item quantity="many">%d мест</item>
    <item quantity="other">%d места</item>
</plurals>
```

Для русского языка у слова три формы («1 место», «2 места», «5 мест»), `pluralStringResource` сам выбирает правильную по числу.

**Список мест и карточка**

```kotlin
Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
        containerColor = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CityIllustration(
            category = place.category,
            icon = place.icon,
            iconSize = 40.dp,
            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
            modifier = Modifier.size(96.dp),
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(text = stringResource(place.name), style = MaterialTheme.typography.titleMedium,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(text = stringResource(place.shortDescription), style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
```

- Цвет карточки зависит от параметра `selected`: на широком экране выбранное место подсвечивается.
- Скругляются только левые углы иллюстрации (`topStart`, `bottomStart`), чтобы она аккуратно прилегала к краю карточки.
- Раскладка «картинка слева, текст справа» такая же, как в Superheroes из Unit 3.

**Подробности места**

```kotlin
Column(
    modifier = modifier
        .verticalScroll(rememberScrollState())
        .padding(contentPadding),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Column(modifier = Modifier.widthIn(max = ContentMaxWidth)) {
        CityIllustration(
            category = place.category,
            icon = place.icon,
            iconSize = 112.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(min(220.dp, LocalConfiguration.current.screenHeightDp.dp * 0.4f))
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
        )
        // метка категории, название, адрес с иконкой LocationOn, HorizontalDivider, описание
    }
}
```

- `verticalScroll(rememberScrollState())` — обычная прокрутка (не ленивая), так как содержимое небольшое.
- `LocalConfiguration.current.screenHeightDp` — высота экрана. Баннер ограничен `min(220.dp, 40% высоты)`, чтобы в альбомной ориентации на телефоне осталось место под текст.
- `HorizontalDivider` — тонкая разделительная линия.

### 3.10. Тема

- `Color.kt` — палитра Material 3. Основа — зелёный `#1E6A4F` (цвет флага Татарстана), красный `tertiary` (`#9C4142`) как акцент. Есть наборы `...Light` и `...Dark`.
- `Theme.kt`:

```kotlin
@Composable
fun MyCityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
```

Тема переключается по системной настройке. Градиенты категорий одинаковые в обеих темах, поверх них всегда рисуется белая иконка.

---

## 4. Тесты

### 4.1. `CityViewModelTest` — локальные (JVM), эмулятор не нужен

```kotlin
class CityViewModelTest {
    private val viewModel = CityViewModel()

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
}
```

Что проверяют пять тестов:

| Тест | Проверка |
|---|---|
| `dataProvider_everyCategory_hasAtLeastThreePlaces` | В каждой категории не меньше трёх мест (требование задания) |
| `cityViewModel_Initialization_...` | Начальное состояние: выбрана первая категория и первое место, флаги `false` |
| `cityViewModel_CategorySelected_...` | После `selectCategory` показаны места только этой категории, первое выбрано |
| `cityViewModel_PlaceSelected_...` | После `selectPlace` выбраны это место и его категория, показаны подробности |
| `cityViewModel_BackToCategories_...` | `onScreenShown(Categories)` сбрасывает флаги, но выбранное место остаётся |

Термины:
- `@Test` — аннотация JUnit, помечает тестовый метод.
- `assertEquals`, `assertTrue`, `assertFalse` — проверки: если условие не выполнено, тест падает.
- `viewModel.uiState.value` — текущее значение `StateFlow`.
- Имя теста по схеме `объект_действие_ожидание` — принятая в курсе запись.
- Тест выполняется на JVM без Android, потому что `ViewModel` не зависит от интерфейса.

### 4.2. `MyCityAppTest` — UI-тесты, нужен эмулятор или устройство

```kotlin
@get:Rule
val composeTestRule = createAndroidComposeRule<ComponentActivity>()

@Test
fun compactDevice_categoryAndPlaceClicked_detailsShownAndBackReturnsToList() {
    setApp(WindowWidthSizeClass.Compact)
    val place = LocalPlacesDataProvider.getPlaces(Category.Museums).first()

    onNodeWithStringId(Category.Museums.title).performClick()
    onNodeWithStringId(place.shortDescription).assertIsDisplayed()

    onNodeWithStringId(place.name).performClick()
    onNodeWithStringId(place.address).assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription(string(R.string.back_button)).performClick()
    onNodeWithStringId(place.shortDescription).assertIsDisplayed()
}
```

| Тест | Проверка |
|---|---|
| `compactDevice_...` | Путь «категория → места → подробности → назад»; на первом экране нет стрелки «назад» |
| `mediumDevice_usesNavigationRail` | В Medium есть `NavigationRail` (по `testTag`) |
| `mediumDevice_railCategoryClicked_...` | Клик по категории в rail показывает её места |
| `expandedDevice_usesNavigationDrawerAndShowsListAndDetails` | В Expanded есть drawer, список и панель подробностей видны одновременно |

Термины:
- `createAndroidComposeRule<ComponentActivity>()` — правило, которое запускает Compose-интерфейс для теста.
- `setContent` — тест сам подставляет нужный `WindowWidthSizeClass`, поэтому физический размер экрана не важен (кроме Expanded, см. комментарий в файле).
- `onNodeWithText`, `onNodeWithTag`, `onNodeWithContentDescription` — поиск элементов в **семантическом дереве**.
- `performClick()` — нажатие, `assertIsDisplayed()` и `assertExists()` — проверки.
- `testTag` в коде приложения нужен только тестам, для пользователя он не виден.

Запуск: `./gradlew test` (локальные), `./gradlew connectedAndroidTest` (UI).

---

## 5. Словарь терминов

| Термин | Что значит |
|---|---|
| **ViewModel** | Хранит состояние UI и переживает пересоздание Activity |
| **UiState** | Единый `data class` с полным описанием экрана |
| **StateFlow / MutableStateFlow** | Поток, который хранит последнее значение; UI подписывается на него |
| **Backing property** | Приватный `_uiState` (изменяемый) и публичный `uiState` (только чтение) |
| **UDF** | Однонаправленный поток данных: состояние вниз, события вверх |
| **State hoisting** | Вынос состояния из composable наверх, composable получает данные и обработчики |
| **`collectAsState()`** | Превращает `StateFlow` в состояние Compose |
| **WindowSizeClass** | Категория размера окна: `Compact`, `Medium`, `Expanded` |
| **NavHost / NavController** | Контейнер экранов и объект управления переходами |
| **Route** | Строка-идентификатор экрана в навигации |
| **Back stack** | Стек открытых экранов; «назад» снимает верхний |
| **`popBackStack`** | Убирает экраны из стека до указанного маршрута |
| **NavigationRail** | Узкая боковая панель навигации для средних экранов |
| **PermanentNavigationDrawer** | Постоянная боковая панель для больших экранов |
| **List-detail** | Список и подробности рядом на одном экране |
| **`LaunchedEffect`** | Запускает побочный эффект (корутину) из composable |
| **Корутина** | Лёгкая асинхронная задача Kotlin |
| **Extension function** | Функция-расширение: `Category.gradient()` добавляет метод к существующему классу |
| **`enum class` / `entries`** | Перечисление и список его значений |
| **`data class` / `copy`** | Класс для данных и метод копирования с заменой полей |
| **Nullable (`Place?`) / `?.let` / `?:`** | Тип, допускающий `null`; безопасный вызов; значение по умолчанию |
| **Plurals** | Строки с формами множественного числа |
| **`testTag`** | Метка элемента для UI-теста |
| **Immutable** | Неизменяемый: состояние не правится, а создаётся заново через `copy` |

---

## 6. Что из курса здесь используется

| Тема | Где в проекте |
|---|---|
| Списки (Unit 3) | `PlacesList`, `CategoriesScreen`, `LazyColumn`, `items(key = ...)` |
| Material 3 и тема (Unit 3) | `ui/theme`, `Card`, `Scaffold`, `TopAppBar` |
| Модель и слой данных (Unit 3) | `Place`, `Category`, `LocalPlacesDataProvider` |
| `ViewModel`, `UiState`, `StateFlow` | `CityViewModel`, `CityUiState` |
| Навигация | `NavHost`, `NavController`, `CityScreen` |
| Адаптивность | `calculateWindowSizeClass`, `MyCityApp`, `NavigationRail`, `PermanentNavigationDrawer` |
| List-detail | `CityListAndDetailApp` |
| Тесты | `CityViewModelTest`, `MyCityAppTest` |

## 7. Как рассказать про проект

1. **Идея:** гид по Казани, 5 категорий, 24 места.
2. **Архитектура:** `data` → `ViewModel` → UI. Состояние описано одним `CityUiState`, течёт вниз; события идут вверх через методы `selectCategory`, `selectPlace`, `onScreenShown`.
3. **Главное, что показать:** одно приложение в трёх раскладках. На эмуляторе Phone приложение идёт экран за экраном, на Foldable слева появляется rail, на Tablet рядом список и подробности.
4. **Что интересного в коде:**
   - позиция пользователя сохраняется при смене раскладки (флаги в `UiState` и `LaunchedEffect`);
   - состояние хранится во `ViewModel`, поэтому поворот экрана ничего не сбрасывает;
   - иконки на градиентах вместо фото: нет файлов-картинок, цвета привязаны к категориям;
   - `plurals` для правильного склонения «1 место / 2 места / 5 мест».
5. **Отличие от Cupcake и Reply:** от Cupcake тем, что здесь адаптивность, а не пошаговый заказ; от Reply тем, что три уровня навигации (категории → места → детали), а не папки почты.
6. **Чего нет сознательно:** сети и базы данных. Данные лежат в коде и отделены в `LocalPlacesDataProvider`, который можно заменить.

## 8. Как запустить

Открыть папку `Unit4/My-city` в Android Studio, дождаться синхронизации Gradle, запустить `app`. Для проверки адаптивности использовать эмуляторы Phone, Foldable и Tablet или режим Resizable.
