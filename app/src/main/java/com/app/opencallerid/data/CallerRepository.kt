package com.app.opencallerid.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallerRepository @Inject constructor(
    private val callerDao: CallerDao,
    private val callerApiService: CallerApiService
) {
    suspend fun getCallerInfo(phoneNumber: String): CallerInfo? {
        // First check local database
        val localInfo = callerDao.getCallerInfo(phoneNumber)

        // If not found locally, try to fetch from API
        if (localInfo == null) {
            try {
                val remoteInfo = callerApiService.getCallerInfo(phoneNumber)
                // Cache the result
                callerDao.insertCallerInfo(remoteInfo)
                return remoteInfo
            } catch (e: Exception) {
                // Handle network errors
                return null
            }
        }

        return localInfo
    }

    suspend fun reportCaller(phoneNumber: String, name: String, isSpam: Boolean) {
        val callerInfo = CallerInfo(
            phoneNumber = phoneNumber,
            name = name,
            isSpam = isSpam
        )

        // Save locally
        callerDao.insertCallerInfo(callerInfo)

        // Report to server
        try {
            callerApiService.reportCaller(callerInfo)
        } catch (e: Exception) {
            // Handle network errors
        }
    }

    fun getAllBlockedNumbers(): Flow<List<CallerInfo>> {
        return callerDao.getAllBlockedNumbers()
    }
}