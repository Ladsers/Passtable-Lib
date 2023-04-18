package com.ladsers.passtable.lib.updater

import com.ladsers.passtable.lib.codes.UpdaterCheckResult
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.*

/**
 * An object responsible for updating applications. Supported platforms: jvm, android.
 */
object Updater {

    private val source = URL("""https://ladsers.com/wp-content/uploads/passtable-appversion.json""")

    var lastVer: String = "unknown"
        private set

    /**
     * Check for new versions of applications.
     *
     * The search is performed by [platform] and [currentVerTag] in the file on remote server (GitHub).
     * @return [0] – success: no update required, [1] – success: update is available,
     * [-1] – error: unable to parse the file on remote server.
     */
    fun check(platform: Platform, currentVerTag: String): UpdaterCheckResult {
        val jsonData: String

        try {
            val scanner = Scanner(source.openStream()).useDelimiter("\\Z")
            jsonData = scanner.next()
            scanner.close()
        } catch (e: Exception) {
            return UpdaterCheckResult.CONNECTION_ERROR
        }

        val appVersion: AppVersion

        try {
            val json = Json { ignoreUnknownKeys = true }
            appVersion = json.decodeFromString<AppVersion>(jsonData)
        } catch (e: Exception) {
            return UpdaterCheckResult.PARSING_ERROR
        }

        lastVer = appVersion.getVerTag(platform)

        return if (currentVerTag == lastVer) UpdaterCheckResult.UP_TO_DATE else UpdaterCheckResult.NEED_UPDATE
    }
}