package com.ledwon.jakub.chessclock.ui

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val purple200 = Color(0xFFBB86FC)
val purple500 = Color(0xFF6200EE)
val purple700 = Color(0xFF3700B3)
val accentForPurple = Color(0xFF03DAC5)

val blue700 = Color(0xFF1976D2)
val blue500 = Color(0xFF1f97f3)
val blue200 = Color(0xFF90caf9)
val accentForBlue = Color(0xFFf88fc7)

val darkBlue700 = Color(0xFF303F9F)
val darkBlue500 = Color(0xFF3f51b5)
val darkBlue200 = Color(0xFF9fa8da)
val accentForDarkBlue = Color(0xFFe9e59b)

val green700 = Color(0xFF689F38)
val green500 = Color(0xFF8bc34a)
val green200 = Color(0xFFc5e1a5)
val accentForGreen = Color(0xFFb59bcf)

val darkGreen700 = Color(0xFF388e3c)
val darkGreen500 = Color(0xFF4caf50)
val darkGreen200 = Color(0xFFa5d6a7)
val accentForDarkGreen = Color(0xFFc794c3)

val orange700 = Color(0xFFE64A19)
val orange500 = Color(0xFFff5622)
val orange200 = Color(0xFFffab91)
val accentForOrange = Color(0xFF7ed4ee)

val brown700 = Color(0xFF5d4037)
val brown500 = Color(0xFF795548)
val brown200 = Color(0xFFbcaaa4)
val accentForBrown = Color(0xFFa6bbc1)

val pink700 = Color(0xFFC2185B)
val pink500 = Color(0xFFe91e64)
val pink200 = Color(0xFFf48fb2)
val accentForPink = Color(0xFF8edbb3)

val red700 = Color(0xFFd32f2f)
val red500 = Color(0xFFf44336)
val red200 = Color(0xFFef9a9a)
val accentForRed = Color(0xFF76e0de)

class SimpleColorTheme(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color
)

class DarkModeAwareSimpleColorTheme(
    light: SimpleColorTheme,
    dark: SimpleColorTheme
) {

    val lightColors: Colors = lightColors(
        primary = light.primary,
        primaryVariant = light.primaryVariant,
        secondary = light.secondary
    )

    val darkColors: Colors = darkColors(
        primary = dark.primary,
        primaryVariant = dark.primaryVariant,
        secondary = dark.secondary
    )
}

sealed class ColorTheme(val theme: DarkModeAwareSimpleColorTheme) {
    object Purple : ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = purple500,
                primaryVariant = purple700,
                secondary = accentForPurple
            ),
            dark = SimpleColorTheme(
                primary = purple200,
                primaryVariant = purple700,
                secondary = accentForPurple
            )
        )
    )

    object Green : ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = green500,
                primaryVariant = green700,
                secondary = accentForGreen
            ),
            dark = SimpleColorTheme(
                primary = green200,
                primaryVariant = green700,
                secondary = accentForGreen
            )
        )
    )

    object DarkGreen : ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = darkGreen500,
                primaryVariant = darkGreen700,
                secondary = accentForDarkGreen
            ),
            dark = SimpleColorTheme(
                primary = darkGreen200,
                primaryVariant = darkGreen700,
                secondary = accentForDarkGreen
            )
        )
    )

    object Blue : ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = blue500,
                primaryVariant = blue700,
                secondary = accentForBlue
            ),
            dark = SimpleColorTheme(
                primary = blue200,
                primaryVariant = blue700,
                secondary = accentForBlue
            )
        )
    )

    object DarkBlue: ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = darkBlue500,
                primaryVariant = darkBlue700,
                secondary = accentForDarkBlue
            ),
            dark = SimpleColorTheme(
                primary = darkBlue200,
                primaryVariant = darkBlue700,
                secondary = accentForDarkBlue
            )
        )
    )

    object Orange : ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = orange500,
                primaryVariant = orange700,
                secondary = accentForOrange
            ),
            dark = SimpleColorTheme(
                primary = orange200,
                primaryVariant = orange700,
                secondary = accentForOrange
            )
        )
    )

    object Pink : ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = pink500,
                primaryVariant = pink700,
                secondary = accentForPink
            ),
            dark = SimpleColorTheme(
                primary = pink200,
                primaryVariant = pink700,
                secondary = accentForPink
            )
        )
    )

    object Brown : ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = brown500,
                primaryVariant = brown700,
                secondary = accentForBrown
            ),
            dark = SimpleColorTheme(
                primary = brown200,
                primaryVariant = brown700,
                secondary = accentForBrown
            )
        )
    )

    object Red : ColorTheme(
        DarkModeAwareSimpleColorTheme(
            light = SimpleColorTheme(
                primary = red500,
                primaryVariant = red700,
                secondary = accentForRed
            ),
            dark = SimpleColorTheme(
                primary = red200,
                primaryVariant = red700,
                secondary = accentForRed
            )
        )
    )
}