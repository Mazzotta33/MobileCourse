package com.example.pathway1

import org.junit.Test
import com.example.pathway1.collections.main as collectionsMain
import com.example.pathway1.generics.main as genericsMain
import com.example.pathway1.higherorder.main as higherOrderMain
import com.example.pathway1.practice.main as practiceMain

/**
 * Запускает каждый кодлаб Pathway 1 как обычный unit-тест, чтобы увидеть вывод в окне Run
 * без эмулятора. Нажмите зелёную стрелку слева от нужного теста.
 */
class CodelabsRunTest {
    @Test
    fun codelab1_generics() = genericsMain()

    @Test
    fun codelab2_collections() = collectionsMain()

    @Test
    fun codelab3_higherOrderFunctions() = higherOrderMain()

    @Test
    fun codelab4_practiceClassesAndCollections() = practiceMain()
}
