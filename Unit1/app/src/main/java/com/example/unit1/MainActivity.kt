package com.example.unit1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unit1.pathway3.Greeting
import com.example.unit1.pathway3.GreetingImage
import com.example.unit1.pathway3.BusinessCardApp
import com.example.unit1.pathway3.ComposeArticleApp
import com.example.unit1.pathway3.ComposeQuadrantApp
import com.example.unit1.pathway3.TaskManagerApp
import com.example.unit1.ui.theme.Unit1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Unit1Theme {
                Unit1App()
            }
        }
    }
}

@Composable
fun Unit1App(modifier: Modifier = Modifier) {
    var selectedTabIndex by remember { mutableIntStateOf(5) }
    val tabs = listOf(
        "Greeting",
        "Birthday Card",
        "Article",
        "Task Manager",
        "Quadrant",
        "Business Card"
    )

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTabIndex) {
                    0 -> Greeting("Razil")
                    1 -> GreetingImage(
                        message = stringResource(R.string.happy_birthday_text),
                        from = stringResource(R.string.signature_text)
                    )
                    2 -> ComposeArticleApp()
                    3 -> TaskManagerApp()
                    4 -> ComposeQuadrantApp()
                    5 -> BusinessCardApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Unit1AppPreview() {
    Unit1Theme {
        Unit1App()
    }
}
