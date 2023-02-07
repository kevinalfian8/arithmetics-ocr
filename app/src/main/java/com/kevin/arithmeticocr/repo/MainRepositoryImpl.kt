package com.kevin.arithmeticocr.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.kevin.arithmeticocr.data.EquationResult
import com.kevin.arithmeticocr.db.EquationDao
import com.kevin.arithmeticocr.util.FILE
import com.kevin.arithmeticocr.util.TYPE
import com.kevin.arithmeticocr.util.getCipher
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val dao: EquationDao,
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : MainRepository {

    override suspend fun insertEquationDB(data: EquationResult): List<EquationResult> {
        dao.insertEquation(data)
        val result = getAllEquationDB()
        return result
    }

    override suspend fun getAllEquationDB(): List<EquationResult> {
        val result = dao.getAllEquationResult()
        return result
    }

    override suspend fun getAllEquationFile(): List<EquationResult> {
        try {
            val encrypted: ByteArray
            context.openFileInput("result.txt").use {
                encrypted = it.readBytes()
            }

            if (encrypted.isEmpty()) {
                return mutableListOf()
            }

            val decrypt = getCipher(Cipher.DECRYPT_MODE).doFinal(encrypted)
            val json = String(decrypt)

            val content = Gson().fromJson(
                json,
                Array<EquationResult>::class.java
            )
            return content.toList()
        } catch (e: Exception) {
            return mutableListOf()
        }
    }

    override suspend fun insertEquationFile(data: EquationResult): List<EquationResult> {
        try {
            val content = getAllEquationFile().toMutableList()
            content.add(data)

            context.openFileOutput("result.txt", Context.MODE_PRIVATE).use {
                val json = Gson().toJson(content)
                val encryptedJson = getCipher(Cipher.ENCRYPT_MODE).doFinal(json.toByteArray())

                it.write(encryptedJson)
                it.close()
            }

            return content
        } catch (e: Exception) {
            return mutableListOf()
        }
    }

    override fun getCurrentType(): String? = sharedPreferences.getString(TYPE, FILE)

    override fun setCurrentType(type: String) {
        sharedPreferences.edit().putString(TYPE, type).apply()
    }

}