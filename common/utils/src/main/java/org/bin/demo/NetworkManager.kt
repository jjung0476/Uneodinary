package org.bin.demo

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(@ApplicationContext val context: Context) {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var callback: NetworkCallback? = null

    init {
        startMonitoring()
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun startMonitoring() {
        callback = NetworkCallback()
        connectivityManager.registerDefaultNetworkCallback(callback!!)
    }

    fun stopMonitoring() {
        callback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
    }

    private inner class NetworkCallback : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            debug("onAvailable !")
            _isConnected.value = true
//            onNetworkConnected()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            debug("onLost !")
            _isConnected.value = false
//            onNetworkDisconnected()
        }
    }

    private fun onNetworkConnected() {
//        val intent = Intent(context, ComposeMainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
//        )

//        val notificationId = System.currentTimeMillis().toInt()
//        NotificationHelper.showNotification(
//            context,
//            "네트워크 연결",
//            "network_connected",
//            "네트워크 연결 됨",
//            "메인 액티비티로 이동",
//            notificationId,
//            pendingIntent
//        )
    }

    private fun onNetworkDisconnected() {
//        val intent = Intent(context, LockControlTestSerialActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notificationId = System.currentTimeMillis().toInt()
//        NotificationHelper.showNotification(
//            context,
//            "네트워크 연결 끊김",
//            "network_disconnected",
//            "네트워크 연결 끊김",
//            "시리얼 태스트 액티비티로 이동",
//            notificationId,
//            pendingIntent
//        )
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun isCurrentlyConnected(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
