package com.example.thirtydays.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Мягкие крупные скругления: дружелюбный «спокойный» стиль
val Shapes = Shapes(
    small = RoundedCornerShape(12.dp),   // метка дня
    medium = RoundedCornerShape(20.dp),  // иллюстрация внутри карточки
    large = RoundedCornerShape(28.dp)    // карточка совета
)
