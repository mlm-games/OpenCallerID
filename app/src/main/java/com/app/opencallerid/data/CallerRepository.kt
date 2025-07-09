package com.app.opencallerid.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.get

@Singleton
class CallerRepository @Inject constructor(
    private val callerDao: CallerDao
) {
    private val supabase = SupabaseClient.client
    private val postgrest = supabase.postgrest

    suspend fun getCallerInfo(phoneNumber: String): CallerInfo? {
        // First check local database
        val localInfo = callerDao.getCallerInfo(phoneNumber)

        if (localInfo == null) {
            try {
                // Fetch from Supabase
                val response = postgrest["caller_info"]
                    .select(Columns.all()) {
                        filter {
                            eq("phone_number", phoneNumber)
                        }
                    }

                val remoteInfo = response.decodeList<CallerInfoDto>().firstOrNull()?.toCallerInfo()

                // Cache the result locally if found
                if (remoteInfo != null) {
                    callerDao.insertCallerInfo(remoteInfo)
                }

                return remoteInfo
            } catch (e: Exception) {
                // Handle network errors
                return null
            }
        }

        return localInfo
    }

    suspend fun reportCaller(phoneNumber: String, name: String, isSpam: Boolean, reportType: String) {
        val callerInfo = CallerInfo(
            phoneNumber = phoneNumber,
            name = name,
            isSpam = isSpam
        )

        // Save locally
        callerDao.insertCallerInfo(callerInfo)

        try {
            // Check if record exists
            val exists = postgrest["caller_info"]
                .select(Count.exact) {
                    filter {
                        eq("phone_number", phoneNumber)
                    }
                }.count > 0

            if (exists) {
                // Update existing record
                postgrest["caller_info"]
                    .update({
                        "name" to name
                        "is_spam" to isSpam
                        "report_count" to postgrest.raw("report_count + 1")
                        "last_updated" to System.currentTimeMillis()
                    }) {
                        filter {
                            eq("phone_number", phoneNumber)
                        }
                    }
            } else {
                // Insert new record
                postgrest["caller_info"]
                    .insert(CallerInfoDto(
                        phoneNumber = phoneNumber,
                        name = name,
                        isSpam = isSpam,
                        reportCount = 1,
                        lastUpdated = System.currentTimeMillis()
                    ))
            }

            // Add user report if user is authenticated
            supabase.gotrue.currentUserOrNull()?.let { user ->
                postgrest["user_reports"].insert(
                    UserReportDto(
                        userId = user.id,
                        phoneNumber = phoneNumber,
                        reportType = reportType
                    )
                )
            }

        } catch (e: Exception) {
            // Handle network errors
        }
    }

    suspend fun syncLocalDatabase() {
        try {
            // Get recently updated records
            val response = postgrest["caller_info"]
                .select(Columns.all()) {
                    order("last_updated", ascending = false)
                    limit(100)
                }

            val remoteInfos = response.decodeList<CallerInfoDto>().map { it.toCallerInfo() }

            // Update local database
            remoteInfos.forEach { callerInfo ->
                callerDao.insertCallerInfo(callerInfo)
            }
        } catch (e: Exception) {
            // Handle sync errors
        }
    }
}

// Data transfer objects for Supabase
data class CallerInfoDto(
    val phoneNumber: String,
    val name: String? = null,
    val isSpam: Boolean = false,
    val tags: List<String> = emptyList(),
    val reportCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toCallerInfo() = CallerInfo(
        phoneNumber = phoneNumber,
        name = name,
        isSpam = isSpam,
        tags = tags,
        reportCount = reportCount,
        lastUpdated = lastUpdated
    )
}

data class UserReportDto(
    val userId: String,
    val phoneNumber: String,
    val reportType: String,
    val comment: String? = null
)
