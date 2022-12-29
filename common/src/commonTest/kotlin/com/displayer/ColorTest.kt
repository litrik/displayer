package com.displayer

import androidx.compose.ui.graphics.Color
import org.junit.Test
import kotlin.test.assertEquals

class ColorTest {

    @Test
    fun parseColorWithoutAlpha() {
        assertEquals(Color.Black, Color.parse("#000000"))
        assertEquals(Color.White, Color.parse("#ffffff"))
        assertEquals(Color.Red, Color.parse("#ff0000"))
        assertEquals(Color.Green, Color.parse("#00ff00"))
        assertEquals(Color.Blue, Color.parse("#0000ff"))
    }

    @Test
    fun parseColorWithAlpha() {
        assertEquals(Color.Black, Color.parse("#000000ff"))
        assertEquals(Color.White, Color.parse("#ffffffff"))
        assertEquals(Color.Red.copy(0.5f), Color.parse("#ff000080"))
        assertEquals(Color.Green.copy(0.5f), Color.parse("#00ff0080"))
        assertEquals(Color.Blue.copy(0.5f), Color.parse("#0000ff80"))
    }

    @Test
    fun parseColorWithoutPrefix() {
        assertEquals(Color.Black, Color.parse("000000"))
        assertEquals(Color.Black, Color.parse("000000ff"))
    }

    @Test
    fun parseText() {
        assertEquals(Color.Unspecified, Color.parse("text"))
    }

    @Test
    fun parseMissingDigit() {
        assertEquals(Color.Unspecified, Color.parse("#00000"))
    }

    @Test
    fun parseInvalidHex() {
        assertEquals(Color.Unspecified, Color.parse("#ABCXYZ"))
    }
}
