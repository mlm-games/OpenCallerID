package com.app.opencallerid.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CallerDao {
    @Query("SELECT * FROM caller_info WHERE phoneNumber = :phoneNumber")
    suspend fun getCallerInfo(phoneNumber: String): CallerInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallerInfo(callerInfo: CallerInfo)

    @Query("SELECT * FROM caller_info WHERE isSpam = 1")
    fun getAllBlockedNumbers(): Flow<List<CallerInfo>>

    @Query("SELECT * FROM caller_info ORDER BY lastUpdated DESC LIMIT 50")
    fun getRecentCallerInfo(): Flow<List<CallerInfo>>
}