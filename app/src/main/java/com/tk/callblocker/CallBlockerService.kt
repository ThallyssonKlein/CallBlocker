package com.tk.callblocker

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.CallScreeningService.CallResponse

/**
 * Registered as the system's call-screening service (granted via RoleManager.ROLE_CALL_SCREENING).
 * Called by the OS for every incoming call before it rings.
 */
class CallBlockerService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        if (!Prefs.isBlockingEnabled(this)) {
            allowCall(callDetails)
            return
        }

        val number = callDetails.handle?.schemeSpecificPart
        // Without a number to check, or without contacts permission, don't block.
        if (number.isNullOrBlank() || ContactsLookup.isNumberInContacts(this, number)) {
            allowCall(callDetails)
        } else {
            blockCall(callDetails)
        }
    }

    private fun allowCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()
        respondToCall(callDetails, response)
    }

    private fun blockCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(false)
            .setSkipNotification(true)
            .build()
        respondToCall(callDetails, response)
    }
}
