package com.example.application

import com.example.application.plugins.configureCORS
import com.example.application.plugins.configureHTTP
import com.example.application.plugins.configureMonitoring
import com.example.application.plugins.configureSecurity
import com.example.application.plugins.configureSerialization
import com.example.application.plugins.configureStatusPages
import com.example.application.plugins.registerSwagger
import com.example.service.user.UserService
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.annotation.Singleton

@Singleton
class Server(
    private val router: Router,
    private val userService: UserService,
) {

    @Suppress("ExtractKtorModule")
    fun startServer() = embeddedServer(Netty, port = 8080) {
        configure(userService)
        router.routeAll(application = this)
    }.start(wait = true)
}

private fun Application.configure(userService: UserService) {
    configureSecurity(userService)
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureCORS()
    configureStatusPages()
    registerSwagger()
}
