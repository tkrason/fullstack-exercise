package com.example.controller.auth.dto

import com.example.service.user.data.LoginRequest
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val userName: String,
    val password: String,
)

fun LoginRequestDto.toInternalModel() = LoginRequest(userName = userName, password = password)

@Serializable
data class LoginResponseDto(
    val token: String,
)
