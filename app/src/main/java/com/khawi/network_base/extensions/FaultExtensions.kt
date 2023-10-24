package com.advance.network.extensions

import com.khawi.network_base.NetworkConstants
import com.khawi.network_base.model.LocalAdvanceError


fun LocalAdvanceError.connectivityFault() = codeResponse == NetworkConstants.kUnknownHost
