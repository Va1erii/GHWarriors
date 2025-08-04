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
import jp.vpopov.ghwarriors.core.extension.throwOnCancellation
import jp.vpopov.ghwarriors.core.logging.Logging
import jp.vpopov.ghwarriors.core.logging.d
import jp.vpopov.ghwarriors.core.logging.e
import jp.vpopov.ghwarriors.core.logging.withTagLazy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Interface for managing application preferences.
 * Provides reactive access to user settings and configuration options.
 */
interface AppPreferences {
    /**
     * Observes the current theme configuration.
     *
     * @return A Flow that emits the current ThemeConfig whenever it changes
     */
    fun observeTheme(): Flow<ThemeConfig>

    /**
     * Updates the theme configuration.
     *
     * @param theme The new theme configuration to apply
     */
    suspend fun setTheme(theme: ThemeConfig)
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
    private val logger by Logging.withTagLazy(AppPreferencesDataStore::class)
    private val themeConfigKey = stringPreferencesKey("theme_config")

    override fun observeTheme(): Flow<ThemeConfig> {
        return dataStore.data.map { preferences ->
            ThemeConfig.entries
                .find { it.name == preferences[themeConfigKey] }
                ?: ThemeConfig.FOLLOW_SYSTEM
        }
            .catch {
                logger.e(it) { "Failed to observe theme" }
                emit(ThemeConfig.FOLLOW_SYSTEM)
            }
    }

    override suspend fun setTheme(theme: ThemeConfig) {
        logger.d { "Set theme: ${theme.name}" }
        runCatching {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    set(themeConfigKey, theme.name)
                }
            }
        }
            .throwOnCancellation()
            .onFailure { logger.e(it) { "Failed to set theme: ${theme.name}" } }
    }
}