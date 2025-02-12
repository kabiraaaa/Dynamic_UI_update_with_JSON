package com.example.uifromjson

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object RemoteConfigHelper {

    fun initializeRemoteConfig(callback: (String) -> Unit) {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(configSettings)

        FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val json = FirebaseRemoteConfig.getInstance().getString("ui_config")
                Log.d("RemoteConfigHelper", "Fetched config: $json")
                callback(json)
            } else {
                Log.e("RemoteConfigHelper", "Failed to fetch config")
            }
        }
    }
}