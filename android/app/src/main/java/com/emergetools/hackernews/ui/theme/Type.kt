package com.emergetools.hackernews.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.emergetools.hackernews.R

val plex = FontFamily(
  Font(resId = R.font.ibm_plex_sans_regular, weight = FontWeight.Normal),
  Font(resId = R.font.ibm_plex_sans_medium, weight = FontWeight.Medium),
  Font(resId = R.font.ibm_plex_sans_bold, weight = FontWeight.Bold),
)

val Typography = Typography(
  titleSmall = TextStyle(
    fontFamily = plex,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
  ),
  labelSmall = TextStyle(
    fontFamily = plex,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp
  ),
  labelMedium = TextStyle(
    fontFamily = plex,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
  ),
  /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)