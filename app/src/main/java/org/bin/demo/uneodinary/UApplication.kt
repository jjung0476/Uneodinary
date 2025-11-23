package org.bin.demo.uneodinary

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UApplication : Application() {


    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    init{
        instance = this
    }

    companion object {
        const val LOG_TAG = "Test_Hakerton"

        lateinit var instance: UApplication

        fun getUAppContext() : Context {
            return instance.applicationContext
        }

        fun extractNumber(inputString: String): Long {
            val numericString = inputString.replace(Regex("[^0-9]"), "")
            return numericString.toLongOrNull() ?: 0L
        }

    }

}
