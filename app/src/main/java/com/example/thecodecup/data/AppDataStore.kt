package com.example.thecodecup.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

private val Context.appDataStore by preferencesDataStore(name = "thecodecup_datastore")

class AppDataStore(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    private object Keys {
        val appStateJson = stringPreferencesKey("app_state_json")
    }

    suspend fun loadState(): PersistedAppState? {
        val preferences = context.appDataStore.data.first()
        val json = preferences[Keys.appStateJson]
        return json?.let { safeDecode(it) }
    }

    suspend fun saveState(state: PersistedAppState) {
        val json = gson.toJson(state)
        context.appDataStore.edit { prefs ->
            prefs[Keys.appStateJson] = json
        }
    }

    suspend fun clearAll() {
        context.appDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private fun safeDecode(json: String): PersistedAppState? {
        return try {
            gson.fromJson(json, PersistedAppState::class.java)
        } catch (_: Exception) {
            null
        }
    }
}


