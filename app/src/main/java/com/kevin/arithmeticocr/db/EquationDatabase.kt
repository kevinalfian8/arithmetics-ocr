package com.kevin.arithmeticocr.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kevin.arithmeticocr.data.EquationResult

@Database(entities = [EquationResult::class], version = 1)
abstract class EquationDatabase: RoomDatabase() {
    abstract fun equationDao(): EquationDao
}