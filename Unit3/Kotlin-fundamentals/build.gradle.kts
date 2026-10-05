plugins {
    kotlin("jvm") version "2.2.20"
    application
}

kotlin {
    jvmToolchain(21)
}

// Запуск конкретного упражнения:
//   ./gradlew run -Pmain=generics.GenericsKt
application {
    mainClass.set(providers.gradleProperty("main").orElse("generics.GenericsKt"))
}

tasks.named<JavaExec>("run") {
    // Чтобы символы ▓ ▒ корректно выводились в консоли Windows
    jvmArgs("-Dstdout.encoding=UTF-8", "-Dfile.encoding=UTF-8")
}
