package com.displayer

import androidx.compose.ui.graphics.Color
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ColorIsLightTest {

    @Test
    fun blackIsDark() {
        assertFalse(Color.Black.isLight())
    }

    @Test
    fun whiteIsLight() {
        assertTrue(Color.White.isLight())
    }

    @Test
    fun redIsDark() {
        assertFalse(Color.Red.isLight())
    }

    @Test
    fun blueIsDark() {
        assertFalse(Color.Blue.isLight())
    }

    @Test
    fun yellowIsLight() {
        assertTrue(Color.Yellow.isLight())
    }

}
