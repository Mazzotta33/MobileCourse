# 30 дней к здоровью (Thirty-days) — самостоятельная работа Unit 3

Итоговое практическое приложение третьего юнита. Задание курса: сделать свой список из 30 карточек («30 дней чего-нибудь») с собственной темой, без пошагового руководства. Здесь выбрана тема здоровья: на каждый день по одному небольшому совету.

В файле три части: **что делает приложение**, **как оно устроено (с кодом)** и **словарь терминов**. Ко всему, что есть в коде, даны пояснения.

---

## 1. Что делает приложение

- Показывает прокручиваемый список из 30 карточек.
- В карточке: метка «День N», заголовок совета, цветная иллюстрация и стрелка.
- Нажатие на карточку раскрывает подробное описание, повторное нажатие скрывает.
- Сверху закреплена панель с названием и иконкой; когда под неё прокручивается список, она меняет цвет.
- Есть светлая и тёмная тема, шрифты PT Sans и Montserrat Alternates, мягкие скругления.

Технические параметры: `minSdk = 24`, `compileSdk = 34`, Jetpack Compose, Material 3, `material-icons-extended`. Сети, базы данных, `ViewModel` и навигации нет, это тема следующего юнита.

## 2. Структура проекта

```
app/src/main/
├── java/com/example/thirtydays/
│   ├── MainActivity.kt          точка входа, Scaffold, верхняя панель
│   ├── TipsScreen.kt            TipsList, TipCard, DayBadge, TipIllustration
│   ├── model/Tip.kt             модель одного совета
│   ├── data/TipsRepository.kt   список из 30 советов
│   └── ui/theme/
│       ├── Color.kt             палитры светлой и тёмной темы
│       ├── Type.kt              шрифты и текстовые стили
│       ├── Shape.kt             скругления
│       └── Theme.kt             ThirtyDaysTheme
└── res/
    ├── font/                    montserrat_alternates_bold.ttf, pt_sans_regular.ttf, pt_sans_bold.ttf
    └── values/strings.xml       тексты 30 советов и служебные строки
```

Поток данных: **`TipsRepository` → `TipsList` → `TipCard`**. Данные идут сверху вниз и нигде не хранятся в UI. Это упрощённая версия схемы из Affirmations.

---

## 3. Разбор кода

### 3.1. Точка входа — `MainActivity`

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ThirtyDaysTheme {
                ThirtyDaysApp()
            }
        }
    }
}
```

- `ComponentActivity` — базовый класс Activity для Compose.
- `onCreate` — метод жизненного цикла: вызывается один раз при создании экрана.
- `enableEdgeToEdge()` — рисует приложение под системными панелями (статус-бар и панель навигации), а отступы потом учитывает `Scaffold`.
- `setContent { ... }` — метод, который подключает Compose-интерфейс к Activity.
- `ThirtyDaysTheme { ... }` — оборачивает всё приложение в собственную тему, чтобы любой вложенный composable мог читать `MaterialTheme.colorScheme`, `typography` и `shapes`.

### 3.2. Каркас экрана — `ThirtyDaysApp`

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirtyDaysApp() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { ThirtyDaysTopAppBar(scrollBehavior = scrollBehavior) }
    ) { innerPadding ->
        TipsList(
            tips = TipsRepository.tips,
            contentPadding = innerPadding
        )
    }
}
```

- `@Composable` — аннотация: функция описывает кусок интерфейса и может вызывать другие composable.
- `@OptIn(ExperimentalMaterial3Api::class)` — явное согласие использовать API, который Google ещё помечает как экспериментальный.
- `Scaffold` — готовый каркас Material: верхняя панель, контент, плавающая кнопка и т. д. Передаёт в лямбду `innerPadding` — отступы, которые нужно применить к содержимому, чтобы оно не уезжало под панель.
- `TopAppBarDefaults.pinnedScrollBehavior()` — поведение верхней панели: она остаётся на месте, но меняет цвет, когда под ней прокручивается контент.
- `Modifier.nestedScroll(...)` — подключает вложенную прокрутку: список сообщает панели, насколько прокрутился.
- `TipsRepository.tips` — список из 30 советов; `contentPadding = innerPadding` передаёт отступы в список.

