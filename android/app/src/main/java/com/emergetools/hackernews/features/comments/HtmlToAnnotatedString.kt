package com.emergetools.hackernews.features.comments

import android.graphics.Typeface
import android.text.Layout
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import androidx.core.text.HtmlCompat
import com.emergetools.hackernews.ui.theme.HNOrange

/**
 * This code will be added to Compose 1.7, just some utilities to convert HTML Spanned to Annotated String
 *
 * https://android-review.googlesource.com/c/platform/frameworks/support/+/3003973/3/compose/ui/ui-text/src/androidMain/kotlin/androidx/compose/ui/text/Html.android.kt#139
 */

fun String.parseAsHtml(): AnnotatedString {
  val spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
  return spanned.toAnnotatedString()
}

private fun Spanned.toAnnotatedString(): AnnotatedString {
  return AnnotatedString.Builder(capacity = length)
    .append(this)
    .also { it.addSpans(this) }
    .toAnnotatedString()
}

private fun AnnotatedString.Builder.addSpans(spanned: Spanned) {
  spanned.getSpans(0, length, Any::class.java).forEach { span ->
    val range = TextRange(spanned.getSpanStart(span), spanned.getSpanEnd(span))
    addSpan(span, range.start, range.end)
  }
}

private fun AnnotatedString.Builder.addSpan(span: Any, start: Int, end: Int) {
  when (span) {
    is AbsoluteSizeSpan -> {}
    is AlignmentSpan -> {
      addStyle(span.toParagraphStyle(), start, end)
    }

    is Annotation -> {}
    is BackgroundColorSpan -> {
      addStyle(SpanStyle(background = Color(span.backgroundColor)), start, end)
    }

    is ForegroundColorSpan -> {
      addStyle(SpanStyle(color = Color(span.foregroundColor)), start, end)
    }

    is RelativeSizeSpan -> {
      addStyle(SpanStyle(fontSize = span.sizeChange.em), start, end)
    }

    is StrikethroughSpan -> {
      addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, end)
    }

    is StyleSpan -> {
      span.toSpanStyle()?.let { addStyle(it, start, end) }
    }

    is SubscriptSpan -> {
      addStyle(SpanStyle(baselineShift = BaselineShift.Subscript), start, end)
    }

    is SuperscriptSpan -> {
      addStyle(SpanStyle(baselineShift = BaselineShift.Superscript), start, end)
    }

    is TypefaceSpan -> {
      addStyle(span.toSpanStyle(), start, end)
    }

    is UnderlineSpan -> {
      addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
    }

    is URLSpan -> {
      span.url?.let { url ->
        addLink(
          LinkAnnotation.Url(
            url,
            styles = TextLinkStyles(
              style = SpanStyle(
                color = HNOrange
              )
            )
          ),
          start,
          end
        )
      }
    }
  }
}

private fun AlignmentSpan.toParagraphStyle(): ParagraphStyle {
  val alignment = when (this.alignment) {
    Layout.Alignment.ALIGN_NORMAL -> TextAlign.Start
    Layout.Alignment.ALIGN_CENTER -> TextAlign.Center
    Layout.Alignment.ALIGN_OPPOSITE -> TextAlign.End
    else -> TextAlign.Unspecified
  }
  return ParagraphStyle(textAlign = alignment)
}

private fun StyleSpan.toSpanStyle(): SpanStyle? {
  return when (style) {
    Typeface.BOLD -> {
      SpanStyle(fontWeight = FontWeight.Bold)
    }

    Typeface.ITALIC -> {
      SpanStyle(fontStyle = FontStyle.Italic)
    }

    Typeface.BOLD_ITALIC -> {
      SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
    }

    else -> null
  }
}

private fun TypefaceSpan.toSpanStyle(): SpanStyle {
  val fontFamily = this.typeface?.let { FontFamily(it) }
  return SpanStyle(fontFamily = fontFamily)
}