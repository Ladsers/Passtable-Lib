package com.ladsers.passtable.lib

/**
 * A class that implements password generation.
 */
class PasswordGenerator {
    /**
     * Collection of numbers.
     */
    val numberChars = "0123456789".toList()

    /**
     * Collection of lowercase letters.
     */
    val lowercaseLetterChars = "abcdefghijklmnopqrstuvwxyz".toList()

    /**
     * Collection of capital letters.
     */
    val capitalLetterChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList()

    /**
     * Collection of all ASCII symbols (except space char).
     */
    val symbolChars = "!\"#\$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toList()

    /**
     * Collection of easy-to-type symbols from any device.
     */
    val easySymbolChars = "@\$#&!?+*-_:".toList()

    /**
     * List of characters that should be prohibited from being used in the generator.
     * @see blockChars
     */
    private var blockedChars = listOf<Char>()


    /**
     * Flag allowing the use of a collection of numbers in the generator.
     */
    var isNumbersAllowed = true

    /**
     * Flag allowing the use of a collection of lowercase letters in the generator.
     */
    var isLowercaseLettersAllowed = true

    /**
     * Flag allowing the use of a collection of capital letters in the generator.
     */
    var isCapitalLettersAllowed = true

    /**
     * Flag allowing the use of a collection of symbols in the generator.
     */
    var isSymbolsAllowed = true

    /**
     * If true, then use an easy-to-type collection, otherwise a complete one.
     */
    var isEasySymbolsMode = true


    /**
     * Block the specified [characters] for use in the generator.
     */
    fun blockChars(characters: List<Char>) {
        blockedChars = characters
    }

    /**
     * Block the specified [characters] for use in the generator.
     * [characters] must be listed consecutively or separated by spaces.
     */
    fun blockChars(characters: String) = blockChars(characters.toList())

    /**
     * Generate password of the specified [length].
     * Parameters [minLowercaseLetters], [minSymbolsChars], [minCapitalLetters], [minNumbers] allow to set
     * the minimum required number of characters in password.
     *
     * @throws: incorrect parameters.
     * @throws: no chars in collection.
     * @return pseudo-random password.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun generate(
        length: Int,
        minLowercaseLetters: Int = 0,
        minSymbolsChars: Int = 0,
        minCapitalLetters: Int = 0,
        minNumbers: Int = 0
    ): String {
        /* Initial checks */
        if (length <= 0 || minLowercaseLetters < 0 || minSymbolsChars < 0 || minCapitalLetters < 0 || minNumbers < 0)
            throw IllegalArgumentException("Illegal arguments passed to function.")
        if (!checkGenParams(length, minLowercaseLetters, minSymbolsChars, minCapitalLetters, minNumbers))
            throw IllegalArgumentException("The sum of minimum required characters exceeds the password length.")

        /* Initializing working collections */
        val numberAllowedChars = numberChars.toMutableList()
        val capitalLetterAllowedChars = capitalLetterChars.toMutableList()
        val lowercaseLetterAllowedChars = lowercaseLetterChars.toMutableList()
        val symbolAllowedChars = if (isEasySymbolsMode) easySymbolChars.toMutableList() else symbolChars.toMutableList()

        val allAllowedChars = mutableListOf<Char>()

        /* Remove blocked chars from collections and add them to the main collection */
        fun configureCollection(flag: Boolean, minValue: Int, collection: MutableList<Char>) {
            if (flag) {
                collection.removeAll(blockedChars)
                allAllowedChars.addAll(collection)
                if (collection.isEmpty() && minValue > 0)
                    throw IllegalStateException(
                        "The minimum required number is greater than zero, " +
                                "but the character collection is empty. Too many blocked characters."
                    )
            } else if (minValue > 0)
                throw IllegalArgumentException(
                    "The minimum required number is greater than zero, " +
                            "but the use of the character collection is not allowed."
                )
        }

        configureCollection(isLowercaseLettersAllowed, minLowercaseLetters, lowercaseLetterAllowedChars)
        configureCollection(isSymbolsAllowed, minSymbolsChars, symbolAllowedChars)
        configureCollection(isCapitalLettersAllowed, minCapitalLetters, capitalLetterAllowedChars)
        configureCollection(isNumbersAllowed, minNumbers, numberAllowedChars)

        if (allAllowedChars.isEmpty()) throw IllegalStateException(
            "The collection does not contain any chars for the generator. Too many blocked characters."
        )


        /* Generate required number of chars */
        val freeIndexes = MutableList(length) { it }
        val result = CharArray(length)

        fun genRequired(minValue: Int, collection: MutableList<Char>) {
            for (i in 0..minValue.dec()) {
                val index = freeIndexes.random()
                result[index] = collection.random()
                freeIndexes.remove(index)
            }
        }

        genRequired(minLowercaseLetters, lowercaseLetterAllowedChars)
        genRequired(minSymbolsChars, symbolAllowedChars)
        genRequired(minCapitalLetters, capitalLetterAllowedChars)
        genRequired(minNumbers, numberAllowedChars)

        /* Fill the remaining length with random chars from the main collection */
        allAllowedChars.shuffle()
        for (index in freeIndexes) result[index] = allAllowedChars.random()

        return result.joinToString("")
    }

    /**
     * Check generator parameters.
     * Calculate the sum of minimum required characters and check if it exceeds the password [length].
     *
     * @return are generator parameters correct?
     */
    fun checkGenParams(
        length: Int,
        minLowercaseLetters: Int,
        minSymbolsChars: Int,
        minCapitalLetters: Int,
        minNumbers: Int
    ) = minLowercaseLetters + minSymbolsChars + minCapitalLetters + minNumbers <= length
}