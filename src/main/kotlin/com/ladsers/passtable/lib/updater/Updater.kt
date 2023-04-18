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

    /**
     * The latest version tag. Default value before accessing the remote server: "unknown".
     */
    var lastVer: String = "unknown"
        private set

    /**
     * Check for new versions of applications.
     *
     * The search is performed by [platform] in the Json file on remote server.
     * The comparison is based on the [currentVerTag] and the latest one.
     * @return result code.
     * @see UpdaterCheckResult
     */
    fun check(platform: Platform, currentVerTag: String): UpdaterCheckResult {
        /* Get data from file on the remote server */
        val jsonData: String

        try {
            val scanner = Scanner(source.openStream()).useDelimiter("\\Z")
            jsonData = scanner.next()
            scanner.close()
        } catch (e: Exception) {
            return UpdaterCheckResult.CONNECTION_ERROR
        }

        /* Parse the received data */
        val appVersion: AppVersion

        try {
            val json = Json { ignoreUnknownKeys = true }
            appVersion = json.decodeFromString<AppVersion>(jsonData)
        } catch (e: Exception) {
            return UpdaterCheckResult.PARSING_ERROR
        }

        /* Get the version tag */
        lastVer = appVersion.getVerTag(platform)

        return if (currentVerTag == lastVer) UpdaterCheckResult.UP_TO_DATE else UpdaterCheckResult.NEED_UPDATE
    }
}