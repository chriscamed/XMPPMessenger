package com.medios.xmppmessenger.theme

object XMPPMessengerTheme {

    fun updatePalette(colors: XMPPMessengerColorsPalette) {
        OnLightCustomColorsPalette = colors.lightColors
        OnDarkCustomColorsPalette = colors.darkColors
    }

    val defaultColors = XMPPMessengerColorsPalette(OnLightCustomColorsPalette, OnDarkCustomColorsPalette)

}