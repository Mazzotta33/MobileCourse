package com.example.pathway1.practice

// Codelab 4: Practice: Classes and Collections
// https://developer.android.com/codelabs/basic-android-kotlin-compose-practice-classes-and-collections
// Все 7 заданий в итоговом виде. Запуск: fun main() внизу.

// ---------- Task 1: data class Event ----------
// Сначала daypart был String; в Task 2 он заменён на enum Daypart.
// description необязательное, поэтому тип String? и значение по умолчанию null.

// ---------- Task 2: enum вместо строки ----------
// Нельзя случайно написать "Morning", "morning" и "MORNING" как три разных значения.
enum class Daypart {
    MORNING,
    AFTERNOON,
    EVENING,
}

data class Event(
    val title: String,
    val description: String? = null,
    val daypart: Daypart,
    val durationInMinutes: Int,
)

// ---------- Task 7: extension property ----------
// Добавляет свойство к Event, не изменяя сам класс.
val Event.durationOfEvent: String
    get() = if (this.durationInMinutes < 60) {
        "short"
    } else {
        "long"
    }

fun main() {
    // Task 1: создаём и печатаем (toString у data class генерируется сам)
    val studyKotlin = Event(
        title = "Study Kotlin",
        description = "Commit to studying Kotlin at least 15 minutes per day.",
        daypart = Daypart.EVENING,
        durationInMinutes = 15,
    )
    println(studyKotlin)

    // Task 3: коллекция вместо отдельных переменных
    val event1 = Event(title = "Wake up", description = "Time to get up", daypart = Daypart.MORNING, durationInMinutes = 0)
    val event2 = Event(title = "Eat breakfast", daypart = Daypart.MORNING, durationInMinutes = 15)
    val event3 = Event(title = "Learn about Kotlin", daypart = Daypart.AFTERNOON, durationInMinutes = 30)
    val event4 = Event(title = "Practice Compose", daypart = Daypart.AFTERNOON, durationInMinutes = 60)
    val event5 = Event(title = "Watch latest DevBytes video", daypart = Daypart.AFTERNOON, durationInMinutes = 10)
    val event6 = Event(title = "Check out latest Android Jetpack library", daypart = Daypart.EVENING, durationInMinutes = 45)

    // MutableList: можно добавлять события, а количество даёт events.size
    val events = mutableListOf<Event>(event1, event2, event3, event4, event5, event6)

    // Task 4: короткие события (меньше 60 минут)
    val shortEvents = events.filter { it.durationInMinutes < 60 }
    println("You have ${shortEvents.size} short events.")

    // Task 5: группировка по времени суток
    val groupedEvents = events.groupBy { it.daypart }
    groupedEvents.forEach { (daypart, events) ->
        println("$daypart: ${events.size} events")
    }

    // Task 6: последний элемент (вместо events[events.size - 1])
    println("Last event of the day: ${events.last().title}")

    // Task 7: использование extension property
    println("Duration of first event of the day: ${events[0].durationOfEvent}")
}
