package com.ladsers.passtable.lib

import java.net.URL
import java.util.*

// TODO: change /Ladsers/temp-files/ to /Ladsers/Passtable/

/**
 * An object responsible for updating applications (jvm & android).
 */
object Updater {
    private val urlUpdate =
        URL("""https://raw.githubusercontent.com/Ladsers/temp-files/master/apps_current_version.ini""")
    private lateinit var lastVer: String

    /**
     * Check for new versions of applications.
     *
     * The search is performed by [platform] and [verTag] in the file on remote server (GitHub).
     * @return [0] – success: no update required, [1] – success: update is available,
     * [-1] – error: unable to parse the file on remote server.
     */
    fun check(platform: String, verTag: String): Int {
        val isNeedUpdate: Boolean

        try {
            val s = Scanner(urlUpdate.openStream())
            s.useDelimiter("[^\\S]")
            val lines = mutableListOf<String>()
            while (s.hasNextLine()) lines.add(s.next())

            isNeedUpdate = when (platform.lowercase()) {
                "jvm" -> {
                    lastVer = lines[1]
                    verTag != lines[1]
                }
                "apk" -> {
                    lastVer = lines[3]
                    verTag != lines[3]
                }
                else -> {
                    lastVer = verTag
                    false
                }
            }
        } catch (e: Exception) {
            return -1
        }

        return if (isNeedUpdate) 1 else 0
    }

    /**
     * Get the latest version tag.
     *
     * @return the latest version tag.
     */
    fun getLastVer() = lastVer
}