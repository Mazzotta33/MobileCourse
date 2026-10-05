package com.example.pathway1.generics

// Codelab 1: Generics, objects, and extensions
// https://developer.android.com/codelabs/basic-android-kotlin-compose-generics
// Итоговый код всех разделов в одном файле. Запуск: fun main() внизу.

// ---------- 2. Универсальный класс (generics) ----------
// Вместо трёх классов (FillInTheBlank, TrueOrFalse, Numeric) один класс с типом-параметром T:
// T — это тип ответа (String, Boolean, Int ...), его выбирают при создании объекта.

// ---------- 3. enum class ----------
// Сложность задаётся перечислением, а не строкой — опечатка невозможна.
enum class Difficulty {
    EASY, MEDIUM, HARD
}

// ---------- 4. data class ----------
// data class автоматически создаёт toString(), equals(), hashCode(), copy().
data class Question<T>(
    val questionText: String,
    val answer: T,
    val difficulty: Difficulty
)

// ---------- 7. interface ----------
// Интерфейс — "контракт": любой класс, который его реализует, обязан иметь эти члены.
interface ProgressPrintable {
    val progressText: String
    fun printProgressBar()
}

// ---------- 5. companion object ----------
// companion object — синглтон внутри класса: доступ через имя класса (Quiz.answered).
class Quiz : ProgressPrintable {
    val question1 = Question<String>("Quoth the raven ___", "nevermore", Difficulty.MEDIUM)
    val question2 = Question<Boolean>("The sky is green. True or false", false, Difficulty.EASY)
    val question3 = Question<Int>("How many days are there between full moons?", 28, Difficulty.HARD)

    companion object StudentProgress {
        var total: Int = 10
        var answered: Int = 3
    }

    override val progressText: String
        get() = "${answered} of ${total} answered"

    override fun printProgressBar() {
        repeat(Quiz.answered) { print("▓") }
        repeat(Quiz.total - Quiz.answered) { print("▒") }
        println()
        println(progressText)
    }

    // ---------- 8. scope function let() ----------
    // let() выполняет блок, передавая объект как it.
    fun printQuiz() {
        question1.let {
            println(it.questionText)
            println(it.answer)
            println(it.difficulty)
        }
        println()
        question2.let {
            println(it.questionText)
            println(it.answer)
            println(it.difficulty)
        }
        println()
        question3.let {
            println(it.questionText)
            println(it.answer)
            println(it.difficulty)
        }
        println()
    }
}

// ---------- 5. object (синглтон) ----------
// Отдельный объект в единственном экземпляре, создавать его не нужно.
object StudentProgress {
    var total: Int = 10
    var answered: Int = 3
}

// ---------- 6. Функции и свойства-расширения ----------
// Добавляют члены к существующему классу, не меняя его код.
val Quiz.StudentProgress.progressText: String
    get() = "${answered} of ${total} answered"

fun Quiz.StudentProgress.printProgressBar() {
    repeat(Quiz.answered) { print("▓") }
    repeat(Quiz.total - Quiz.answered) { print("▒") }
    println()
    println(Quiz.progressText)
}

fun main() {
    println("--- 2-4. Question<T> (generics, enum, data class) ---")
    val question1 = Question<String>("Quoth the raven ___", "nevermore", Difficulty.MEDIUM)
    println(question1.toString())

    println("--- 5. object ---")
    println("${StudentProgress.answered} of ${StudentProgress.total} answered.")

    println("--- 5. companion object ---")
    println("${Quiz.answered} of ${Quiz.total} answered.")

    println("--- 6. extension property / function ---")
    println(Quiz.progressText)
    Quiz.printProgressBar()

    println("--- 7. interface ---")
    Quiz().printProgressBar()

    println("--- 8. scope functions: let() и apply() ---")
    val quiz = Quiz()
    quiz.printQuiz()
    // apply() выполняет блок, внутри которого объект доступен как this
    Quiz().apply {
        printQuiz()
    }
}