### 3.3. Верхняя панель — `ThirtyDaysTopAppBar`

```kotlin
CenterAlignedTopAppBar(
    title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Spa,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall
            )
        }
    },
    scrollBehavior = scrollBehavior,
    modifier = modifier
)
```

- `CenterAlignedTopAppBar` — панель с заголовком по центру.
- `Row` — раскладка в строку; `verticalAlignment` выравнивает элементы по вертикали.
- `Icon(imageVector = Icons.Filled.Spa, ...)` — векторная иконка из набора Material Icons. `contentDescription = null` означает, что иконка декоративная и экранный диктор (TalkBack) её пропустит.
- `tint = MaterialTheme.colorScheme.primary` — иконка красится в основной цвет темы.
- `Spacer` — пустой элемент для отступа.
- `stringResource(R.string.app_name)` — читает строку «30 дней к здоровью» из `strings.xml`.
- `MaterialTheme.typography.displaySmall` — текстовый стиль, который в `Type.kt` переопределён под Montserrat Alternates.

### 3.4. Модель — `Tip`

```kotlin
data class Tip(
    val day: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val icon: ImageVector
)
```

- `data class` — класс, предназначенный для хранения данных. Kotlin сам создаёт `equals`, `hashCode`, `toString` и `copy`.
- `@StringRes` — аннотация-проверка: в это поле можно положить только идентификатор строкового ресурса, а не любое число. Компилятор поймает ошибку.
- Модель хранит не текст, а ссылки на ресурсы (`R.string.tip1_title`). Это удобно для локализации: тексты лежат отдельно и могут быть переведены.
- `ImageVector` — векторная иконка, рисуется из кода, файлов-картинок не нужно.

### 3.5. Данные — `TipsRepository`

```kotlin
object TipsRepository {
    val tips = listOf(
        Tip(1, R.string.tip1_title, R.string.tip1_desc, Icons.Filled.WaterDrop),
        Tip(2, R.string.tip2_title, R.string.tip2_desc, Icons.Filled.DirectionsWalk),
        Tip(3, R.string.tip3_title, R.string.tip3_desc, Icons.Filled.Bedtime),
        // ... до 30
        Tip(30, R.string.tip30_title, R.string.tip30_desc, Icons.Filled.Celebration),
    )
}
```

- `object` — синглтон: в приложении существует ровно один такой экземпляр, создавать его не нужно.
- `listOf(...)` — неизменяемый список.
- В `strings.xml` лежат пары строк, например `tip1_title` («Стакан воды утром») и `tip1_desc` (подробное описание).
- Если нужно добавить 31-й совет, достаточно добавить две строки и одну запись в список. UI менять не надо.

### 3.6. Список — `TipsList`

```kotlin
@Composable
fun TipsList(
    tips: List<Tip>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tips, key = { it.day }) { tip ->
            TipCard(
                tip = tip,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
```

- `LazyColumn` — вертикальный прокручиваемый список с **ленивой композицией**: на экране создаются только видимые карточки, а не все 30 сразу. Для больших списков это критично.
- `items(tips, key = { it.day })` — для каждого элемента списка строит карточку. `key` — уникальный ключ элемента: Compose использует его, чтобы не путать карточки при прокрутке и сохранить состояние (в нашем случае раскрытое/свёрнутое) у нужной карточки.
- `Arrangement.spacedBy(12.dp)` — одинаковые промежутки между карточками.
- `contentPadding` — отступы внутри списка. В отличие от обычного `padding`, элементы прокручиваются в этих отступах, а не обрезаются.
- Параметр `modifier: Modifier = Modifier` — стандартный приём Compose: вызывающий код может изменить внешний вид composable, не переписывая его.

### 3.7. Карточка — `TipCard`

Главная функция проекта, в ней собраны все новые приёмы юнита.

```kotlin
@Composable
fun TipCard(tip: Tip, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrowRotation"
    )
    val expandDescription = stringResource(R.string.expand_content_description)

    Card(
        onClick = { expanded = !expanded },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.semantics { contentDescription = expandDescription }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DayBadge(day = tip.day)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(tip.titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
            Spacer(Modifier.height(12.dp))
            TipIllustration(tip = tip)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = stringResource(tip.descriptionRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}
```

