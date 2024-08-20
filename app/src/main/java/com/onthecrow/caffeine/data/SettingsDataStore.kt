package com.onthecrow.caffeine.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val settingsSerializer: SettingsSerializer,
) {

    private val Context.dataStore: DataStore<CaffeineSettings> by dataStore("caffeine_settings.proto", settingsSerializer)

    val settings: Flow<CaffeineSettings> = context.dataStore.data

    suspend fun updateSettings(settings: CaffeineSettings) {
        context.dataStore.updateData { settings }
    }
}
