package com.app.opencallerid.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caller_info")
data class CallerInfo(
    @PrimaryKey
    val phoneNumber: String,
    val name: String? = null,
    val isSpam: Boolean = false,
    val tags: List<String> = emptyList(),
    val reportCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