Разбор по частям.

**Состояние**
- `var expanded by rememberSaveable { mutableStateOf(false) }` — это **состояние (state)** карточки: «раскрыта или нет».
  - `mutableStateOf(false)` создаёт наблюдаемое значение; при его изменении Compose автоматически перерисовывает (делает **рекомпозицию**) тех, кто его читает.
  - `remember` запомнил бы значение между рекомпозициями. `rememberSaveable` идёт дальше: значение переживает **пересоздание Activity** (поворот экрана), потому что сохраняется в `Bundle`.
  - `by` — делегат свойства: позволяет писать `expanded` вместо `expanded.value`.

**Анимации**
- `animateFloatAsState(targetValue = ...)` — плавно меняет число от текущего значения к целевому (0° → 180°). Мы просто меняем `expanded`, а анимацию Compose делает сам.
- `Modifier.rotate(arrowRotation)` — поворачивает стрелку на текущий угол.
- `animateContentSize(spring(...))` — модификатор, который анимирует изменение размера контента. `spring` — пружинная анимация без фиксированной длительности; `dampingRatio` задаёт упругость (насколько «пружинит»), `stiffness` — жёсткость (скорость).
- `AnimatedVisibility(visible, enter, exit)` — показывает или скрывает блок с анимацией. `fadeIn() + expandVertically()` — появление через прозрачность и раскрытие по вертикали; `fadeOut() + shrinkVertically()` — обратный эффект.

**Компонент**
- `Card(onClick = ...)` — карточка Material 3, которую можно нажать.
- `CardDefaults.cardColors(containerColor = ...)` и `cardElevation(...)` — цвет и тень.
- `MaterialTheme.colorScheme.surfaceContainerLow` — цвет из текущей темы, поэтому в тёмной теме карточка станет тёмной автоматически.
- `MaterialTheme.shapes.large` — скругление 24 dp из `Shape.kt`.

**Доступность**
- `Modifier.semantics { contentDescription = ... }` — описание для TalkBack: «Показать или скрыть подробности совета».

**Раскладка**
- `Column` складывает метку, заголовок, иллюстрацию и описание вертикально.
- `Modifier.weight(1f)` у заголовка — он занимает всё свободное место в строке, поэтому стрелка прижата к правому краю.

### 3.8. Метка дня — `DayBadge`

```kotlin
@Composable
fun DayBadge(day: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = stringResource(R.string.day_label, day),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
```

- `Box` — контейнер, который накладывает элементы друг на друга.
- Порядок модификаторов важен: сначала `clip` обрезает форму, потом `background` красит, потом `padding` делает внутренний отступ. Если поставить `padding` перед `background`, цветом закрасится только внутренность.
- `stringResource(R.string.day_label, day)` — строка с подстановкой: в `strings.xml` лежит `День %1$d`, вместо `%1$d` подставляется число.
- `onPrimary` — цвет текста, который гарантированно читается на `primary`. Это принцип Material: у каждого фона есть парный цвет для текста.

### 3.9. Иллюстрация — `TipIllustration`

```kotlin
val scheme = MaterialTheme.colorScheme
val (start, end, foreground) = when (tip.day % 3) {
    1 -> Triple(scheme.primaryContainer, scheme.tertiaryContainer, scheme.onPrimaryContainer)
    2 -> Triple(scheme.secondaryContainer, scheme.primaryContainer, scheme.onSecondaryContainer)
    else -> Triple(scheme.tertiaryContainer, scheme.secondaryContainer, scheme.onTertiaryContainer)
}
Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
        .fillMaxWidth()
        .height(160.dp)
        .clip(MaterialTheme.shapes.medium)
        .background(Brush.linearGradient(listOf(start, end)))
) {
    DecorCircle(foreground.copy(alpha = 0.08f), 140.dp, Modifier.offset(x = (-110).dp, y = 40.dp))
    DecorCircle(foreground.copy(alpha = 0.06f), 90.dp, Modifier.offset(x = 120.dp, y = (-45).dp))
    DecorCircle(foreground.copy(alpha = 0.10f), 120.dp)
    Icon(
        imageVector = tip.icon,
        contentDescription = null,
        tint = foreground,
        modifier = Modifier.size(72.dp)
    )
}
```

