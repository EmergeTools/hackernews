package com.emergetools.hackernews.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun InfoCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun InfoCardPreview() {
    MaterialTheme {
        Surface {
            InfoCard(
                title = "Sample Title",
                content = "This is some sample content for the card component.",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun InfoCardLongContentPreview() {
    MaterialTheme {
        Surface {
            InfoCard(
                title = "Long Content Example",
                content = "This is a much longer piece of content that demonstrates how the card component handles multiple lines of text. It should wrap properly and maintain good readability.",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
