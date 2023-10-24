package com.khawi.network_base.di

import android.util.Log
import com.khawi.network_base.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.timeout
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun providesHttpClient(): HttpClient {
        val httpClient = HttpClient(Android) {
            useDefaultTransformers = false
            expectSuccess = true
            engine {
                val timeout = 120 * 1000
                connectTimeout = timeout
                socketTimeout = timeout
            }
//            followRedirects = true
//            install(HttpTimeout)
            install(HttpRedirect) {
                checkHttpMethod = true
            }

            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }

            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        encodeDefaults = false
                        coerceInputValues = true
                        isLenient = true
                        serializersModule = SerializersModule {
                        }
                    }
                )
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Logger Ktor =>", message)
                    }
                }
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                headers.remove(HttpHeaders.AcceptCharset)
            }
        }
        return httpClient
    }

}