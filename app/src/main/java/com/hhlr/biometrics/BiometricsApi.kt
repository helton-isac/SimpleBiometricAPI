package com.hhlr.biometrics

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.hhlr.biometrics.crypt.CryptographyManager
import com.hhlr.biometrics.utils.BiometricPromptUtils
import com.hhlr.biometrics.utils.CIPHER_TEXT_PREF
import com.hhlr.biometrics.utils.SHARED_PREFS_FILENAME

class BiometricsApi() {

    companion object {
        const val SECRET_KEY_NAME = "biometric_encryption_key"
    }

    private lateinit var cryptographyManager: CryptographyManager

    fun canAuthenticate(context: Context): Boolean {
        val canAuthenticate = BiometricManager.from(context).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showBiometricPromptForEncryption(
        activityContext: FragmentActivity,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        token: String,
        onAuthenticationSucceeded: (String?) -> Unit,
        onAuthenticationError: () -> Unit,
        onAuthenticationFailed: (Exception?) -> Unit
    ) {
        cryptographyManager = CryptographyManager()

        val cipher = cryptographyManager.getInitializedCipherForEncryption(SECRET_KEY_NAME)
        val biometricPrompt =
            BiometricPromptUtils.createBiometricPrompt(
                activityContext,
                token,
                ::encryptAndStoreServerToken,
                ::onAuthenticationError,
                ::onAuthenticationFailed,
                onAuthenticationSucceeded,
                onAuthenticationError,
                onAuthenticationFailed
            )
        val promptInfo = BiometricPromptUtils.createPromptInfo(
            title, subtitle, negativeButtonText
        )
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun encryptAndStoreServerToken(
        context: FragmentActivity,
        authResult: BiometricPrompt.AuthenticationResult,
        token: String?,
        onAuthenticationSucceeded: (plainText: String?) -> Unit,
        onAuthenticationFailedCallback: (e: Exception) -> Unit
    ) {
        try {
            if (token != null && token.isNotEmpty()) {
                token.let {
                    authResult.cryptoObject?.cipher?.apply {
                        val encryptedKeyWrapper =
                            cryptographyManager.encryptData(it, this)
                        cryptographyManager.persistCipherTextWrapperToSharedPrefs(
                            encryptedKeyWrapper,
                            context,
                            SHARED_PREFS_FILENAME,
                            Context.MODE_PRIVATE,
                            CIPHER_TEXT_PREF
                        )
                    }
                    onAuthenticationSucceeded(null)
                }
            } else {
                onAuthenticationFailedCallback(Exception("Empty Token"))
            }
        } catch (e: Exception) {
            onAuthenticationFailedCallback(e)
        }
    }

    private fun onAuthenticationError(
        errCode: Int,
        errString: CharSequence,
        onAuthenticationErrorCallback: () -> Unit
    ) {
        onAuthenticationErrorCallback()
    }

    private fun onAuthenticationFailed(onAuthenticationFailedCallback: (e: Exception?) -> Unit) {
        onAuthenticationFailedCallback(null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showBiometricPromptForDecryption(
        context: AppCompatActivity,
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onAuthenticationSucceeded: (plainText: String?) -> Unit,
        onAuthenticationError: () -> Unit,
        onAuthenticationFailed: (Exception?) -> Unit
    ) {
        cryptographyManager = CryptographyManager()

        val cipherTextWrapper =
            cryptographyManager.getCipherTextWrapperFromSharedPrefs(
                context,
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHER_TEXT_PREF
            )

        cipherTextWrapper?.let { textWrapper ->
            val cipher = cryptographyManager.getInitializedCipherForDecryption(
                SECRET_KEY_NAME, textWrapper.initializationVector
            )
            val biometricPrompt =
                BiometricPromptUtils.createBiometricPrompt(
                    context,
                    null,
                    ::decryptServerTokenFromStorage,
                    ::onAuthenticationError,
                    ::onAuthenticationFailed,
                    onAuthenticationSucceeded,
                    onAuthenticationError,
                    onAuthenticationFailed
                )
            val promptInfo = BiometricPromptUtils.createPromptInfo(
                title, subtitle, negativeButtonText
            )
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        }
    }

    private fun decryptServerTokenFromStorage(
        context: FragmentActivity,
        authResult: BiometricPrompt.AuthenticationResult,
        token: String?,
        onAuthenticationSucceededCallback: (plainText: String?) -> Unit,
        onAuthenticationFailedCallback: (Exception?) -> Unit
    ) {
        val cipherTextWrapper =
            cryptographyManager.getCipherTextWrapperFromSharedPrefs(
                context,
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHER_TEXT_PREF
            )
        cipherTextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintext =
                    cryptographyManager.decryptData(textWrapper.cipherText, it)
                onAuthenticationSucceededCallback(plaintext)
                return
            }
        }
        onAuthenticationFailedCallback(null)
    }
}
