package com.example.service.user.exception

class UsernameAlreadyExistsException(userName: String) : RuntimeException(
    "Username $userName already exists!",
)
