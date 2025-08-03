package jp.vpopov.ghwarriors.feature.settings.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.vpopov.ghwarriors.core.designsystem.theme.GHWarriorsTheme
import jp.vpopov.ghwarriors.core.domain.model.ThemeConfig
import jp.vpopov.ghwarriors.core.extension.Localization
import jp.vpopov.ghwarriors.feature.settings.presentation.component.SettingsComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    component: SettingsComponent,
    modifier: Modifier = Modifier
) {
    val model by component.model.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Localization.settings),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSection(
                    title = stringResource(Localization.theme_title),
                    description = stringResource(Localization.theme_description)
                ) {
                    ThemePreferenceGroup(
                        selectedTheme = model.theme,
                        themeOptions = model.themeOptions,
                        onThemeSelected = component::setThemePreference
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            description?.let { desc ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ThemePreferenceGroup(
    selectedTheme: ThemeConfig,
    themeOptions: List<ThemeConfig>,
    onThemeSelected: (ThemeConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.selectableGroup()
    ) {
        themeOptions.forEach { theme ->
            ThemeOption(
                theme = theme,
                isSelected = selectedTheme == theme,
                onSelected = { onThemeSelected(theme) }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    theme: ThemeConfig,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = getThemeTitle(theme),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = getThemeDescription(theme),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun getThemeTitle(theme: ThemeConfig): String {
    return when (theme) {
        ThemeConfig.FOLLOW_SYSTEM -> stringResource(Localization.theme_system_title)
        ThemeConfig.LIGHT -> stringResource(Localization.theme_light_title)
        ThemeConfig.DARK -> stringResource(Localization.theme_dark_title)
    }
}

@Composable
private fun getThemeDescription(theme: ThemeConfig): String {
    return when (theme) {
        ThemeConfig.FOLLOW_SYSTEM -> stringResource(Localization.theme_system_description)
        ThemeConfig.LIGHT -> stringResource(Localization.theme_light_description)
        ThemeConfig.DARK -> stringResource(Localization.theme_dark_description)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsSectionPreview() {
    GHWarriorsTheme {
        Surface {
            SettingsSection(
                title = "Theme",
                description = "Choose your preferred theme"
            ) {
                ThemePreferenceGroup(
                    selectedTheme = ThemeConfig.FOLLOW_SYSTEM,
                    themeOptions = ThemeConfig.entries,
                    onThemeSelected = {}
                )
            }
        }
    }
}