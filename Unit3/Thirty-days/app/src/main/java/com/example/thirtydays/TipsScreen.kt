package com.example.thirtydays

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.thirtydays.data.TipsRepository
import com.example.thirtydays.model.Tip
import com.example.thirtydays.ui.theme.ThirtyDaysTheme

/**
 * Прокручиваемый список из 30 советов.
 */
@Composable
fun TipsList(
    tips: List<Tip>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tips, key = { it.day }) { tip ->
            TipCard(
                tip = tip,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Карточка совета: метка дня, заголовок, иллюстрация и описание.
 * Нажатие на карточку раскрывает или скрывает подробное описание
 * (spring-анимация размера, fade + expand для текста, поворот стрелки).
 */
@Composable
fun TipCard(tip: Tip, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrowRotation"
    )
    val expandDescription = stringResource(R.string.expand_content_description)

    Card(
        onClick = { expanded = !expanded },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.semantics { contentDescription = expandDescription }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DayBadge(day = tip.day)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(tip.titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
            Spacer(Modifier.height(12.dp))
            TipIllustration(tip = tip)
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = stringResource(tip.descriptionRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

/**
 * Метка «День N» в цвете primary.
 */
@Composable
fun DayBadge(day: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = stringResource(R.string.day_label, day),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * Иллюстрация совета: крупный символ на градиенте из цветов темы
 * с декоративными кругами. Цвета чередуются от дня к дню и меняются вместе
 * с темой (светлая/тёмная). Изображение декоративное: смысл передаёт заголовок.
 */
@Composable
fun TipIllustration(tip: Tip, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (start, end, foreground) = when (tip.day % 3) {
        1 -> Triple(scheme.primaryContainer, scheme.tertiaryContainer, scheme.onPrimaryContainer)
        2 -> Triple(scheme.secondaryContainer, scheme.primaryContainer, scheme.onSecondaryContainer)
        else -> Triple(scheme.tertiaryContainer, scheme.secondaryContainer, scheme.onTertiaryContainer)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Brush.linearGradient(listOf(start, end)))
    ) {
        DecorCircle(foreground.copy(alpha = 0.08f), 140.dp, Modifier.offset(x = (-110).dp, y = 40.dp))
        DecorCircle(foreground.copy(alpha = 0.06f), 90.dp, Modifier.offset(x = 120.dp, y = (-45).dp))
        DecorCircle(foreground.copy(alpha = 0.10f), 120.dp)
        Icon(
            imageVector = tip.icon,
            contentDescription = null,
            tint = foreground,
            modifier = Modifier.size(72.dp)
        )
    }
}

@Composable
private fun DecorCircle(color: Color, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Preview("Светлая тема")
@Preview("Тёмная тема", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TipCardPreview() {
    ThirtyDaysTheme {
        TipCard(tip = TipsRepository.tips.first())
    }
}
