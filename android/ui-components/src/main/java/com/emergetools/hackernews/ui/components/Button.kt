package com.emergetools.hackernews.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(text = text)
    }
}

@PreviewLightDark
@Composable
fun PrimaryButtonPreview() {
    MaterialTheme {
        Surface {
            PrimaryButton(
                text = "Click Me",
                onClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PrimaryButtonDisabledPreview() {
    MaterialTheme {
        Surface {
            PrimaryButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
