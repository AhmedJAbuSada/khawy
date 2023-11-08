package com.khawi.network_base

object NetworkConstants {

    const val BASE_URL = "https://khawii-598d2a7a9203.herokuapp.com/api/"
//    const val BASE_URL = "https://muslim.tahqq.com/api/"
    // general
    const val kHTTPLocalFaultName = "HTTP"
    const val kUnknownHost = "kUnknownHost"

    const val kLocalFaultCodeUnhandledError = "kLocalFaultCodeUnhandledError"
    const val FAULT_PARSE_ERROR = "FAULT_PARSE_ERROR"

    // HTTP (some) status codes
    const val kHTTPStatusCodeBadRequest = 400
    const val kHTTPStatusCodeUnauthorized = 401
    const val kHTTPStatusCodeForbidden = 403
    const val kHTTPStatusCodeNotFound = 404
    const val kHTTPStatusCodeInternalServerError = 500
    const val kHTTPStatusCodeNotImplemented = 501
    const val kHTTPStatusCodeBadGateway = 502
    const val kHTTPStatusCodeServiceUnavailable = 503
    const val kHTTPStatusCodeGatewayTimeout = 504



}
