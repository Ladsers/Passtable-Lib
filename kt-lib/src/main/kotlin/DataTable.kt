/**
 * A class for storing data of one item.
 *
 * Can store the following data: [tag], [note], [login], [password].
 * @constructor Initialization with all necessary data is required. Data can be an empty string.
 */
class DataItem(var tag: String, var note: String, var login: String, var password: String)

/**
 * An abstract class containing all user data and methods for working with them.
 *
 * @constructor When working with data from the file, specify [path] to the file,
 * [masterPass] and the encrypted [cryptData] from this file. If the file doesn't exist yet,
 * the constructor must be empty.
 */
abstract class DataTable(private var path: String? = null,
                         private var masterPass: String? = null, private val cryptData: String = " "){
    /**
     * The main collection containing all items (all user data).
     *
     * @see DataItem
     */
    private val dataList = mutableListOf<DataItem>()

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
     * @see dataList
     */
    fun add(tag: String, note: String, login: String, password: String) {
        dataList.add(DataItem(tag,note,login,password))
        isSaved = false
    }

    /**
     * Remove an item from the main collection by [id].
     *
     * @return [0] – success, [-2] – IndexOutOfBoundsException, [-1] – unhandled exception.
     * @see dataList
     */
    fun delete(id: Int): Int {
        try {
            dataList.removeAt(id)
            isSaved = false
        }
        catch (e: Exception){
            return when(e){
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
     * [-1] – unhandled exception.
     * @see dataList
     */
    fun setData(id: Int, key: String, new: String): Int {
        try {
            when (key) {
                "t" -> dataList[id].tag = new
                "n" -> dataList[id].note = new
                "l" -> dataList[id].login = new
                "p" -> dataList[id].password = new
                else -> return 1
            }
            isSaved = false
        }
        catch (e:Exception){
            return when(e){
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
    fun getData(): List<DataItem> {
        val results = mutableListOf<DataItem>()
        for (data in dataList){
            val hasPassword = if (data.password.isNotEmpty()) "/yes" else "/no"
            results.add(DataItem(data.tag, data.note, data.login, hasPassword))
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
        }
        catch (e: Exception){
            when(e){
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
        for (data in dataList){
            if (data.note.contains(query) || data.login.contains(query)) results.add(data)
        }

        return results
    }

    /**
     * Get collection where tag contain the following search [query].
     *
     * @return the filtered collection.
     * @see dataList
     */
    fun searchByTag(query: String): List<DataItem> {
        val results = mutableListOf<DataItem>()
        for (data in dataList){
            if (data.tag.contains(query)) results.add(data)
        }

        return results
    }

    /**
     * Decrypt and parse data from [cryptData].
     *
     * @return [0] – success, [2] – unsupported file version, [3] – invalid password,
     * [-2] – file is corrupted / unhandled exception
     * @see AesObj
     */
    fun open(): Int {
        dataList.clear()
        if (!masterPass.isNullOrEmpty()) {
            /* Checking the file version. */
            when(cryptData[0]){
                FileVersion.VER_2_TYPE_A.char() -> {
                    try {
                        /* Decrypting data. */
                        val data = AesObj.decrypt(cryptData.removeRange(0, 1), masterPass!!)
                        if (data == "/error") return 3
                        /* Parsing data by template: tag \t note \t login \t password \n. */
                        for (list in data.split("\n")) {
                            val strs = list.split("\t")
                            dataList.add(DataItem(strs[0], strs[1], strs[2], strs[3]))
                        }
                    }
                    catch (e:Exception){
                        return -2
                    }
                }
                else -> return 2
            }
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
     * [4] – empty data, [-3] – error writing to file.
     * @see AesObj
     */
    fun save(newPath: String? = path, newMasterPass: String? = masterPass): Int {
        if (dataList.isEmpty()) return 4
        /* Getting missing information. */
        path = newPath ?: askPath()
        masterPass = newMasterPass ?: askPassword()
        /* Combining data by template: tag \t note \t login \t password \n. */
        var res = ""
        for (data in dataList) res+= data.tag + "\t" + data.note + "\t" + data.login + "\t" +
                data.password + "\n"
        res = res.dropLast(1) // the last line doesn't contain char "\n"
        var strToSave = CurrentVersionFileA.char().toString()
        /* Encrypting data. */
        try {
            val encrypt =AesObj.encrypt(res, masterPass!!)
            val decrypt = AesObj.decrypt(encrypt, masterPass!!) // verification of encryption success
            if (decrypt == res) strToSave+= encrypt // add version char
            else return 2
        }
        catch (e:Exception){
            return -2
        }
        /* Saving encrypted data to the file. */
        try {
            writeToFile(path!!, strToSave)
        }
        catch (e:Exception){
            try {
                val originalName = path!!.substringAfterLast("\\").
                substringBeforeLast(".").plus(".passtable")
                writeToFile(originalName, strToSave) // attempt to save the file near the app itself.
                path = originalName
                return 3
            }
            catch (e: Exception){
                return -3
            }
        }
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
     * Ask user for the master password if it is null.
     *
     * Abstract because it can be specific to some OS.
     * @return the master password that user entered.
     */
    abstract fun askPassword(): String

    /**
     * Ask user for the path to the file for save if it is null.
     *
     * Abstract because it can be specific to some OS.
     * @return the path that user entered.
     */
    abstract fun askPath(): String

    /**
     * The process of writing encrypted information to the passtable-file.
     *
     * Abstract because it can be specific to some OS.
     * It can potentially throw an exception, but it will be handled automatically during the save process.
     * @see save
     */
    abstract fun writeToFile(pathToFile: String, cryptData: String)
}