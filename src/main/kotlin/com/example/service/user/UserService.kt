package com.example.service.user

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.model.User
import com.example.model.UserStatus
import com.example.repository.UserRepository
import com.example.service.ModelService
import com.example.service.user.data.RegisterNewUserRequest
import com.example.service.user.exception.UsernameAlreadyExistsException
import org.koin.core.annotation.Singleton
import java.util.UUID

@Singleton
class UserService(private val userRepository: UserRepository) : ModelService<User>(userRepository) {

    /**
     *  Saves new user if username is unique and returns email verification token for this user.
     */
    suspend fun registerNewUser(request: RegisterNewUserRequest): String {
        if (isUsernameTaken(request.userName)) throw UsernameAlreadyExistsException(request.userName)

        val bCryptPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())
        val user = User(
            id = null,
            email = request.email,
            userName = request.userName,
            bcryptHashedPassword = bCryptPassword,
            status = UserStatus.EMAIL_NOT_VERIFIED,
            emailVerificationToken = UUID.randomUUID().toString(),
        )

        userRepository.insertOne(user)

        return user.emailVerificationToken
    }

    private suspend fun isUsernameTaken(userName: String): Boolean {
        return userRepository.countUserNameOccurrences(userName) != 0L
    }

    suspend fun verifyUser(token: String) {
        userRepository.verifyUserEmailWhereTokenOrNull(token)
    }
}
