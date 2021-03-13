package com.hhlr.biometrics.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

object BiometricPromptUtils {
    fun createBiometricPrompt(
        context: AppCompatActivity,
        token: String?,
        onAuthenticationSucceeded: (
            context: AppCompatActivity,
            result: BiometricPrompt.AuthenticationResult,
            token: String?,
            onAuthenticationSucceededCallback: (plainText: String?) -> Unit,
            onAuthenticationFailedCallback: (e: Exception?) -> Unit
        ) -> Unit,
        onAuthenticationError: (
            errCode: Int,
            errString: CharSequence,
            onAuthenticationErrorCallback: () -> Unit
        ) -> Unit,
        onAuthenticationFailed: (
            onAuthenticationFailedCallback: (e: Exception?) -> Unit
        ) -> Unit,
        onAuthenticationSucceededCallback: (plainText: String?) -> Unit,
        onAuthenticationErrorCallback: () -> Unit,
        onAuthenticationFailedCallback: (e: Exception?) -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errCode, errString)
                onAuthenticationError(errCode, errString, onAuthenticationErrorCallback)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onAuthenticationFailed(onAuthenticationFailedCallback)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onAuthenticationSucceeded(
                    context,
                    result,
                    token,
                    onAuthenticationSucceededCallback,
                    onAuthenticationFailedCallback
                )
            }
        }
        return BiometricPrompt(context, executor, callback)
    }

    fun createPromptInfo(
        title: String,
        subtitle: String,
        negativeButtonText: String,
    ): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(title)
            setSubtitle(subtitle)
            setConfirmationRequired(false)
            setNegativeButtonText(negativeButtonText)
        }.build()
}