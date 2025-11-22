package org.bin.demo.token

import android.webkit.CookieManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.Headers
import org.bin.demo.debug
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    fun getAccessToken(): Flow<String?> {
        debug("getAccessToken !")
        return dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN_KEY]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]
        }
    }

    suspend fun saveAccessToken(token: String){
        dataStore.edit { prefs ->
            debug("token ! $token")
            prefs[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun deleteAccessToken(){
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
        }
    }

    fun saveRefreshTokenToWebView(headers: Headers, domain: String) {
        val cookieManager = CookieManager.getInstance()
        val setCookieHeaders = headers.values("Set-Cookie")

        for (cookieString in setCookieHeaders) {
            if (cookieString.contains("refreshToken=")) {
                cookieManager.setCookie(domain, cookieString)
                debug("TokenManager", "웹뷰에 refreshToken 쿠키 설정됨: $cookieString")
            }
        }
        cookieManager.flush() // 즉시 적용
    }

}