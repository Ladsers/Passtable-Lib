import org.bouncycastle.crypto.BufferedBlockCipher
import org.bouncycastle.crypto.DataLengthException
import org.bouncycastle.crypto.InvalidCipherTextException
import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PKCS7Padding
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.encoders.Base64
import java.nio.charset.StandardCharsets
import java.security.SecureRandom

object AesEncryptor {
    private val keyPadding = charArrayOf('1', 'a', '3', 'b', '5', 'c', '7', 'd', '9', 'e', '0', 'f', '2', 'g', '4')
    @Throws(Exception::class)
    private fun CryptoProcessor(isEncryption: Boolean, data: String, password: String): String {
        var password = password
        val cipher: BufferedBlockCipher = PaddedBufferedBlockCipher(
            CBCBlockCipher(AESEngine()), PKCS7Padding()
        )
        var iKeyPadding = 0
        val newPassLength: Int
        if (password.length > 32 || password.isEmpty()) throw Exception("The password contains 0 or more than 32 characters.")
        newPassLength = if (password.length <= 16) 16 else if (password.length <= 24) 24 else 32
        val passwordBuilder = StringBuilder(password)
        while (passwordBuilder.length < newPassLength) passwordBuilder.append(keyPadding[iKeyPadding++])
        password = passwordBuilder.toString()
        val passwordBytes = password.toByteArray(StandardCharsets.UTF_8)
        val msg: ByteArray
        val iv = ByteArray(16)
        if (isEncryption) {
            if (data.isEmpty()) throw Exception("The data contains nothing.")
            msg = data.toByteArray(StandardCharsets.UTF_8)
            val random = SecureRandom()
            random.nextBytes(iv)
        } else {
            if (data.length <= 16) throw Exception("The data contains nothing.")
            val dataBytes = Base64.decode(data)
            msg = ByteArray(dataBytes.size - 16)
            System.arraycopy(dataBytes, 0, msg, 0, dataBytes.size - 16)
            System.arraycopy(dataBytes, dataBytes.size - 16, iv, 0, 16)
            Arrays.fill(dataBytes, 0.toByte())
        }
        cipher.init(isEncryption, ParametersWithIV(KeyParameter(passwordBytes), iv))
        val result = ByteArray(cipher.getOutputSize(msg.size))
        val outOff = cipher.processBytes(msg, 0, msg.size, result, 0)
        val strResult: String
        strResult = try {
            cipher.doFinal(result, outOff)
            if (isEncryption) Base64.toBase64String(Arrays.concatenate(result, iv)) else String(
                result,
                StandardCharsets.UTF_8
            ).trim { it <= ' ' }
        } catch (e: IllegalStateException) {
            "/error"
        } catch (e: DataLengthException) {
            "/error"
        } catch (e: InvalidCipherTextException) {
            "/error"
        }
        Arrays.fill(passwordBytes, 0.toByte())
        Arrays.fill(msg, 0.toByte())
        Arrays.fill(iv, 0.toByte())
        Arrays.fill(result, 0.toByte())
        return strResult
    }

    @Throws(Exception::class)
    fun Encryption(data: String, password: String): String {
        return CryptoProcessor(true, data, password)
    }

    @Throws(Exception::class)
    fun Decryption(cryptMsg: String, password: String): String {
        return CryptoProcessor(false, cryptMsg, password)
    }
}