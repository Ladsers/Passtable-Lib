package com.ladsers.passtable.lib

/**
 * A class for storing data of one item.
 *
 * Can store the following data: [tag], [note], [username], [password].
 * Additionally, can store a real [id] when searching.
 * @constructor Initialization with all necessary data is required. Data can be an empty string.
 * @see Verifier.verifyData
 */
class DataItem(var tag: String, var note: String, var username: String, var password: String, val id: Int = -1)