package com.hhlr.biometrics.crypt

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import javax.crypto.Cipher

interface CryptographyManager {

    fun getInitializedCipherForEncryption(keyName: String): Cipher
    fun getInitializedCipherForDecryption(keyName: String, initializationVector: ByteArray): Cipher

    fun encryptData(plainText: String, cipher: Cipher): CipherTextWrapper
    fun decryptData(cipherText: ByteArray, cipher: Cipher): String

    fun persistCipherTextWrapperToSharedPrefs(
        cipherTextWrapper: CipherTextWrapper,
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    )

    fun getCipherTextWrapperFromSharedPrefs(
        context: Context,
        filename: String,
        mode: Int,
        prefKey: String
    ): CipherTextWrapper?
}

@RequiresApi(Build.VERSION_CODES.M)
fun CryptographyManager(): CryptographyManager = CryptographyManagerImpl()