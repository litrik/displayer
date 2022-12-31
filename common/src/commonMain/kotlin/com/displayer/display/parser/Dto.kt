package com.displayer.display.parser

import androidx.compose.ui.text.intl.Locale
import com.displayer.weather.WeatherData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisplayFile(
    val parameters: ParametersDto? = null,
    val styles: List<StyleDto> = emptyList(),
    val styleId: String? = null,
    val center: ContainerDto,
    val left: ContainerDto? = null,
    val bottom: ContainerDto? = null,
    val bottomHeight: Float? = null,
)

/*
    CONTAINERS
 */

@Serializable
sealed class ContainerDto {
    abstract val padding: PaddingDto?
    abstract val styleId: String?
    abstract val items: List<ItemDto>
    abstract val divider: ItemDto?
}

@Serializable
@SerialName("stack")
data class StackDto(
    override val padding: PaddingDto? = null,
    override val styleId: String? = null,
    override val items: List<ItemDto> = emptyList(),
    override val divider: ItemDto? = null,
    val autoAdvanceInSeconds: Int? = null,
) : ContainerDto()

@Serializable
@SerialName("column")
data class ColumnDto(
    override val padding: PaddingDto? = null,
    override val styleId: String? = null,
    override val items: List<ItemDto> = emptyList(),
    override val divider: ItemDto? = null,
    val spacing: Float? = null,
    val scrollSpeedInSeconds: Float? = null,
) : ContainerDto()

@Serializable
@SerialName("row")
data class RowDto(
    override val padding: PaddingDto? = null,
    override val styleId: String? = null,
    override val items: List<ItemDto> = emptyList(),
    override val divider: ItemDto? = null,
    val spacing: Float? = null,
    val scrollSpeedInSeconds: Float? = null,
) : ContainerDto()

/*
    ITEMS
 */

@Serializable
sealed class ItemDto {
    abstract val styleId: String?
    abstract val padding: PaddingDto?
}

@Serializable
@SerialName("unknown")
data class UnknownDto(
    override val styleId: String? = null,
    override val padding: PaddingDto? = null,
) : ItemDto()

@Serializable
@SerialName("clock")
data class ClockDto(
    override val styleId: String? = null,
    override val padding: PaddingDto? = null,
) : ItemDto()

@Serializable
@SerialName("text")
data class TextDto(
    val text: String,
    override val styleId: String? = null,
    override val padding: PaddingDto? = null,
) : ItemDto()

@Serializable
@SerialName("image")
data class ImageDto(
    val url: String,
    override val styleId: String? = null,
    override val padding: PaddingDto? = null,
    val scale: ScaleDto? = null,
) : ItemDto()

@Serializable
@SerialName("random")
data class RandomDto(
    override val styleId: String? = null,
    override val padding: PaddingDto? = null,
    val items: List<ItemDto> = emptyList(),
) : ItemDto()

@Serializable
@SerialName("weather")
data class WeatherDto(
    val weatherData: WeatherData? = null,
    override val styleId: String? = null,
    override val padding: PaddingDto? = null,
) : ItemDto()

@Serializable
@SerialName("drink")
data class DrinkDto(
    val image: String,
    val text: String,
    override val styleId: String? = null,
    override val padding: PaddingDto? = null,
    val spacing: Float? = null,
) : ItemDto()

@Serializable
@SerialName("social")
data class SocialDto(
    val app: SocialApp,
    val account: String,
    val text: String? = null,
    override val styleId: String? = null,
    override val padding: PaddingDto? = null,
) : ItemDto()

@Serializable
data class StyleDto(
    val id: String,
    val contentColor: String? = null,
    val backgroundColor: String? = null,
)

@Serializable
data class PaddingDto(
    val horizontal: Float = 0f,
    val vertical: Float = 0f,
)

@Serializable
data class ParametersDto(
    val refreshInMinutes: Int = 0,
    val language: String = Locale.current.language, // ISO 639-1
    val country: String = Locale.current.region, // ISO 3166
    val zip: String? = null,
    val units: Units? = null,
)

enum class Units {
    @SerialName("metric")
    Metric,

    @SerialName("imperial")
    Imperial,
}

enum class ScaleDto {
    @SerialName("crop")
    Crop,

    @SerialName("fit")
    Fit,
}

enum class SocialApp {
    @SerialName("facebook")
    Facebook,

    @SerialName("instagram")
    Instagram,

    @SerialName("tiktok")
    Tiktok,

    @SerialName("youtube")
    YouTube,

    @SerialName("snapchat")
    Snapchat,
}