- Вместо файлов-картинок картинка **рисуется кодом**: градиентный фон, три полупрозрачных круга и иконка по центру.
- `tip.day % 3` — остаток от деления: цветовая пара чередуется между тремя вариантами, поэтому соседние карточки выглядят по-разному.
- `Triple` и **деструктуризация** `val (start, end, foreground) = ...` — тройка значений, которую сразу раскладываем по трём переменным.
- `Brush.linearGradient(listOf(start, end))` — линейный градиент между двумя цветами.
- `foreground.copy(alpha = 0.08f)` — тот же цвет, но почти прозрачный.
- `Modifier.offset(x, y)` — сдвиг элемента, так круги расставлены по углам.
- Все цвета берутся из темы, поэтому в тёмной теме иллюстрации перекрасятся сами.

`DecorCircle` — приватный composable для круга:

```kotlin
@Composable
private fun DecorCircle(color: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}
```

`CircleShape` — готовая круглая форма.

### 3.10. Тема — `ui/theme`

**Цвета (`Color.kt`).** Палитра Material 3 в духе схемы Tonal Spot: основной индиго `#4355B9`, дополнительный сиреневый, акцентный розово-лиловый. Для каждой роли есть пара:

```kotlin
val primaryLight = Color(0xFF4355B9)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFDEE0FF)
val onPrimaryContainerLight = Color(0xFF00105C)
```

`primary` — основной цвет, `onPrimary` — цвет текста поверх него, `primaryContainer` — смягчённая версия для фонов, `onPrimaryContainer` — текст поверх неё. То же для `secondary`, `tertiary`, `error`, `surface` и других ролей. Для тёмной темы есть второй набор (`...Dark`).

**Шрифты (`Type.kt`).**

```kotlin
val MontserratAlternates = FontFamily(
    Font(R.font.montserrat_alternates_bold, FontWeight.Bold)
)

val PtSans = FontFamily(
    Font(R.font.pt_sans_regular, FontWeight.Normal),
    Font(R.font.pt_sans_bold, FontWeight.Bold)
)

val Typography = Typography(
    displaySmall = TextStyle(fontFamily = MontserratAlternates, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge   = TextStyle(fontFamily = PtSans, fontWeight = FontWeight.Bold,   fontSize = 20.sp, lineHeight = 26.sp),
    labelLarge   = TextStyle(fontFamily = PtSans, fontWeight = FontWeight.Bold,   fontSize = 14.sp, letterSpacing = 0.8.sp),
    bodyLarge    = TextStyle(fontFamily = PtSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp)
)
```

- `FontFamily` — семейство шрифта, собранное из файлов в `res/font`.
- `TextStyle` — набор параметров текста: гарнитура, насыщенность, размер (`sp`), межстрочный интервал (`lineHeight`), межбуквенный (`letterSpacing`).
- Оба шрифта поддерживают кириллицу. Montserrat Alternates используется только для названия приложения (фирменный стиль), PT Sans — для остального текста.
- Остальные стили (`headlineMedium`, `bodySmall` и т. д.) остаются стандартными.

**Формы (`Shape.kt`).**

```kotlin
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),    // метка дня
    medium = RoundedCornerShape(16.dp),  // иллюстрация
    large = RoundedCornerShape(24.dp)    // карточка
)
```

**Сама тема (`Theme.kt`).**

