package com.ledwon.jakub.chessclock.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.navigation.Actions
import com.ledwon.jakub.chessclock.util.AmbientIsDarkMode

@ExperimentalFoundationApi
@Composable
fun SettingsScreen(actions: Actions, settingsViewModel: SettingsViewModel) {

    val appDarkTheme = settingsViewModel.appDarkThemeFlow.collectAsState()
    val appColorTheme = settingsViewModel.appColorThemeFlow.collectAsState()
    val randomizePosition = settingsViewModel.randomizePosition.collectAsState()

    val isDarkMode: Boolean = AmbientIsDarkMode.current

    val context = AmbientContext.current

    settingsViewModel.command.observe(AmbientLifecycleOwner.current, {
        when (it) {
            is SettingsViewModel.Command.NavigateBack -> actions.navigateBack()
            is SettingsViewModel.Command.OpenBuyMeACoffee -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.buymeacoffee.com/leedwon")
                context.startActivity(intent)
            }
            is SettingsViewModel.Command.RateApp -> {
                //todo implement when app is published
            }
            else -> {
                //noop
            }
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    val backIcon = painterResource(id = R.drawable.ic_arrow_back_24)
                    IconButton(onClick = settingsViewModel::onBackClick) {
                        Icon(painter = backIcon, contentDescription = "navigate back")
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item { SettingHeader(modifier = Modifier.padding(bottom = 8.dp), text = "Dark mode") }
            item {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextRadioButton(
                        modifier = Modifier.padding(8.dp),
                        text = "Light",
                        selected = appDarkTheme.value == AppDarkTheme.Light,
                        onClick = { settingsViewModel.updateAppDarkTheme(AppDarkTheme.Light) }
                    )
                    TextRadioButton(
                        modifier = Modifier.padding(8.dp),
                        text = "Dark",
                        selected = appDarkTheme.value == AppDarkTheme.Dark,
                        onClick = { settingsViewModel.updateAppDarkTheme(AppDarkTheme.Dark) })
                    TextRadioButton(
                        modifier = Modifier.padding(8.dp),
                        text = "System",
                        selected = appDarkTheme.value == AppDarkTheme.SystemDefault,
                        onClick = { settingsViewModel.updateAppDarkTheme(AppDarkTheme.SystemDefault) })
                }
            }
            item {
                SettingHeader(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "Color theme"
                )
            }

            val rows = settingsViewModel.themes.chunked(3)
            items(rows) { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
                ) {
                    row.forEach { theme ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = theme == appColorTheme.value,
                                onClick = { settingsViewModel.updateAppColorTheme(theme) })
                            Box(
                                modifier = Modifier.height(64.dp).width(64.dp).padding(start = 8.dp)
                                    .background(if (isDarkMode) theme.value.colorTheme.darkColors.primary else theme.value.colorTheme.lightColors.primary)
                            )
                        }
                    }

                }
            }

            item {
                SettingHeader(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "Randomize white & black clock initial position"
                )
            }
            item {
                Text(
                    text = "Note: you can always stop the animation by clicking on rotating dice",
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Switch(
                        modifier = Modifier.width(32.dp).height(32.dp),
                        checked = randomizePosition.value,
                        onCheckedChange = settingsViewModel::updateRandomizePosition
                    )
                }
            }
            item {
                SettingHeader(
                    text = "Do you like the app?",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = settingsViewModel::onRateAppClick) {
                        Text("rate it", fontSize = 18.sp)
                    }
                    Text("or", fontSize = 18.sp)
                    TextButton(onClick = settingsViewModel::onBuyMeACoffeeClick) {
                        Text("buy me a coffee", fontSize = 18.sp)
                    }
                }

            }
        }
    }
}

@Composable
fun SettingHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text, fontSize = 22.sp)
    }
}

@Composable
fun TextRadioButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(text)
        RadioButton(
            selected = selected,
            onClick = { onClick?.invoke() }
        )
    }
}