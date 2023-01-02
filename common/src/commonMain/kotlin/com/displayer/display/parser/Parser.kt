package com.displayer.display.parser

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.displayer.config.Parameters
import com.displayer.display.Padding
import com.displayer.display.Style
import com.displayer.display.container.ColumnLayout
import com.displayer.display.container.Container
import com.displayer.display.container.Empty
import com.displayer.display.container.RowLayout
import com.displayer.display.container.StackLayout
import com.displayer.display.item.Clock
import com.displayer.display.item.DrinkItem
import com.displayer.display.item.Image
import com.displayer.display.item.Item
import com.displayer.display.item.RandomItem
import com.displayer.display.item.SocialItem
import com.displayer.display.item.TextItem
import com.displayer.display.item.UnknownItem
import com.displayer.display.item.Weather
import com.displayer.parse
import com.displayer.weather.WeatherData
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow

object Parser {

    fun parseStyle(dto: StyleDto): Result<Style> = Result(
        Style(
            id = dto.id,
            backgroundColor = dto.backgroundColor?.let { Color.parse(it) } ?: Color.Unspecified,
            contentColor = dto.contentColor?.let { Color.parse(it) } ?: Color.Unspecified,
        ), emptyList())

    private fun parseItem(
        dto: ItemDto,
        context: String,
        resolveStyle: (styleId: String?, context: String) -> Style?,
        observeCurrentWeather: () -> Flow<List<WeatherData>>
    ): Result<Item> =
        when (dto) {
            is UnknownDto -> UnknownItem(style = resolveStyle(dto.styleId, context), padding = parsePadding(dto.padding))
            is ClockDto -> Clock(style = resolveStyle(dto.styleId, context), padding = parsePadding(dto.padding))
            is RandomDto -> RandomItem(
                style = resolveStyle(dto.styleId, context), padding = parsePadding(dto.padding), items = parseItems(
                    dtos = dto.items,
                    context = context,
                    resolveStyle = resolveStyle,
                    observeCurrentWeather = observeCurrentWeather
                ).data.toImmutableList()
            )

            is ImageDto -> Image(style = resolveStyle(dto.styleId, context), padding = parsePadding(dto.padding), url = dto.url, scale = parseContentScale(dto.scale))
            is TextDto -> TextItem(style = resolveStyle(dto.styleId, context), padding = parsePadding(dto.padding), text = dto.text)
            is WeatherDto -> Weather(style = resolveStyle(dto.styleId, context), padding = parsePadding(dto.padding), observeCurrentWeather = observeCurrentWeather)
            is DrinkDto -> DrinkItem(style = resolveStyle(dto.styleId, context), padding = parsePadding(dto.padding), image = dto.image, text = dto.text)
            is SocialDto -> SocialItem(style = resolveStyle(dto.styleId, context), padding = parsePadding(dto.padding), app = dto.app, account = dto.account, text = dto.text)
        }.run {
            val messages = mutableListOf<Message>()
            if (this is UnknownItem) {
                messages.add(Message(Severity.Error, "$context is of unknown type"))
            } else if (this is RandomItem && this.items.isEmpty()) {
                messages.add(Message(Severity.Error, "$context contains no child items"))
            } else if (this is Image && !this.url.startsWith("https://")) {
                messages.add(Message(Severity.Error, "$context uses a URL that does not start with 'https://'"))
            }
            Result(this, messages)
        }

    private fun parsePadding(dto: PaddingDto?): Padding = Padding(horizontal = dto?.horizontal ?: 0f, vertical = dto?.vertical ?: 0f)

    private fun parseItems(
        dtos: List<ItemDto>?,
        context: String,
        resolveStyle: (String?, String) -> Style?,
        observeCurrentWeather: () -> Flow<List<WeatherData>>
    ): Result<List<Item>> {
        val messages = mutableListOf<Message>()
        return (dtos
            ?.mapIndexed { index, itemDto ->
                val itemContext = "Item ${index + 1} in ${context.lowercase()}"
                parseItem(
                    dto = itemDto,
                    context = itemContext,
                    resolveStyle = resolveStyle,
                    observeCurrentWeather = observeCurrentWeather
                )
                    .andReport(messages)
            }
            ?.filter { it !is UnknownItem }
            ?: emptyList())
            .run {
                Result(this, messages)
            }
    }

