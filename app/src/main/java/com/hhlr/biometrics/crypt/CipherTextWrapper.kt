package com.hhlr.biometrics.crypt

data class CipherTextWrapper(val cipherText: ByteArray, val initializationVector: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CipherTextWrapper

        if (!cipherText.contentEquals(other.cipherText)) return false
        if (!initializationVector.contentEquals(other.initializationVector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cipherText.contentHashCode()
        result = 31 * result + initializationVector.contentHashCode()
        return result
    }
}