/**
 * A class for storing data of one item.
 *
 * Can store the following data: [tag], [note], [login], [password].
 * Additionally, can store a real [id] when searching.
 * @constructor Initialization with all necessary data is required. Data can be an empty string.
 */
class DataItem(var tag: String, var note: String, var login: String, var password: String, val id: Int = -1)

/**
 * An abstract class containing all user data and methods for working with them.
 *
 * @constructor When working with data from the file, specify [path] to the file,
 * [masterPass] and the encrypted [cryptData] from this file. If the file doesn't exist yet,
 * the constructor must be empty.
 */
abstract class DataTable(
    private var path: String? = null,
    private var masterPass: String? = null, private var cryptData: String = ""
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
     * It is checked when user safely close the app or open an another file.
     * It is set every time when user interact with the main collection.
     * If true, saving is not required.
     * @see dataList
     */
    var isSaved = true

    /**
     * Add an item to the main collection.
     *
     * @return [0] - success, [1] - note is empty and/or login & password is empty,
     * [2] - wrong tag.
     * @see dataList
     */
    fun add(tag: String, note: String, login: String, password: String): Int {
        if (note.isEmpty() && (login.isEmpty() || password.isEmpty())) return 1
        if (tag !in "0".."5" || tag.length != 1) return 2
        dataList.add(DataItem(tag, note, login, password))
        isSaved = false
        return 0
    }

    /**
     * Remove an item from the main collection by [id].
     *
     * @return [0] – success, [-2] – IndexOutOfBoundsException, [-1] – unhandled exception.
     * @see dataList
     */
    fun remove(id: Int): Int {
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
     * Write [new] data instead of the old for one item found by [id].
     *
     * [key]: t -> tag, n -> note, l -> login, p -> password.
     * @return [0] – success, [1] – wrong key, [-2] – IndexOutOfBoundsException,
     * [2] - note is empty and/or login & password is empty, [3] - wrong tag,
     * [-1] – unhandled exception.
     * @see dataList
     */
    fun setData(id: Int, key: String, new: String): Int {
        try {
            val oldNote = dataList[id].note
            val oldLogin = dataList[id].login
            val oldPassword = dataList[id].password
            when (key) {
                "t" -> {
                    if (new !in "0".."5" || new.length != 1) return 3
                    dataList[id].tag = new
                }
                "n" -> {
                    if (new.isEmpty() && (oldLogin.isEmpty() || oldPassword.isEmpty())) return 2
                    dataList[id].note = new
                }
                "l" -> {
                    if (oldNote.isEmpty() && (new.isEmpty() || oldPassword.isEmpty())) return 2
                    dataList[id].login = new
                }
                "p" -> {
                    if (oldNote.isEmpty() && (oldLogin.isEmpty() || new.isEmpty())) return 2
                    dataList[id].password = new
                }
                else -> return 1
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
     * Write new data: [nTag], [nNote], [nLogin], [nPassword] instead of the old for one item found by [id].
     *
     * @return [0] – success, [1] – wrong key, [-2] – IndexOutOfBoundsException,
     * [2] - note is empty and/or login & password is empty, [3] - wrong tag,
     * [-1] – unhandled exception.
     * @see dataList
     */
    fun setData(id: Int, nTag: String, nNote: String, nLogin: String, nPassword: String): Int {
        if (nNote.isEmpty() && (nLogin.isEmpty() || nPassword.isEmpty())) return 2
        if (nTag !in "0".."5" || nTag.length != 1) return 3
        try {
            dataList[id].apply {
                tag = nTag
                note = nNote
                login = nLogin
                password = nPassword
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
     * @return the collection containing all user information except passwords (passwords are hidden).
     * "/yes" - item has a password, "/no" - item has no password.
     * @see dataList
     */
    fun getData(): MutableList<DataItem> {
        val results = mutableListOf<DataItem>()
        for (data in dataList) {
            results.add(DataItem(data.tag, data.note, data.login, hasPassword(data)))
        }

        return results
    }

    /**
     * Get data from one item found by [id].
     *
     * [key]: t -> tag, n -> note, l -> login, p -> password.
     * @return [value] – success, [] – wrong key, [/error: outOfBounds] – IndexOutOfBoundsException,
     * [/error: unhandledException] – unhandled exception.
     * @see dataList
     */
    fun getData(id: Int, key: String): String {
        return try {
            when (key) {
                "t" -> dataList[id].tag
                "n" -> dataList[id].note
                "l" -> dataList[id].login
                "p" -> dataList[id].password
                else -> ""
            }
        } catch (e: Exception) {
            when (e) {
                is IndexOutOfBoundsException -> "/error: outOfBounds"
                else -> "/error: unhandledException"
            }
        }
    }

    /**
     * Get collection where notes and/or logins contain the following search [query].
     *
     * @return the filtered collection.
     * @see dataList
     */
    fun searchByData(query: String): List<DataItem> {
        val results = mutableListOf<DataItem>()
        val queryLowerCase = query.toLowerCase()
        for ((id, data) in dataList.withIndex()) {
            if (data.note.toLowerCase().contains(queryLowerCase) || data.login.toLowerCase().contains(queryLowerCase))
                results.add(DataItem(data.tag, data.note, data.login, hasPassword(data), id))
        }

        return results
    }

    /**
     * Get collection where tag contains the following search [query].
     *
     * @return the filtered collection.
     * @see dataList
     */
    fun searchByTag(query: String): List<DataItem> {
        val results = mutableListOf<DataItem>()
        for ((id, data) in dataList.withIndex()) {
            if (data.tag.contains(query)) results.add(DataItem(data.tag, data.note, data.login, hasPassword(data), id))
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
            when (true) {
                red && data.tag.contains("1"), green && data.tag.contains("2"),
                blue && data.tag.contains("3"), yellow && data.tag.contains("4"),
                purple && data.tag.contains("5") ->
                    results.add(DataItem(data.tag, data.note, data.login, hasPassword(data), id))
            }
        }

        return results
    }

    /**
     * Fill the main collection with the latest saved data (decrypt and parse data from [cryptData]).
     *
     * @return [0] – success, [2] – unsupported file version, [3] – invalid password,
     * [4] - the master password was not specified, [-2] – file is corrupted / unhandled exception.
     * @see AesObj
     */
    fun fill(): Int {
        dataList.clear()
        if (masterPass.isNullOrEmpty()) return 4
        if (cryptData.isEmpty()) return -2

        /* Checking the file version. */
        when (cryptData[0]) {
            FileVersion.VER_2_TYPE_A.char() -> {
                try {
                    /* Decrypting data. */
                    val data = AesObj.decrypt(cryptData.removeRange(0, 1), masterPass!!)
                    if (data == "/error") return 3
                    if (data == "/emptyCollection") {
                        isSaved = true
                        return 0 // if collection is empty, then parsing is not needed for further work
                    }
                    /* Parsing data by template: tag \t note \t login \t password \n. */
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
        isSaved = true
        return 0
    }

    /**
     * Encrypt and save data to the file.
     *
     * It is possible to save the file with new [path] and new [masterPass].
     * @return [0] – success, [2] – the saved data does not match the current data,
     * [-2] – encryption error, [3] – saved in the same directory as the app,
     * [-3] – error writing to file, [5] - the path to the file for save was not specified,
     * [6] - the master password was not specified.
     * @see AesObj
     */
    fun save(newPath: String? = path, newMasterPass: String? = masterPass): Int {
        /* Checking for missing information. */
        path = newPath ?: return 5
        masterPass = newMasterPass ?: return 6
        /* Preparing data for saving. */
        var res = ""
        if (dataList.isNotEmpty()) {
            /* Combining data by template: tag \t note \t login \t password \n. */
            for (data in dataList) res += data.tag + "\t" + data.note + "\t" + data.login + "\t" +
                    data.password + "\n"
            res = res.dropLast(1) // the last line doesn't contain char "\n"
        } else {
            res = "/emptyCollection"
        }
        /* Encrypting data. */
        var strToSave = CurrentVersionFileA.char().toString()
        try {
            val encrypt = AesObj.encrypt(res, masterPass!!)
            val decrypt = AesObj.decrypt(encrypt, masterPass!!) // verification of encryption success
            if (decrypt == res) strToSave += encrypt // add version char
            else return 2
        } catch (e: Exception) {
            return -2
        }
        /* Saving encrypted data to the file. */
        try {
            writeToFile(path!!, strToSave)
        } catch (e: Exception) {
            try {
                val originalName = path!!.substringAfterLast("\\").substringBeforeLast(".").plus(".passtable")
                writeToFile(originalName, strToSave) // attempt to save the file near the app itself.
                path = originalName
                cryptData = strToSave // update class property
                isSaved = true // reset the flag
                return 3
            } catch (e: Exception) {
                return -3
            }
        }
        cryptData = strToSave // update class property
        isSaved = true // reset the flag
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
     * Abstract because it can be specific to some OS.
     * It can potentially throw an exception, but it will be handled automatically during the save process.
     * @see save
     */
    abstract fun writeToFile(pathToFile: String, cryptData: String)
}