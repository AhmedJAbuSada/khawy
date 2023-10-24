package com.khawi.network_base.model

import kotlinx.serialization.Serializable

@Serializable
open class AdvanceError(
    val status: Boolean = false,
    open var codeResponse: String = "",
    open var message: String = "",
    val showDialog: Boolean = false,

    ) {

    override fun toString(): String {
        return "Fault(code= $codeResponse, message= $message)"
    }

    companion object {
        const val kLocalFaultName = "Local fault"
        const val kHTTPLocalFaultName = "HTTP"

        /**
         * These "keys" are used by Axon in its Legacy AxonFault.
         * See LocalizationHelper#getLegacyErrorStringResourceFor(String).
         */
        const val FAULT_INCOMPLETE_PARAMS = "FAULT_INCOMPLETE_PARAMS"
        const val FAULT_OBJECT_INSTANCE = "FAULT_OBJECT_INSTANCE"
        const val FAULT_NOT_LOGGED_IN = "FAULT_NOT_LOGGED_IN"
        const val FAULT_NO_PUBLIC_INFO = "FAULT_NO_PUBLIC_INFO"
        const val FAULT_EMPTY_RESPONSE = "FAULT_EMPTY_RESPONSE"
        const val FAULT_EMPTY_OBJECT = "FAULT_EMPTY_OBJECT"
        const val FAULT_PARSE_ERROR = "FAULT_PARSE_ERROR"
        const val FAULT_OBJECT_CREATION_ERROR = "FAULT_OBJECT_CREATION_ERROR"
        const val FAULT_NO_STUDY = "FAULT_NO_STUDY"
        const val FAULT_INVALID_STATE = "FAULT_INVALID_STATE"
        const val FAULT_UPLOAD_FAILED = "FAULT_UPLOAD_FAILED"
        const val FAULT_DOWNLOAD_FAILED = "FAULT_DOWNLOAD_FAILED"
        const val FAULT_PUBLIC_USER_REQUIRED = "FAULT_PUBLIC_USER_REQUIRED"
        const val FAULT_CONSENT_PDF_GENERATION = "FAULT_CONSENT_PDF_GENERATION"
        const val FAULT_CONSENT_WITHOUT_REVIEW_ID = "FAULT_CONSENT_WITHOUT_REVIEW_ID"
        const val FAULT_SEND_RESPONSE_FAIL = "FAULT_SEND_RESPONSE_FAIL"
        const val FAULT_FILE_SAVE_ERROR = "FAULT_FILE_SAVE_ERROR"
        const val FAULT_RESEARCH_AUTHENTICATION_ERROR = "kResearchAuthenticationError"
        const val FAULT_ILLEGAL_PARAMETERS_TO_LEAVE_STUDY = "ILLEGAL_PARAMETERS"
    }
}