    private fun parseContentScale(dto: ScaleDto?): ContentScale = when (dto) {
        ScaleDto.Crop -> ContentScale.Crop
        else -> ContentScale.Fit
    }

    fun parseParameters(dto: ParametersDto?): Parameters = with(dto ?: ParametersDto()) {
        Parameters(
            language = language,
            country = country,
            zip = zip,
            units = units ?: getUnitsForCountry(country),
        )
    }

    fun parseContainer(
        dto: ContainerDto?,
        context: String,
        resolveStyle: (String?, String) -> Style?,
        observeCurrentWeather: () -> Flow<List<WeatherData>>,
        required: Boolean = false,
    ): Result<Container> {
        val messages = mutableListOf<Message>()
        if (required && dto?.items.isNullOrEmpty()) {
            messages.add(Message(Severity.Error, "$context contains no items"))
            return Result(Empty(), messages)
        }

        val style = resolveStyle(dto?.styleId, context)
        val items = parseItems(
            dtos = dto?.items,
            context = context,
            resolveStyle = resolveStyle,
            observeCurrentWeather = observeCurrentWeather
        ).andReport(messages)

        val container = when (dto) {
            is StackDto -> StackLayout(
                padding = dto.padding?.let { Padding(it.horizontal, it.vertical) } ?: StackLayout.DEFAULT_PADDING,
                style = style,
                items = items.toImmutableList(),
                autoAdvanceInSeconds = dto.autoAdvanceInSeconds ?: StackLayout.DEFAULT_AUTO_ADVANCE_IN_SECONDS,
            )

            is ColumnDto -> {
                val scrollSpeedInSeconds = dto.scrollSpeedInSeconds ?: ColumnLayout.DEFAULT_SCROLL_SPEED

                ColumnLayout(
                    padding = dto.padding?.let { Padding(it.horizontal, it.vertical) }
                        ?: (if (scrollSpeedInSeconds > 0) ColumnLayout.DEFAULT_PADDING_SCROLLING else ColumnLayout.DEFAULT_PADDING_STATIC),
                    style = style,
                    items = injectDivider(items, dto.divider?.let {
                        parseItem(it, "Divider in ${context.lowercase()}", resolveStyle, observeCurrentWeather).andReport(messages)
                    }).toImmutableList(),
                    spacing = dto.spacing,
                    scrollSpeedSeconds = scrollSpeedInSeconds,
                )
            }

            is RowDto -> {
                val scrollSpeedInSeconds = dto.scrollSpeedInSeconds ?: RowLayout.DEFAULT_SCROLL_SPEED
                RowLayout(
                    padding = dto.padding?.let { Padding(it.horizontal, it.vertical) }
                        ?: (if (scrollSpeedInSeconds > 0) RowLayout.DEFAULT_PADDING_SCROLLING else RowLayout.DEFAULT_PADDING_STATIC),
                    style = style,
                    items = injectDivider(items, dto.divider?.let {
                        parseItem(it, "Divider in ${context.lowercase()}", resolveStyle, observeCurrentWeather).andReport(messages)
                    }).toImmutableList(),
                    spacing = dto.spacing,
                    scrollSpeedSeconds = scrollSpeedInSeconds,
                )
            }

            null -> Empty()
        }
        return Result(container, messages)
    }

    private fun injectDivider(
        items: List<Item>,
        divider: Item?,
    ): List<Item> = items
        .mapIndexed { index, item ->
            listOf(
                if (index == 0) null else divider,
                item,
            )
        }
        .flatten()
        .filterNotNull()

    private fun getUnitsForCountry(country: String): Units = when (country) {
        "US", "LR", "MM" -> Units.Imperial
        else -> Units.Metric
    }


}
