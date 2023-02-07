package com.kevin.arithmeticocr.di

import android.content.Context
import android.content.SharedPreferences
import com.kevin.arithmeticocr.db.EquationDao
import com.kevin.arithmeticocr.repo.MainRepository
import com.kevin.arithmeticocr.repo.MainRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepoModule {

    @Provides
    fun provideRepoModule(dao: EquationDao, @ApplicationContext context: Context, sharedPreferences: SharedPreferences): MainRepository = MainRepositoryImpl(dao, context, sharedPreferences)

}