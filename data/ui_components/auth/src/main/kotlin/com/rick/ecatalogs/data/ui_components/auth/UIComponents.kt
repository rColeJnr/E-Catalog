package com.rick.ecatalogs.data.ui_components.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

/** Requirements shown under a field (password and username tips), one bullet per line. */
@Composable
fun BulletList(bullets: List<String>) {
    val bullet = "\t•\t"
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    Text(buildAnnotatedString {
        bullets.forEach {
            withStyle(style = paragraphStyle) {
                append(bullet)
                append(it)
            }
        }
    })
}

@Preview
@Composable
private fun PreviewBulletList() {
    BulletList(bullets = listOf("No whitespaces", "A minimum of 6 characters"))
}
