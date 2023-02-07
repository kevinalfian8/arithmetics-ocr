package com.kevin.arithmeticocr.util

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

const val TYPE = "type"
const val FILE = "file"
const val DB = "db"

fun getCipher(mode: Int): Cipher {
    val SECRET_KEY = "IniRahasiaBanget"
    val SECRET_IV = "IniJugaRahasiaLo"

    val iv = IvParameterSpec(SECRET_IV.toByteArray())
    val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(mode, keySpec, iv)

    return cipher
}