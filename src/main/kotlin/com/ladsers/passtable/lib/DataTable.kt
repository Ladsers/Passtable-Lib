package com.ladsers.passtable.lib

import com.ladsers.passtable.lib.Verifier.verifyData
import com.ladsers.passtable.lib.Verifier.verifyItem
import com.ladsers.passtable.lib.Verifier.verifyTag

/**
 * An abstract class containing all user data and methods for working with them.
 *
 * @constructor When working with data from the file, specify [path] to the file,
 * [primaryPass] and [encryptedData] from this file. If the file doesn't exist yet,
 * the constructor must be empty.
 */
abstract class DataTable(
    private var path: String? = null,
    private var primaryPass: String? = null, private var encryptedData: String = ""
) {
    /**
     * The main collection containing all items (all user data).
     *
     * @see DataItem
     */
    private val dataList = mutableListOf<DataItem>()

    /**
     * Mask a password.
     *
     * @return [/yes] - item has a password, [/no] - item has no password.
     * @see DataItem
     */
    private fun hasPassword(dataItem: DataItem) = if (dataItem.password.isNotEmpty()) "/yes" else "/no"

    /**
     * Save flag.
     *
     * It is checked when user close the app or open another file for safety.
     * It resets every time when user changes items in the main collection.
     * If true, saving is not required.
     * @see dataList
     */
    var isSaved = true

    /**
     * Add an item to the main collection.
     *
     * @return [0] - success, [1] - note is empty and/or username & password is empty,
     * [2] - wrong tag, [3] - some data contains invalid characters.
     * @see Verifier
     */
    fun add(tag: String, note: String, username: String, password: String): Int {
        if (!verifyData(note, username, password)) return 3
        if (!verifyItem(note, username, password)) return 1
        if (!verifyTag(tag)) return 2
        dataList.add(DataItem(tag, note, username, password))
        isSaved = false
        return 0
    }

    /**
     * Delete an item from the main collection by [id].
     *
     * @return [0] – success, [-2] – IndexOutOfBoundsException, [-1] – unhandled exception.
     * @see dataList
     */
    fun delete(id: Int): Int {
        try {
            dataList.removeAt(id)
            isSaved = false
        } catch (e: Exception) {
            return when (e) {
                is IndexOutOfBoundsException -> -2
                else -> -1
            }
        }
        return 0
    }

    /**
     * Write new tag instead of the old one for the item found by [id].
     *
     * @return [0] – success, [-2] – IndexOutOfBoundsException, [3] - wrong tag, [-1] – unhandled exception.
     * @see Verifier.verifyTag
     */
    fun setTag(id: Int, data: String): Int {
        try {
            if (!verifyTag(data)) return 3
            dataList[id].tag = data

            isSaved = false
        } catch (e: Exception) {
            return when (e) {
                is IndexOutOfBoundsException -> -2
                else -> -1
            }
        }
        return 0
    }

    /**
     * Write new note instead of the old one for the item found by [id].
     *
     * @return [0] – success, [-2] – IndexOutOfBoundsException, [1] - note contains invalid characters,
     * [2] - note is empty and/or username & password is empty, [-1] – unhandled exception.
     * @see Verifier
     */
    fun setNote(id: Int, data: String): Int {
        try {
            val username = dataList[id].username
            val password = dataList[id].password

            if (!verifyData(data)) return 1
            if (!verifyItem(data, username, password)) return 2
            dataList[id].note = data

            isSaved = false
        } catch (e: Exception) {
            return when (e) {
                is IndexOutOfBoundsException -> -2
                else -> -1
            }
        }
        return 0
    }

    /**
     * Write new username instead of the old one for the item found by [id].
     *
     * @return [0] – success, [-2] – IndexOutOfBoundsException, [1] - password contains invalid characters,
     * [2] - note is empty and/or username & password is empty, [-1] – unhandled exception.
     * @see Verifier
     */
    fun setUsername(id: Int, data: String): Int {
        try {
            val note = dataList[id].note
            val password = dataList[id].password

            if (!verifyData(data)) return 1
            if (!verifyItem(note, data, password)) return 2
            dataList[id].username = data

            isSaved = false
        } catch (e: Exception) {
            return when (e) {
                is IndexOutOfBoundsException -> -2
                else -> -1
            }
        }
        return 0
    }

    /**
     * Write new password instead of the old one for the item found by [id].
     *
     * @return [0] – success, [-2] – IndexOutOfBoundsException, [1] - password contains invalid characters,
     * [2] - note is empty and/or username & password is empty, [-1] – unhandled exception.
     * @see Verifier
     */
    fun setPassword(id: Int, data: String): Int {
        try {
            val note = dataList[id].note
            val username = dataList[id].username

            if (!verifyData(data)) return 1
            if (!verifyItem(note, username, data)) return 2
            dataList[id].password = data

            isSaved = false
        } catch (e: Exception) {
            return when (e) {
                is IndexOutOfBoundsException -> -2
                else -> -1
            }
        }
        return 0
    }

    /**
     * Write new data: [tag], [note], [username], [password] instead of the old for the item found by [id].
     *
     * @return [0] – success, [1] – some data contains invalid characters, [-2] – IndexOutOfBoundsException,
     * [2] - note is empty and/or username & password is empty, [3] - wrong tag, [-1] – unhandled exception.
     * @see Verifier
     */
    fun setData(id: Int, tag: String, note: String, username: String, password: String): Int {
        if (!verifyData(note, username, password)) return 1
        if (!verifyItem(note, username, password)) return 2
        if (!verifyTag(tag)) return 3
        try {
            dataList[id].apply {
                this.tag = tag
                this.note = note
                this.username = username
                this.password = password
            }
            isSaved = false
        } catch (e: Exception) {
            return when (e) {
                is IndexOutOfBoundsException -> -2
                else -> -1
            }
        }
        return 0
    }

    /**
     * Get all items from the main collection.
     *
     * @return the collection containing all user information with hidden passwords.
     * password == "/yes" - item has a password, password == "/no" - item has no password.
     * @see dataList
     */
    fun getData(): MutableList<DataItem> {
        val results = mutableListOf<DataItem>()
        for (data in dataList) {
            results.add(DataItem(data.tag, data.note, data.username, hasPassword(data)))
        }

        return results
    }

    /**
     * Get tag from the item found by [id].
     *
     * @return [value] – success, [/error: outOfBounds] – IndexOutOfBoundsException,
     * [/error: unhandledException] – unhandled exception.
     * @see dataList
     */
    fun getTag(id: Int): String {
        return try {
            dataList[id].tag
        } catch (e: Exception) {
            when (e) {
                is IndexOutOfBoundsException -> "/error: outOfBounds"
                else -> "/error: unhandledException"
            }
        }
    }

    /**
     * Get note from the item found by [id].
     *
     * @return [value] – success, [/error: outOfBounds] – IndexOutOfBoundsException,
     * [/error: unhandledException] – unhandled exception.
     * @see dataList
     */
    fun getNote(id: Int): String {
        return try {
            dataList[id].note
        } catch (e: Exception) {
            when (e) {
                is IndexOutOfBoundsException -> "/error: outOfBounds"
                else -> "/error: unhandledException"
            }
        }
    }

    /**
     * Get username from the item found by [id].
     *
     * @return [value] – success, [/error: outOfBounds] – IndexOutOfBoundsException,
     * [/error: unhandledException] – unhandled exception.
     * @see dataList
     */
    fun getUsername(id: Int): String {
        return try {
            dataList[id].username
        } catch (e: Exception) {
            when (e) {
                is IndexOutOfBoundsException -> "/error: outOfBounds"
                else -> "/error: unhandledException"
            }
        }
    }

    /**
     * Get password from the item found by [id]. Use this function only when you need to show the password openly.
     *
     * @return [value] – success, [/error: outOfBounds] – IndexOutOfBoundsException,
     * [/error: unhandledException] – unhandled exception.
     * @see dataList
     */
    fun getPassword(id: Int): String {
        return try {
            dataList[id].password
        } catch (e: Exception) {
            when (e) {
                is IndexOutOfBoundsException -> "/error: outOfBounds"
                else -> "/error: unhandledException"
            }
        }
    }

    /**
     * Get collection where notes and/or usernames contain the following search [query].
     *
     * @return the filtered collection.
     * @see dataList
     */
    fun searchByText(query: String): List<DataItem> {
        val results = mutableListOf<DataItem>()
        val queryLowerCase = query.lowercase()
        for ((id, data) in dataList.withIndex()) {
            if (data.note.lowercase().contains(queryLowerCase) || data.username.lowercase().contains(queryLowerCase))
                results.add(DataItem(data.tag, data.note, data.username, hasPassword(data), id))
        }

        return results
    }

    @Deprecated(
        message = "Incorrect function name. Use function with the correct name.",
        replaceWith = ReplaceWith("searchByText(query)")
    )
    fun searchByData(query: String) = searchByText(query)

    /**
     * Get collection where tag contains the following search [query].
     *
     * @return the filtered collection.
     * @see dataList
     */
    fun searchByTag(query: String): List<DataItem> {
        val results = mutableListOf<DataItem>()
        for ((id, data) in dataList.withIndex()) {
            if (data.tag.contains(query)) results.add(
                DataItem(
                    data.tag,
                    data.note,
                    data.username,
                    hasPassword(data),
                    id
                )
            )
        }

        return results
    }

    /**
     * Get collection where tags contain selected colors ([red], [green], [blue], [yellow], [purple]).
     *
     * @return the filtered collection.
     * @see dataList
     */
    fun searchByTag(
        red: Boolean = false,
        green: Boolean = false,
        blue: Boolean = false,
        yellow: Boolean = false,
        purple: Boolean = false
    ): List<DataItem> {
        val results = mutableListOf<DataItem>()
        for ((id, data) in dataList.withIndex()) {
            if ((red && data.tag.contains("1")) || (green && data.tag.contains("2")) ||
                (blue && data.tag.contains("3")) || (yellow && data.tag.contains("4")) ||
                (purple && data.tag.contains("5"))
            )
                results.add(DataItem(data.tag, data.note, data.username, hasPassword(data), id))
        }

        return results
    }

    /**
     * Swap the specified items in the main collection.
     */
    fun swapItems(from: Int, to: Int) {
        dataList[to] = dataList[from].also { dataList[from] = dataList[to] }
    }

    /**
     * Move an item in the main collection from the [from] position to the [to] position.
     */
    fun moveItem(from: Int, to: Int) {
        val item = dataList[from]
        dataList.remove(item)
        dataList.add(to, item)
    }

    /**
     * Fill the main collection with the latest saved data, i.e. decrypt and parse data from [encryptedData].
     *
     * @return [0] – success, [2] – unsupported file version, [3] – invalid password,
     * [-2] – file is corrupted / unhandled exception.
     * @throws: cannot be filled from empty file.
     * @throws: the primary password cannot be null or empty.
     * @see DataItem
     */
    @Throws(Exception::class)
    fun fill(): Int {
        dataList.clear()
        if (path == null) { // situation when the file has not yet been created
            isSaved = true
            return 0 // it's a normal situation
        }
        // Here you cannot work with a file that has been created, but not yet filled. Do "save" first.
        if (encryptedData.isEmpty()) throw Exception("The file contains nothing. Filling is not possible.")
        // You are probably trying to load data, but forgot to specify the primary password in the constructor.
        if (primaryPass.isNullOrEmpty()) throw Exception("The primary password was not specified.")

        /* Checking the file version. */
        when (encryptedData[0]) { // the first character in encryptedData is version indicator
            FileVersion.VER_2_TYPE_A.char() -> {
                try {
                    /* Decrypting data. */
                    val encryptedMsg = encryptedData.removeRange(0, 1) // delete version indicator
                    val data = AesObj.decrypt(encryptedMsg, primaryPass!!)
                    if (data == "/error") return 3
                    if (data == "/emptyCollection") {
                        isSaved = true
                        return 0 // if collection is empty, then parsing is not needed for further work
                    }
                    /* Parsing data by template: tag \t note \t username \t password \n. */
                    for (list in data.split("\n")) {
                        val strs = list.split("\t")
                        val item = DataItem(strs[0], strs[1], strs[2], strs[3])
                        dataList.add(item)
                    }
                } catch (e: Exception) {
                    return -2
                }
            }

            else -> return 2
        }
        isSaved = true // set the flag
        return 0
    }

    /**
     * Encrypt and save data to the file.
     *
     * It is possible to save the file with new [path] and new [primaryPass].
     * @return [0] – success, [2] – the saved data does not match the current data,
     * [-2] – encryption error, [3] – saved in the same directory as the app,
     * [-3] – error writing to file, [5] - the path to the file for save was not specified,
     * [6] - the primary password was not specified.
     * @see AesObj
     */
    fun save(newPath: String? = path, newPrimaryPass: String? = primaryPass): Int {
        /* Checking for missing information. */
        path = newPath ?: return 5
        primaryPass = newPrimaryPass ?: return 6
        /* Preparing data for saving. */
        val res: String = if (dataList.isNotEmpty()) {
            val strBuilder = StringBuilder()
            /* Combining data by template: tag \t note \t username \t password \n. */
            for (data in dataList) strBuilder.append("${data.tag}\t${data.note}\t${data.username}\t${data.password}\n")
            strBuilder.toString().dropLast(1) // the last line doesn't contain char "\n"
        } else {
            "/emptyCollection"
        }
        /* Encrypting data. */
        val strToSave: String
        try {
            val encrypt = AesObj.encrypt(res, primaryPass!!)
            val decrypt = AesObj.decrypt(encrypt, primaryPass!!) // verification of successful encryption
            if (decrypt == res) strToSave = CurrentVersionFileA.char().toString() + encrypt // add version char
            else return 2
        } catch (e: Exception) {
            return -2
        }
        /* Saving encrypted data to the file. */
        try {
            writeToFile(path!!, strToSave)
        } catch (e: Exception) {
            try {
                val osWindows = System.getProperty("os.name").startsWith("win", true)
                val delimiter = if (osWindows) "\\" else "/"
                val originalName = path!!.substringAfterLast(delimiter).substringBeforeLast(".").plus(".passtable")
                writeToFile(originalName, strToSave) // attempt to save the file near the app itself.
                path = originalName
                encryptedData = strToSave // update class property
                isSaved = true // set the flag
                return 3
            } catch (e: Exception) {
                return -3
            }
        }
        encryptedData = strToSave // update class property
        isSaved = true // set the flag
        return 0
    }

    /**
     * Get path to the file that contains the last saved version of the main collection.
     *
     * @return the file path or null.
     * @see save
     */
    fun getPath() = path

    /**
     * Get the size of the main collection.
     *
     * @return the size of the main collection.
     * @see dataList
     */
    fun getSize() = dataList.size

    /**
     * The process of writing encrypted information to the passtable-file.
     *
     * Abstract because it can be specific to different OS.
     * It can potentially throw an exception, but it will be handled automatically during the save process.
     * @see save
     */
    abstract fun writeToFile(pathToFile: String, cryptData: String)
}