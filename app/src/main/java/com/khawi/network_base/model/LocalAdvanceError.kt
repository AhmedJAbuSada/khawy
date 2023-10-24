package com.khawi.network_base.model

import com.khawi.network_base.NetworkConstants

class LocalAdvanceError internal constructor(
    override var codeResponse: String,
    message: String,
) : AdvanceError(codeResponse = codeResponse, message = message) {


    companion object {
        fun createLocalFault(code: String, message: String? = null) =
            LocalAdvanceError(code, message ?: "")


        fun httpFault(httpStatusCode: Int): LocalAdvanceError {
            val message: String = when (httpStatusCode) {
                NetworkConstants.kHTTPStatusCodeBadRequest -> "BadRequest"
                NetworkConstants.kHTTPStatusCodeUnauthorized -> "Unauthorized"
                NetworkConstants.kHTTPStatusCodeForbidden -> "Forbidden"
                NetworkConstants.kHTTPStatusCodeNotFound -> "NotFound"
                NetworkConstants.kHTTPStatusCodeInternalServerError -> "InternalServerError"
                NetworkConstants.kHTTPStatusCodeNotImplemented -> "NotImplemented"
                NetworkConstants.kHTTPStatusCodeBadGateway -> "BadGateway"
                NetworkConstants.kHTTPStatusCodeServiceUnavailable -> "ServiceUnavailable"
                NetworkConstants.kHTTPStatusCodeGatewayTimeout -> "GatewayTimeout"
                else -> "HTTP error"
            }

            return LocalAdvanceError(
                httpStatusCode.toString(),
                message
            )
        }
    }
}
