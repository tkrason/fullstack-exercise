package com.example.controller.article

import com.example.controller.Controller
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.core.annotation.Singleton

@Singleton(binds = [Controller::class])
class ArticleController : Controller(
    basePath = "api/v1",
    useBearerAuth = true,
) {
    override fun Route.routesForRegistrationOnBasePath() {
        testLogin()
    }

    private fun Route.testLogin() = get("/test-login") {
        call.respond(HttpStatusCode.OK)
    }
}
