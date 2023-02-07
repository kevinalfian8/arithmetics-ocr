package com.kevin.arithmeticocr.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equation_table")
data class EquationResult(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "equation")
    val equation: String = "",
    @ColumnInfo(name = "result")
    val result: String = ""
)