```kotlin
@Composable
fun ThirtyDaysTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

- `isSystemInDarkTheme()` — читает системную настройку тёмной темы.
- `dynamicColor` — **динамические цвета** Android 12+ (`Build.VERSION_CODES.S`): палитра берётся из обоев. Здесь выключено, чтобы сохранить фирменные цвета.
- `lightColorScheme` / `darkColorScheme` — собирают `ColorScheme` из ролей (`LightColors`, `DarkColors` определены в этом же файле).
- `MaterialTheme(colorScheme, typography, shapes, content)` — применяет всё к дереву composable. С этого момента любой дочерний элемент может использовать `MaterialTheme.colorScheme.primary` и т. д.

### 3.11. Превью

```kotlin
@Preview("Светлая тема")
@Preview("Тёмная тема", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TipCardPreview() {
    ThirtyDaysTheme {
        TipCard(tip = TipsRepository.tips.first())
    }
}
```

`@Preview` показывает composable прямо в Android Studio без запуска на устройстве. Несколько аннотаций дают несколько превью (здесь светлая и тёмная тема), `uiMode = UI_MODE_NIGHT_YES` включает ночной режим.

---

## 4. Словарь терминов

| Термин | Что значит |
|---|---|
| **Composable** | Функция с `@Composable`, описывающая часть интерфейса |
| **Рекомпозиция** | Повторный вызов composable при изменении состояния |
| **State (состояние)** | Данные, при изменении которых UI перерисовывается (`mutableStateOf`) |
| **`remember`** | Запоминает значение между рекомпозициями, но не между пересозданиями Activity |
| **`rememberSaveable`** | То же, но переживает поворот экрана (сохраняется в `Bundle`) |
| **Modifier** | Цепочка настроек элемента: размер, отступы, фон, клик. Порядок важен |
| **Ленивая композиция** | `LazyColumn` создаёт только видимые элементы |
| **`@StringRes`** | Проверка типа: параметр должен быть ID строкового ресурса |
| **Material 3 (M3)** | Библиотека дизайн-системы Google: компоненты, цвета, типографика |
| **ColorScheme** | Набор цветовых ролей (`primary`, `onPrimary`, `surface` и др.) |
| **Container / on-цвета** | Фон и парный цвет для текста поверх него |
| **Dynamic color** | Цвета из обоев, Android 12+ |
| **`dp` / `sp`** | Единицы: `dp` для размеров, `sp` для шрифтов (зависит от настроек пользователя) |
| **Spring-анимация** | Пружинная анимация, параметры `dampingRatio` и `stiffness` |
| **TalkBack / `contentDescription`** | Экранный диктор и текст, который он озвучивает |
| **Edge-to-edge** | Интерфейс рисуется под системными панелями |
| **Scaffold** | Каркас экрана с верхней панелью и контентом |
| **`innerPadding`** | Отступы, которые `Scaffold` отдаёт контенту |

---

## 5. Что из курса здесь используется

| Тема Unit 3 | Где в проекте |
|---|---|
| Списки (`LazyColumn`) | `TipsList` |
| Модель данных и `@StringRes` | `Tip`, `strings.xml` |
| Слой данных | `TipsRepository` |
| `Card`, `Scaffold`, `TopAppBar` | `TipCard`, `ThirtyDaysApp` |
| Собственная тема | `Color`, `Type`, `Shape`, `Theme` |
| Светлая и тёмная схемы | `Theme.kt`, `@Preview` с `UI_MODE_NIGHT_YES` |
| Анимации | `animateContentSize`, `AnimatedVisibility`, `animateFloatAsState` |
| Иконки Material | `Tip.icon`, верхняя панель |
| Превью | `ThirtyDaysPreview`, `TipCardPreview` |

## 6. Как рассказать про проект

1. **Идея:** 30 советов о здоровье, по одному на день.
2. **Структура:** модель `Tip` → `TipsRepository` → `TipsList` → `TipCard`. Данные отдельно от UI.
3. **Показать на экране:** прокрутка списка (цвет верхней панели меняется), нажатие на карточку (раскрытие, поворот стрелки), смена светлой и тёмной темы, поворот экрана (раскрытая карточка остаётся раскрытой).
4. **Почему так:**
   - `LazyColumn` вместо `Column`, чтобы не создавать все 30 карточек сразу;
   - `rememberSaveable` вместо `remember`, чтобы состояние пережило поворот;
   - иконки вместо фото, потому что они векторные и красятся цветами темы;
   - цвета только из `MaterialTheme`, поэтому тёмная тема работает без отдельного кода.
5. **Чего нет:** `ViewModel` и навигации, это Unit 4. Состояние раскрытия хранится прямо в карточке.

## 7. Как запустить

Открыть папку `Unit3/Thirty-days` в Android Studio, дождаться синхронизации Gradle, выбрать эмулятор или устройство и запустить конфигурацию `app`.
