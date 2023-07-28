package com.example.service.user.exception

class UsernameAlreadyExistsException(userName: String) : RuntimeException(
    "Username $userName already exists!",
)

class LoginFailedException : RuntimeException("Login did not succeed!")

class UserEmailNotVerifiedException : RuntimeException("User email not yet verified!")
