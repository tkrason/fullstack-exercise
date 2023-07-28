package com.example.application.plugins

import com.example.service.user.UserService
import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.bearer

const val BEARER_SECURITY_AUTH = "bearer-security"

fun Application.configureSecurity(userService: UserService) {
    authentication {
        bearer(BEARER_SECURITY_AUTH) {
            authenticate {
                val foundUserId = userService.getUserIdFromTokenOrNull(it.token)
                if (foundUserId != null) UserIdPrincipal(foundUserId.toHexString()) else null
            }
        }
    }
}
