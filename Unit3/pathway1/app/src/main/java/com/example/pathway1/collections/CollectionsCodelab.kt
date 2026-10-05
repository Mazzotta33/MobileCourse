package com.example.pathway1.collections

// Codelab 2: Use collections in Kotlin
// https://developer.android.com/codelabs/basic-android-kotlin-compose-collections
// Массивы, списки, множества и словари. Запуск: fun main() внизу.

// ---------- 2. Arrays ----------
fun arraysDemo() {
    val rockPlanets = arrayOf<String>("Mercury", "Venus", "Earth", "Mars")
    val gasPlanets = arrayOf("Jupiter", "Saturn", "Uranus", "Neptune")
    val solarSystem = rockPlanets + gasPlanets

    // Доступ по индексу (с нуля)
    println(solarSystem[0])
    println(solarSystem[1])
    println(solarSystem[2])
    println(solarSystem[3])
    println(solarSystem[4])
    println(solarSystem[5])
    println(solarSystem[6])
    println(solarSystem[7])

    // Изменять элементы можно...
    solarSystem[3] = "Little Earth"
    println(solarSystem[3])

    // ...а размер массива менять нельзя
    try {
        solarSystem[8] = "Pluto"
    } catch (e: ArrayIndexOutOfBoundsException) {
        println("Exception: ${e.message}")
    }

    val newSolarSystem = arrayOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto")
    println(newSolarSystem[8])
}

// ---------- 3. Lists ----------
fun listsDemo() {
    // listOf — неизменяемый список
    val planets = listOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")
    println(planets.size)
    println(planets[2])
    println(planets.get(3))
    println(planets.indexOf("Earth"))
    println(planets.indexOf("Pluto")) // -1, если элемента нет
    for (planet in planets) {
        println(planet)
    }

    // mutableListOf — можно добавлять, менять, удалять
    val solarSystem = mutableListOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")
    solarSystem.add("Pluto")        // в конец
    solarSystem.add(3, "Theia")     // по индексу, остальные сдвигаются

    solarSystem[3] = "Future Moon"
    println(solarSystem[3])
    println(solarSystem[9])

    solarSystem.removeAt(9)
    solarSystem.remove("Future Moon")
    println(solarSystem.contains("Pluto"))
    println("Future Moon" in solarSystem)
}

// ---------- 4. Sets ----------
// Множество хранит только уникальные элементы.
fun setsDemo() {
    val solarSystem = mutableSetOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune")
    println(solarSystem.size)
    solarSystem.add("Pluto")
    println(solarSystem.size)
    println(solarSystem.contains("Pluto"))

    solarSystem.add("Pluto") // дубликат игнорируется
    println(solarSystem.size)

    solarSystem.remove("Pluto")
    println(solarSystem.size)
    println(solarSystem.contains("Pluto"))
}

// ---------- 5. Maps ----------
// Словарь: пары "ключ to значение" (здесь планета — число спутников).
fun mapsDemo() {
    val solarSystem = mutableMapOf(
        "Mercury" to 0,
        "Venus" to 0,
        "Earth" to 1,
        "Mars" to 2,
        "Jupiter" to 79,
        "Saturn" to 82,
        "Uranus" to 27,
        "Neptune" to 14
    )
    println(solarSystem.size)

    solarSystem["Pluto"] = 5
    println(solarSystem.size)
    println(solarSystem["Pluto"])
    println(solarSystem.get("Theia")) // null, такого ключа нет

    solarSystem.remove("Pluto")
    println(solarSystem.size)

    solarSystem["Jupiter"] = 78
    println(solarSystem["Jupiter"])
}

fun main() {
    println("--- 2. Arrays ---")
    arraysDemo()
    println("--- 3. Lists ---")
    listsDemo()
    println("--- 4. Sets ---")
    setsDemo()
    println("--- 5. Maps ---")
    mapsDemo()
}
