package com.app.opencallerid.di

import android.content.Context
import androidx.room.Room
import com.app.opencallerid.data.AppDatabase
import com.app.opencallerid.data.CallerApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "opencaller_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCallerDao(database: AppDatabase) = database.callerDao()

    @Provides
    @Singleton
    fun provideCallerApiService(): CallerApiService {
        return Retrofit.Builder()
            .baseUrl("https://later-for-api-server.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CallerApiService::class.java)
    }
}