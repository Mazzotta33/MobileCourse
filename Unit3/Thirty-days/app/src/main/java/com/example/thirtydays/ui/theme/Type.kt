package com.example.thirtydays.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.thirtydays.R

// Шрифты с Google Fonts (лицензия OFL), оба поддерживают кириллицу
// Montserrat Alternates — только для названия приложения (фирменный стиль)
val MontserratAlternates = FontFamily(
    Font(R.font.montserrat_alternates_bold, FontWeight.Bold)
)

val PtSans = FontFamily(
    Font(R.font.pt_sans_regular, FontWeight.Normal),
    Font(R.font.pt_sans_bold, FontWeight.Bold)
)

val Typography = Typography(
    // Заголовок в верхней панели
    displaySmall = TextStyle(
        fontFamily = MontserratAlternates,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    // Название совета
    titleLarge = TextStyle(
        fontFamily = PtSans,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    // Метка «День N»
    labelLarge = TextStyle(
        fontFamily = PtSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.8.sp
    ),
    // Описание совета
    bodyLarge = TextStyle(
        fontFamily = PtSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
)
