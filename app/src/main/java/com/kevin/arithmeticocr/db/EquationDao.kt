package com.kevin.arithmeticocr.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevin.arithmeticocr.data.EquationResult

@Dao
interface EquationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEquation(equationResult: EquationResult): Long

    @Query("SELECT * FROM equation_table ORDER BY id")
    suspend fun getAllEquationResult(): List<EquationResult>

}