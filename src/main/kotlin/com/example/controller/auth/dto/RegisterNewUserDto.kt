package com.example.controller.auth.dto

import com.example.service.user.data.RegisterNewUserRequest
import kotlinx.serialization.Serializable

@Serializable
data class RegisterNewUserRequestDto(
    val userName: String,
    val email: String,
    val password: String,
)

fun RegisterNewUserRequestDto.toInternalModel() = RegisterNewUserRequest(
    userName = userName,
    email = email,
    password = password,
)

@Serializable
data class RegisterNewUserResponseDto(
    val verifyEmailLink: String,
)
