package com.vickikbt.darajakmp.network

import com.vickikbt.darajakmp.utils.DarajaConstants
import com.vickikbt.darajakmp.utils.DarajaEnvironment
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class DarajaHttpClientFactory constructor(private val environment: DarajaEnvironment) {

    private val BASE_URL = if (environment == DarajaEnvironment.SANDBOX_ENVIRONMENT) {
        DarajaConstants.SANDBOX_BASE_URL
    } else {
        DarajaConstants.PROD_BASE_URL
    }


    /*Initialize Http Client responsible for handling network operations*/
    internal fun createDarajaHttpClient() = HttpClient(engineFactory = CIO) {
        defaultRequest {
            contentType(ContentType.Application.Json)

            url {
                host = BASE_URL
                url { protocol = URLProtocol.HTTPS }
            }
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }

        if (environment == DarajaEnvironment.SANDBOX_ENVIRONMENT) {
            install(Logging) {
                level = LogLevel.BODY
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.i(tag = "Http Client", message = message)
                    }
                }
            }.also {
                Napier.base(DebugAntilog())
            }
        }
    }
}
