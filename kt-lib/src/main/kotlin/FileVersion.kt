enum class FileVersion(private val ver: Int, private val type: Int){
    VER_2_TYPE_A(2,1);

    fun char() = (ver * 10 + type).toChar()
    fun setChar(str: String) = char() + str
}

val CurrentVersionFileA = FileVersion.VER_2_TYPE_A