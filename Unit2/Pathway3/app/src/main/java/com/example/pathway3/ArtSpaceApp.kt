package com.example.pathway3

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pathway3.ui.theme.Pathway3Theme

@Composable
fun ArtSpaceApp() {
    var currentStep by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .safeDrawingPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        // Artwork Display
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp
        ) {
            val imageResource = when (currentStep) {
                1 -> R.drawable.art_1
                2 -> R.drawable.art_2
                else -> R.drawable.art_3
            }
            Image(
                painter = painterResource(imageResource),
                contentDescription = null,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Artwork Details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFECECEC))
                .padding(16.dp)
        ) {
            val titleResource = when (currentStep) {
                1 -> R.string.art_1_title
                2 -> R.string.art_2_title
                else -> R.string.art_3_title
            }
            val artistResource = when (currentStep) {
                1 -> R.string.art_1_artist
                2 -> R.string.art_2_artist
                else -> R.string.art_3_artist
            }
            Text(
                text = stringResource(titleResource),
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(artistResource),
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Controller Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { 
                    if (currentStep > 1) currentStep-- else currentStep = 3 
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.previous))
            }
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            Button(
                onClick = { 
                    if (currentStep < 3) currentStep++ else currentStep = 1 
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.next))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtSpaceAppPreview() {
    Pathway3Theme {
        ArtSpaceApp()
    }
}
