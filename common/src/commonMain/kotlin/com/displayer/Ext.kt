package com.displayer

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Color.Companion.parse(colorString: String): Color =
    with(colorString.removePrefix("#")) {
        try {
            when (length) {
                8 -> {
                    val color = (toLong(16) and 0xFFFFFF00) shr 8
                    val alpha = toLong(16) and 0xFF
                    Color(alpha shl 24 or color)
                }
                6 -> Color(toLong(16) or 0x00000000FF000000)
                else -> Unspecified
            }
        } catch (e: Exception) {
            Unspecified
        }
    }

fun Color.isLight(): Boolean = luminance() > 0.179f

@Composable
fun Dp.toSp() = with(LocalDensity.current) { this@toSp.toSp() }

@Composable
fun Dp.toPx(): Float = with(LocalDensity.current) { this@toPx.toPx() }
