package com.ledwon.jakub.chessclock.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.feature.common.exhaustive
import com.ledwon.jakub.chessclock.navigation.Actions
import com.ledwon.jakub.chessclock.util.LocalIsDarkMode

@ExperimentalFoundationApi
@Composable
fun SettingsScreen(actions: Actions, settingsViewModel: SettingsViewModel) {

    val appDarkTheme = settingsViewModel.appDarkTheme.collectAsState()
    val appColorTheme = settingsViewModel.appColorTheme.collectAsState()
    val randomizePosition = settingsViewModel.randomizePosition.collectAsState()
    val selectedClockType = settingsViewModel.clockType.collectAsState()
    val pulsationEnabled = settingsViewModel.pulsationEnabled.collectAsState()

    val isDarkMode: Boolean = LocalIsDarkMode.current

    val context = LocalContext.current

    settingsViewModel.command.observe(LocalLifecycleOwner.current, {
        //todo move links to build config
        when (it) {
            is SettingsViewModel.Command.NavigateBack -> actions.navigateBack()
            is SettingsViewModel.Command.OpenBuyMeACoffee -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.buymeacoffee.com/leedwon")
                context.startActivity(intent)
            }
            is SettingsViewModel.Command.RateApp -> {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.ledwon.jakub.chessclock")
                    )
                )
            }
            is SettingsViewModel.Command.OpenClockPreview -> {
                actions.openClockDisplayPreview(it.clockDisplayTypeName)
            }
            is SettingsViewModel.Command.Noop -> {

            }
        }.exhaustive
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
                    horizontalArrangement = Arrangement.SpaceEvenly
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

            val colorThemeRows = settingsViewModel.themes.chunked(3)
            items(colorThemeRows) { row ->
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
                                modifier = Modifier
                                    .height(64.dp)
                                    .width(64.dp)
                                    .padding(start = 8.dp)
                                    .clickable { settingsViewModel.updateAppColorTheme(theme) }
                                    .background(if (isDarkMode) theme.value.colorTheme.darkColors.primary else theme.value.colorTheme.lightColors.primary)
                            )
                        }
                    }

                }
            }

            item {
                SettingHeader(text = "Clock display type")
            }

            items(settingsViewModel.clockTypes) { clockType ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { settingsViewModel.updateClockType(clockType) },
                    ) {
                        Text(clockType.name)
                    }
                    Button(onClick = { settingsViewModel.onClockTypePreviewClick(clockType) }) {
                        Text("Preview")
                    }
                    RadioButton(
                        selected = clockType.display == selectedClockType.value,
                        onClick = { settingsViewModel.updateClockType(clockType) }
                    )
                }
            }

            item {
                SettingHeader(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = "Active player's pulsation enabled"
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Switch(
                        modifier = Modifier.width(32.dp).height(32.dp),
                        checked = pulsationEnabled.value,
                        onCheckedChange = settingsViewModel::updatePulsationEnabled
                    )
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