package com.khawi.network_base

import com.advance.network.extensions.toFault
import com.khawi.MainApplication
import com.khawi.base.english_key
import com.khawi.base.getPreference
import com.khawi.base.language_key
import com.khawi.network_base.NetworkConstants.BASE_URL
import com.khawi.network_base.model.AdvanceError
import com.khawi.network_base.model.AdvanceResult
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.prepareFormWithBinaryData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.PartData
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteDataSource @Inject constructor(
    val http: HttpClient,
) {

    //    private val timeout: Long = 120L
    suspend inline fun <reified T> get(
        urlPath: String,
        params: Map<String, Any>? = null,
        headers: Map<String, String>? = null,
    ): AdvanceResult<T> {
        return execute(
            Request(
                url = "$BASE_URL$urlPath",
                method = HttpMethod.Get,
                headers = headers,
                params = params,
            )
        )
    }

    suspend inline fun <reified T> post(
        urlPath: String,
        headers: Map<String, String>? = null,
        params: Map<String, Any>? = null,
        body: Any? = null,
    ): AdvanceResult<T> {
        return execute(
            Request(
                url = "$BASE_URL$urlPath",
                method = HttpMethod.Post,
                headers = headers,
                params = params,
                body = body
            )
        )
    }

    suspend inline fun <reified T> post(
        urlPath: String,
        headers: Map<String, String>? = null,
        files: List<PartData>? = null,
        body: Any? = null
    ): AdvanceResult<T> {
        return execute(
            Request(
                url = "$BASE_URL$urlPath",
                method = HttpMethod.Post,
                headers = headers,
                params = null,
                files = files,
                body = body
            )
        )
    }

    @Suppress("NestedBlockDepth")
    suspend inline fun <reified T> execute(request: Request): AdvanceResult<T> {
        return try {

//            val response: HttpResponse = if (request.files == null) {
//                http.request(request.url) {
//                    method = request.method
//                    setupRequest(request)
//                }
//            } else {
//                http.submitFormWithBinaryData(request.url, request.files) {
//                    setupRequest(request)
//                }
//            }
            val execute =
                if (request.files == null) {
                    http.prepareRequest(request.url) {
                        method = request.method
                        contentType(ContentType.Application.Json)
                        setupRequest(request)
                    }
                } else {
                    http.prepareFormWithBinaryData(request.url, request.files) {
                        contentType(ContentType.MultiPart.FormData)
                        setupRequest(request)
                    }
                }
            val response = execute.execute()
            if (response.status.isSuccess()) {
                try {
                    AdvanceResult.Success(response.body())
                } catch (de: Exception) {
                    de.printStackTrace()
                    Timber.e("Error while serializationException: ${de.message}")
                    AdvanceResult.Error(response.body<AdvanceError>())
                }
            } else {
                AdvanceResult.Error(response.body())
            }
        } catch (cre: ClientRequestException) {
            Timber.e("RequestException: Error while building result object:", cre)

            AdvanceResult.Error(cre.response.body())
        } catch (re: ResponseException) {
            Timber.e("ResponseException: Error while building result object:", re)

            AdvanceResult.Error(re.toFault())
        } catch (e: Exception) {
            Timber.e("Error while executing request:", e)

            e.printStackTrace()

            AdvanceResult.Error(e.toFault())
        }
    }

    fun HttpRequestBuilder.setupRequest(request: Request) {
//        val language = MainApplication.applicationContext().getPreference(language_key)
//        if (language.isNotEmpty()) {
//            header(HttpHeaders.AcceptLanguage, language)
//        }
//        header(HttpHeaders.ContentType, ContentType.Application.Json)
//        header(HttpHeaders.Accept, ContentType.Application.Json)
//        append(HttpHeaders.XHttpMethodOverride, "POST")

        headers {
            var language = MainApplication.applicationContext().getPreference(language_key)
            if (language.isEmpty())
                language = english_key
            append(HttpHeaders.AcceptLanguage, language)

            if (request.headers != null)
                request.headers.keys.forEach {
                    append(it, request.headers[it] ?: "")
                }

        }
        headers.remove(HttpHeaders.AcceptCharset)
        if (request.params != null) {
            request.params.forEach {
                if (it.value != null)
                    parameter(it.key, it.value ?: "")
            }
        }
//        timeout {
//            connectTimeoutMillis = timeout * TIMEOUT_MILLIS
//            requestTimeoutMillis = timeout * TIMEOUT_MILLIS
//            socketTimeoutMillis = timeout * TIMEOUT_MILLIS
//        }
        if (request.body != null) {
            setBody(body = request.body)
        }
    }

    companion object {
//        const val TIMEOUT_MILLIS = 1000
//        const val kUnknownHost = "kUnknownHost"
//        const val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
//        const val HEADER_USER_AGENT = "User-Agent"
    }
}

class Request(
    val url: String,
    val method: HttpMethod,
    val headers: Map<String, String>? = null,
    val params: Map<String, Any?>? = null,
    val files: List<PartData>? = null,
    val body: Any? = null
)
