package com.example.thirtydays.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Совет на один день.
 *
 * @param day номер дня (1–30)
 * @param titleRes короткий заголовок
 * @param descriptionRes подробное описание (появляется при раскрытии карточки)
 * @param icon символ для иллюстрации совета
 */
data class Tip(
    val day: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val icon: ImageVector
)
