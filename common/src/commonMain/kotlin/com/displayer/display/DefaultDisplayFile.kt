package com.displayer.display

import com.displayer.display.parser.ClockDto
import com.displayer.display.parser.ColumnDto
import com.displayer.display.parser.DisplayFile
import com.displayer.display.parser.ImageDto
import com.displayer.display.parser.ParametersDto
import com.displayer.display.parser.RowDto
import com.displayer.display.parser.ScaleDto
import com.displayer.display.parser.StackDto
import com.displayer.display.parser.StyleDto
import com.displayer.display.parser.TextDto

val defaultDisplayFile = DisplayFile(
    parameters = ParametersDto(
        language = "en",
        country = "BE",
    ),
    styleId = "light",
    center = StackDto(
        items = listOf(
            TextDto("Displayer Demo"),
            TextDto("Lorem ipsum dolor sit amet, consectetur adipiscing elit"),
            ImageDto(url = "https://picsum.photos/id/24/960/540", scale = ScaleDto.Crop),
            TextDto("Maecenas consectetur in erat sit amet condimentum."),
            ImageDto(url = "https://picsum.photos/id/56/960/540", scale = ScaleDto.Crop),
            TextDto("Etiam nec ipsum et massa accumsan pulvinar."),
        ),
        autoAdvanceInSeconds = 5,
    ),
    left = ColumnDto(
        styleId = "darker",
        items = listOf(
            ClockDto(),
            TextDto("⚝"),
        )
    ),
    bottom = RowDto(
        styleId = "dark",
        items = listOf(
            TextDto("Maecenas consectetur in erat sit amet condimentum."),
            ImageDto(url = "https://picsum.photos/id/106/640/480"),
            TextDto("Lorem ipsum dolor sit amet, consectetur adipiscing elit"),
            ImageDto(url = "https://picsum.photos/id/159/960/540"),
            TextDto("Etiam nec ipsum et massa accumsan pulvinar."),
        ),
        divider = TextDto("•"),
    ),
    styles = listOf(
        StyleDto(id = "light", backgroundColor = "#ffffff", contentColor = "#1E9991"),
        StyleDto(id = "dark", backgroundColor = "#1E9991", contentColor = "#ffffff"),
        StyleDto(id = "darker", backgroundColor = "#17756F", contentColor = "#ffffff"),
    )
)
