package com.advance.network.extensions

import com.khawi.network_base.NetworkConstants
import com.khawi.network_base.model.LocalAdvanceError
import io.ktor.util.network.UnresolvedAddressException
import timber.log.Timber
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

fun Throwable.isNetworkException(): Boolean {
    return this is UnknownHostException || this is UnresolvedAddressException ||
        this is SSLHandshakeException || this is ConnectException || this is SocketTimeoutException
}

fun Throwable.toFault(): LocalAdvanceError {
//    Timber.e("Creating new fault: $message", this)

    return if (isNetworkException()) {
        LocalAdvanceError( NetworkConstants.kUnknownHost, message  ?: "")
    } else if (this is EOFException) {
        LocalAdvanceError(NetworkConstants.FAULT_PARSE_ERROR, message ?: "")
    } else {
        LocalAdvanceError(
            NetworkConstants.kLocalFaultCodeUnhandledError,
            message  ?: ""
        )
    }
}
