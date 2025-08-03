package jp.vpopov.ghwarriors.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jp.vpopov.ghwarriors.core.domain.model.ThemeConfig
import jp.vpopov.ghwarriors.core.domain.model.ThemePalette
import jp.vpopov.ghwarriors.core.extension.throwOnCancellation
import jp.vpopov.ghwarriors.core.logging.Logging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

interface AppPreferences {
    fun observeTheme(): Flow<ThemeConfig>
    suspend fun setTheme(theme: ThemeConfig)
    fun observeThemePalette(): Flow<ThemePalette>
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences",
    corruptionHandler = ReplaceFileCorruptionHandler {
        emptyPreferences()
    },
)

class AppPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) : AppPreferences {
    private val dataStore: DataStore<Preferences>
        get() = context.dataStore
    private val themeConfigKey = stringPreferencesKey("theme_config")
    private val themePaletteKey = stringPreferencesKey("theme_palette")

    override fun observeTheme(): Flow<ThemeConfig> {
        return dataStore.data.map { preferences ->
            ThemeConfig.entries
                .find { it.name == preferences[themeConfigKey] }
                ?: ThemeConfig.FOLLOW_SYSTEM
        }.catch { emit(ThemeConfig.FOLLOW_SYSTEM) }
    }

    override fun observeThemePalette(): Flow<ThemePalette> {
        return dataStore.data.map { preferences ->
            ThemePalette.entries
                .find { it.name == preferences[themePaletteKey] }
                ?: ThemePalette.NATURAL_GREEN
        }.catch { emit(ThemePalette.NATURAL_GREEN) }
    }

    override suspend fun setTheme(theme: ThemeConfig) {
        runCatching {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    set(themeConfigKey, theme.name)
                }
            }
        }
            .throwOnCancellation()
            .onFailure { Logging.e(it) { "Failed to set theme: ${theme.name}" } }
    }
}