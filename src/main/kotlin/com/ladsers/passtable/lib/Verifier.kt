package com.ladsers.passtable.lib

/**
 * An object containing methods for verifying data entered by the user.
 */
object Verifier {
    /**
     * Verify [masterPass]word.
     *
     * @return [0] - the master password is correct, [1] - the master password is empty,
     * [2] - the master password contains invalid character, [3] - the master password starts with "/" (unacceptable),
     * [4] - the master password is too long.
     */
    fun verifyMp(masterPass: String): Int {
        return when (true) {
            masterPass.isEmpty() -> 1
            masterPass.contains("[^ -~]".toRegex()) -> 2
            masterPass.startsWith("/") -> 3
            masterPass.length > 32 -> 4
            else -> 0
        }
    }

    /**
     * Get string containing allowed characters for use in the master password.
     *
     * @return String containing allowed characters for use in the master password.
     */
    fun getMpAllowedChars(translationForSpace: String = "space") = "A..Z a..z 0..9 $translationForSpace\n" +
            "@ \$ # % & ~ ! ? = + * - _ . , : ; ' \" ` ^ ( ) < > [ ] { } \\ / |"

    /**
     * Verify file [name].
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
            name.length > 200 -> 5
            else -> 0
        }
    }

    /**
     * Invalid characters of Unix & OS Windows for the file name.
     */
    const val fileNameInvalidChars = "\\ / : * ? \" < > |"

    /**
     * Invalid words of OS Windows for the file name.
     */
    const val fileNameInvalidWinWords = "COM0..COM9 LPT0..LPT9 CON PRN AUX NUL CONIN\$ CONOUT\$"
}