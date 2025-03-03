package com.helloyanis.streetmeet.utils

        import android.content.Context
        import androidx.compose.runtime.Composable
        import androidx.compose.runtime.LaunchedEffect
        import androidx.compose.runtime.getValue
        import androidx.compose.runtime.mutableIntStateOf
        import androidx.compose.runtime.mutableStateOf
        import androidx.compose.runtime.remember
        import androidx.compose.runtime.setValue
        import androidx.compose.ui.platform.LocalContext
        import androidx.datastore.preferences.core.edit
        import androidx.datastore.preferences.core.intPreferencesKey
        import androidx.datastore.preferences.core.stringPreferencesKey
        import androidx.datastore.preferences.preferencesDataStore
        import com.helloyanis.streetmeet.R
        import kotlinx.coroutines.flow.Flow
        import kotlinx.coroutines.flow.map

        private val Context.dataStore by preferencesDataStore(name = "settings")

        class DataStoreTalker(private val context: Context) {

            private val MESSAGE_KEY = stringPreferencesKey("savedMessage")
            private val NAME_KEY = stringPreferencesKey("savedName")
            private val TIME_KEY = intPreferencesKey("time")

            val messageFlow: Flow<String> = context.dataStore.data
                .map { preferences ->
                    preferences[MESSAGE_KEY] ?: "Hello there"
                }

            val nameFlow: Flow<String> = context.dataStore.data
                .map { preferences ->
                    preferences[NAME_KEY] ?: "Anonyme"
                }

            val activationTimeFlow: Flow<Int> = context.dataStore.data
                .map { preferences ->
                    preferences[TIME_KEY] ?: 0
                }

            val messageValue: String
                get() = messageFlow.toString()

            val nameValue: String
                get() = nameFlow.toString()

            val activationTimeValue: String
                get() = activationTimeFlow.toString()

            suspend fun setMessageInDataStore(newMessage: String) {
                context.dataStore.edit { preferences ->
                    preferences[MESSAGE_KEY] = newMessage
                }
            }

            suspend fun setNameInDataStore(newName: String) {
                context.dataStore.edit { preferences ->
                    preferences[NAME_KEY] = newName
                }
            }

            suspend fun setActivationTimeInDataStore(time: Int) {
                context.dataStore.edit { preferences ->
                    preferences[TIME_KEY] = time
                }
            }

        }
