package com.kevin.arithmeticocr.di

import android.content.Context
import androidx.room.Room
import com.kevin.arithmeticocr.db.EquationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    fun provideDao(db: EquationDatabase) = db.equationDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, EquationDatabase::class.java, "equation_database")
        .build()

}