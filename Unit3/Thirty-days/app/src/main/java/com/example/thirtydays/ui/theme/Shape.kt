package com.example.thirtydays.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Мягкие крупные скругления: аккуратный дружелюбный стиль
val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),    // метка дня
    medium = RoundedCornerShape(16.dp),  // иллюстрация внутри карточки
    large = RoundedCornerShape(24.dp)    // карточка совета
)
