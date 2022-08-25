package com.ladsers.passtable.lib

/**
 * An object containing methods for verifying data entered by the user.
 */
object Verifier {
    /**
     * Invalid characters of Unix & OS Windows for the file name.
     */
    const val fileNameInvalidChars = "\\ / : * ? \" < > |"

    /**
     * Invalid words of OS Windows for the file name.
     */
    const val fileNameInvalidWinWords = "COM0..COM9 LPT0..LPT9 CON PRN AUX NUL CONIN\$ CONOUT\$"

    /**
     * Get string containing allowed characters for use in the primary password.
     *
     * @return String containing allowed characters for use in the primary password.
     */
    fun getPrimaryAllowedChars(translationForSpace: String = "space") = "A..Z a..z 0..9 $translationForSpace\n" +
            "@ \$ # % & ~ ! ? = + * - _ . , : ; ' \" ` ^ ( ) < > [ ] { } \\ / |"

    /**
     * Verify the [primaryPass]word.
     *
     * @return [0] - the primary password is correct, [1] - the primary password is empty,
     * [2] - the primary password contains invalid character, [3] - the primary password starts with "/" (unacceptable),
     * [4] - the primary password is too long.
     */
    fun verifyPrimary(primaryPass: String): Int {
        return when (true) {
            primaryPass.isEmpty() -> 1
            primaryPass.contains("[^ -~]".toRegex()) -> 2
            primaryPass.startsWith("/") -> 3
            (primaryPass.length > 32) -> 4
            else -> 0
        }
    }

    /**
     * Verify the file [name].
     *
     * @return [0] - the file name is correct, [1] - the file name is blank,
     * [2] - the file name contains invalid characters, [3] - the file name starts with whitespace character,
     * [4] - the file name is invalid word for OS Windows, [5] - the file name is too long.
     */
    fun verifyFileName(name: String): Int {
        return when (true) {
            name.isBlank() -> 1
            name.contains("[\\x00-\\x1F/\\\\:*?\"<>|]".toRegex()) -> 2
            name.startsWith(" ") -> 3
            name.matches("(?i)(^(COM[0-9]|LPT[0-9]|CON|CONIN\\\$|CONOUT\\\$|PRN|AUX|NUL))".toRegex()) -> 4
            (name.length > 200) -> 5
            else -> 0
        }
    }

    /**
     * Verify the data for the suitability of adding to the collection.
     *
     * @return can the data be added to the collection?
     */
    fun verifyData(vararg data: String): Boolean {
        for (d in data) {
            if (d.contains("[\\x00-\\x1F]".toRegex())) return false
        }
        return true
    }

    /**
     * Verify the data set against the item rule.
     *
     * @return can the item be added to the collection?
     */
    fun verifyItem(note: String, username: String, password: String) =
        note.isNotBlank() || (username.isNotBlank() && password.isNotEmpty())

    /**
     * Verify the tag for the suitability of adding to the item.
     *
     * @return can the tag be assigned to the item?
     */
    fun verifyTag(tag: String) = tag in "0".."5" && tag.length == 1
}