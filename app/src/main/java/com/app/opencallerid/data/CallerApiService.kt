package com.app.opencallerid.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CallerApiService {
    @GET("caller/{phoneNumber}")
    suspend fun getCallerInfo(@Path("phoneNumber") phoneNumber: String): CallerInfo

    @POST("caller/report")
    suspend fun reportCaller(@Body callerInfo: CallerInfo)
}
