package com.kevin.arithmeticocr.repo

import android.content.Context
import com.kevin.arithmeticocr.data.EquationResult

interface MainRepository {

    suspend fun insertEquationDB(data: EquationResult): List<EquationResult>
    suspend fun getAllEquationDB(): List<EquationResult>

    suspend fun getAllEquationFile(): List<EquationResult>
    suspend fun insertEquationFile(data: EquationResult): List<EquationResult>

    fun getCurrentType(): String?
    fun setCurrentType(type: String)
}