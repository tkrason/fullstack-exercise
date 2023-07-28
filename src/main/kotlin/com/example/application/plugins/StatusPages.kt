package com.example.application.plugins

import com.example.service.user.exception.LoginFailedException
import com.example.service.user.exception.UserEmailNotVerifiedException
import com.example.service.user.exception.UsernameAlreadyExistsException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessageDto(
    val error: String,
)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UsernameAlreadyExistsException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorMessageDto(cause.message.orEmpty()))
        }
        exception<LoginFailedException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorMessageDto(cause.message.orEmpty()))
        }
        exception<UserEmailNotVerifiedException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorMessageDto(cause.message.orEmpty()))
        }
    }
}
