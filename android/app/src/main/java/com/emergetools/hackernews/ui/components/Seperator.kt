package com.emergetools.hackernews.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ColumnSeparator(
  lineColor: Color,
  size: Dp = 0.5.dp
) {
  Spacer(
    modifier = Modifier
      .fillMaxWidth()
      .height(size)
      .background(color = lineColor)
  )
}
