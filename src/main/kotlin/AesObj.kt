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

/**
 * An object containing methods for encrypting and decrypting data.
 *
 * The core logic of cryptography is implemented here.
 */
object AesObj {
    /**
     * The sequence of characters required to increase the length of the password.
     */
    private val keyPadding = charArrayOf('1', 'a', '3', 'b', '5', 'c', '7', 'd', '9', 'e', '0', 'f', '2', 'g', '4')

    /**
     * The method in which the cryptography process is implemented.
     *
     * Processing [data] using [password]. [isEncryption] determines the direction of encryption.
     * @return Processed [data].
     */
    @Throws(Exception::class)
    private fun cryptoProcessor(isEncryption: Boolean, data: String, password: String): String {
        var passwordFull = password
        val cipher: BufferedBlockCipher = PaddedBufferedBlockCipher(
            CBCBlockCipher(AESEngine()), PKCS7Padding()
        )
        /* Padding the password to a length of 16, 24, or 32 characters. */
        if (passwordFull.length > 32 || passwordFull.isEmpty())
            throw Exception("The password contains 0 or more than 32 characters.")
        val newPassLength = if (passwordFull.length <= 16) 16 else if (passwordFull.length <= 24) 24 else 32
        var iKeyPadding = 0
        val passwordBuilder = StringBuilder(passwordFull)
        while (passwordBuilder.length < newPassLength) passwordBuilder.append(keyPadding[iKeyPadding++])
        passwordFull = passwordBuilder.toString()
        /* Configuring data for the cryptographic process depending on the selected mode: encryption / decryption. */
        val passwordBytes = passwordFull.toByteArray(StandardCharsets.UTF_8)
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
        /* Running the cryptography process. */
        cipher.init(isEncryption, ParametersWithIV(KeyParameter(passwordBytes), iv))
        val result = ByteArray(cipher.getOutputSize(msg.size))
        val outOff = cipher.processBytes(msg, 0, msg.size, result, 0)
        val strResult = try {
            cipher.doFinal(result, outOff)
            if (isEncryption) Base64.toBase64String(Arrays.concatenate(result, iv)) else String(
                result,
                StandardCharsets.UTF_8
            )
        } catch (e: IllegalStateException) {
            "/error"
        } catch (e: DataLengthException) {
            "/error"
        } catch (e: InvalidCipherTextException) {
            "/error"
        }
        /* Filling all used arrays with zeros. */
        Arrays.fill(passwordBytes, 0.toByte())
        Arrays.fill(msg, 0.toByte())
        Arrays.fill(iv, 0.toByte())
        Arrays.fill(result, 0.toByte())

        return strResult
    }

    /**
     * Encrypt [data] with the specified [password].
     *
     * @throws [data] contains nothing.
     * @throws [password] contains 0 or more than 32 characters.
     * @return Encrypted message.
     */
    @Throws(Exception::class)
    fun encrypt(data: String, password: String): String {
        return cryptoProcessor(true, data, password)
    }

    /**
     * Decrypt [cryptoMsg] with the specified [password].
     *
     * @throws [cryptoMsg] contains nothing or only init vector.
     * @throws [password] contains 0 or more than 32 characters.
     * @return Decrypted data.
     */
    @Throws(Exception::class)
    fun decrypt(cryptoMsg: String, password: String): String {
        return cryptoProcessor(false, cryptoMsg, password)
    }
}