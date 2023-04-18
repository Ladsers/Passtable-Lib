package com.ladsers.passtable.lib.updater

import kotlinx.serialization.Serializable

@Serializable
internal class AppVersion(
    val androidRelease: String? = null,
    val jvmRelease: String? = null
) {

    private val undefined = "undefined"

    fun getVerTag(platform: Platform): String {
        return when (platform) {
            Platform.ANDROID_RELEASE -> androidRelease ?: undefined
            Platform.JVM_RELEASE -> jvmRelease ?: undefined
        }
    }
}