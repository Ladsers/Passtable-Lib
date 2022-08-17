package com.ladsers.passtable.lib

/**
 * An enumeration containing all supported versions of the passtable-file.
 */
enum class FileVersion(private val ver: Int, private val type: Int) {
    VER_2_TYPE_A(2, 1);

    /**
     * Get character that contains the version and file type.
     */
    fun char() = (ver * 10 + type).toChar()
}

/**
 * The current version of the A type file. Used to indicate the version of the file throughout the source code.
 */
val CurrentVersionFileA = FileVersion.VER_2_TYPE_A