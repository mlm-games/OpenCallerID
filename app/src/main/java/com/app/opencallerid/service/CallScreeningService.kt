package com.app.opencallerid.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.app.opencallerid.data.CallerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CallerIdentificationService : CallScreeningService() {

    @Inject
    lateinit var callerRepository: CallerRepository

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle.schemeSpecificPart

        // Create response builder
        val responseBuilder = CallResponse.Builder()

        CoroutineScope(Dispatchers.IO).launch {
            val callerInfo = callerRepository.getCallerInfo(phoneNumber)

            if (callerInfo?.isSpam == true) {
                // Mark as spam if our database indicates it's spam
                responseBuilder.setDisallowCall(true)
                responseBuilder.setRejectCall(true)
                responseBuilder.setSkipCallLog(false)
                responseBuilder.setSkipNotification(false)
            } else {
                // Allow call but display caller info
                responseBuilder.setDisallowCall(false)
                responseBuilder.setRejectCall(false)
            }

            // Respond to the call
            respondToCall(callDetails, responseBuilder.build())
        }
    }
}