package com.medios.xmppmessenger.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

@Immutable
data class CustomColorsPalette(
    val MessageOutBox: Color = Color.Unspecified,
    val MessageInput: Color = Color.Unspecified,
    val MessageInputBorder: Color = Color.Unspecified,
    val MessageBubbleCurrentUser: Color = Color.Unspecified,
    val MessageBubbleRemoteUser: Color = Color.Unspecified,
    val MessageHour: Color = Color.Unspecified
)

data class XMPPMessengerColorsPalette(
    val lightColors: CustomColorsPalette,
    val darkColors: CustomColorsPalette
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

var OnDarkCustomColorsPalette = CustomColorsPalette(
    MessageOutBox = Color(0xFF171717),
    MessageInput = Color(0xFF2C2B2D),
    MessageInputBorder = Color(0xFF171717),
    MessageBubbleCurrentUser = Color(0xFF005045),
    MessageBubbleRemoteUser = Color(0xFF363537),
    MessageHour = Color(0xFF91B0B3)
)

var OnLightCustomColorsPalette = CustomColorsPalette(
    MessageOutBox = Color(0xFFF6F6F6),
    MessageInput = Color(0xFFFFFFFF),
    MessageInputBorder = Color(0xFFB3B3B3),
    MessageBubbleCurrentUser = Color(0xFFE1FFD4),
    MessageBubbleRemoteUser = Color.White,
    MessageHour = Color.Gray
)