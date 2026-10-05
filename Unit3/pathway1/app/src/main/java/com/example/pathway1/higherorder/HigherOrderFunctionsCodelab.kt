package com.example.pathway1.higherorder

// Codelab 3: Higher-order functions with collections
// https://developer.android.com/codelabs/basic-android-kotlin-compose-higher-order-functions
// forEach, map, filter, groupBy, fold, sortedBy. Запуск: fun main() внизу.

class Cookie(
    val name: String,
    val softBaked: Boolean,
    val hasFilling: Boolean,
    val price: Double
)

val cookies = listOf(
    Cookie(name = "Chocolate Chip", softBaked = false, hasFilling = false, price = 1.69),
    Cookie(name = "Banana Walnut", softBaked = true, hasFilling = false, price = 1.49),
    Cookie(name = "Vanilla Creme", softBaked = false, hasFilling = true, price = 1.59),
    Cookie(name = "Chocolate Peanut Butter", softBaked = false, hasFilling = true, price = 1.49),
    Cookie(name = "Snickerdoodle", softBaked = true, hasFilling = false, price = 1.39),
    Cookie(name = "Blueberry Tart", softBaked = true, hasFilling = true, price = 1.79),
    Cookie(name = "Sugar and Sprinkles", softBaked = false, hasFilling = false, price = 1.39)
)

// ---------- 2. forEach() и шаблоны строк ----------
// Лямбда { ... } получает элемент как it. Для обращения к свойству нужны фигурные скобки: ${it.name}.
fun forEachDemo() {
    cookies.forEach {
        println("Menu item: ${it.name}")
    }
}

// ---------- 3. map() ----------
// Преобразует каждый элемент и возвращает НОВЫЙ список. "$$" выводит символ доллара перед значением.
fun mapDemo() {
    val fullMenu = cookies.map {
        "${it.name} - $${it.price}"
    }
    println("Full menu:")
    fullMenu.forEach {
        println(it)
    }
}

// ---------- 4. filter() ----------
// Оставляет только элементы, для которых лямбда вернула true.
fun filterDemo() {
    val softBakedMenu = cookies.filter {
        it.softBaked
    }
    println("Soft cookies:")
    softBakedMenu.forEach {
        println("${it.name} - $${it.price}")
    }
}

// ---------- 5. groupBy() ----------
// Разбивает список на группы по ключу и возвращает Map<ключ, список>.
// "?: listOf()" (оператор Элвиса) подставляет пустой список, если группы нет.
fun groupByDemo() {
    val groupedMenu = cookies.groupBy { it.softBaked }
    val softBakedMenu = groupedMenu[true] ?: listOf()
    val crunchyMenu = groupedMenu[false] ?: listOf()

    println("Soft cookies:")
    softBakedMenu.forEach {
        println("${it.name} - $${it.price}")
    }
    println("Crunchy cookies:")
    crunchyMenu.forEach {
        println("${it.name} - $${it.price}")
    }
}

// ---------- 6. fold() ----------
// Сворачивает коллекцию в одно значение: начальное значение + функция (накопитель, элемент).
fun foldDemo() {
    val totalPrice = cookies.fold(0.0) { total, cookie ->
        total + cookie.price
    }
    println("Total price: $${totalPrice}")
}

// ---------- 7. sortedBy() ----------
// Возвращает новый отсортированный список по значению, которое вернула лямбда.
fun sortedByDemo() {
    val alphabeticalMenu = cookies.sortedBy {
        it.name
    }
    println("Alphabetical menu:")
    alphabeticalMenu.forEach {
        println(it.name)
    }
}

fun main() {
    println("--- 2. forEach ---")
    forEachDemo()
    println("--- 3. map ---")
    mapDemo()
    println("--- 4. filter ---")
    filterDemo()
    println("--- 5. groupBy ---")
    groupByDemo()
    println("--- 6. fold ---")
    foldDemo()
    println("--- 7. sortedBy ---")
    sortedByDemo()
}
