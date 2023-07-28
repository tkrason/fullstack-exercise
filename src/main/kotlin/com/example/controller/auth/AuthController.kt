package com.example.controller.auth

import com.example.application.config.Config
import com.example.application.plugins.ErrorMessageDto
import com.example.controller.Controller
import com.example.controller.auth.dto.LoginRequestDto
import com.example.controller.auth.dto.LoginResponseDto
import com.example.controller.auth.dto.RegisterNewUserRequestDto
import com.example.controller.auth.dto.RegisterNewUserResponseDto
import com.example.controller.auth.dto.toInternalModel
import com.example.service.user.UserService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.util.getOrFail
import org.koin.core.annotation.Singleton

@Singleton(binds = [Controller::class])
class AuthController(
    private val config: Config,
    private val userService: UserService,
) : Controller(
    basePath = "api/v1",
    useBearerAuth = false,
) {

    private val apiTags = listOf("Auth")

    override fun Route.routesForRegistrationOnBasePath() {
        registerNewUser()
        verifyUser()
        getLoginToken()
    }

    private fun Route.registerNewUser() = post("/register", {
        tags = apiTags
        description = "Requests a new user registration. Returns an email verification link."
        request { body<RegisterNewUserRequestDto>() }
        response {
            HttpStatusCode.OK to { body<RegisterNewUserResponseDto>() }
            HttpStatusCode.BadRequest to { body<ErrorMessageDto>() }
        }
    }) {
        val newUserRegistrationRequest = call.receive<RegisterNewUserRequestDto>().toInternalModel()
        val verificationToken = userService.registerNewUser(newUserRegistrationRequest)

        call.respond(
            RegisterNewUserResponseDto(
                verifyEmailLink = "localhost:${config.port}/${super.basePath}/verify?token=$verificationToken",
            ),
        )
    }

    private fun Route.verifyUser() = get("/verify", {
        tags = apiTags
        description = "Verify user with the provided token."
        request { queryParameter<String>("token") { description = "User verification token" } }
        response {
            HttpStatusCode.NoContent to { }
        }
    }) {
        val token = call.parameters.getOrFail("token")
        userService.verifyUser(token)
        call.respond(HttpStatusCode.OK)
    }

    private fun Route.getLoginToken() = post("/login", {
        tags = apiTags
        description = "Get login token for username / password combination"
        request { body<LoginRequestDto>() }
    }) {
        val loginRequest = call.receive<LoginRequestDto>().toInternalModel()

        val loginToken = userService.loginUser(loginRequest)
        call.respond(LoginResponseDto(token = loginToken.toString()))
    }
}
