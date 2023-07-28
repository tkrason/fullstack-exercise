package com.example.service.user

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.model.User
import com.example.model.UserStatus
import com.example.repository.UserRepository
import com.example.service.ModelService
import com.example.service.user.data.LoginRequest
import com.example.service.user.data.RegisterNewUserRequest
import com.example.service.user.exception.LoginFailedException
import com.example.service.user.exception.UserEmailNotVerifiedException
import com.example.service.user.exception.UsernameAlreadyExistsException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.koin.core.annotation.Singleton
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.hours

@Singleton
class UserService(private val userRepository: UserRepository) : ModelService<User>(userRepository) {

    private val userIssuedLoginTokenToUserId = ConcurrentHashMap<UUID, ObjectId>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val userServiceSchedulerDispatcher = Dispatchers.IO.limitedParallelism(1)
    private val userServiceSchedulerScope = CoroutineScope(userServiceSchedulerDispatcher)

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

    /**
     *  Logs in user for some time and returns token needed for all subsequent API calls
     */
    suspend fun loginUser(loginRequest: LoginRequest): UUID {
        val foundUser = userRepository.findUserByUserNameOrNull(userName = loginRequest.userName)
            ?: throw LoginFailedException()

        throwIfUserEmailNotVerified(foundUser)
        throwIfPasswordNotMatching(loginRequest, foundUser)

        return getLoginTokenAndScheduleItsRemoval(foundUser.id!!)
    }

    private fun throwIfPasswordNotMatching(loginRequest: LoginRequest, potentialUser: User) {
        val result = BCrypt.verifyer().verify(loginRequest.password.toCharArray(), potentialUser.bcryptHashedPassword)
        if (!result.verified) throw LoginFailedException()
    }

    private fun throwIfUserEmailNotVerified(potentialUser: User) =
        if (potentialUser.status != UserStatus.EMAIL_VERIFIED) throw UserEmailNotVerifiedException() else Unit

    private suspend fun getLoginTokenAndScheduleItsRemoval(userId: ObjectId): UUID {
        val loginToken = UUID.randomUUID()
        userIssuedLoginTokenToUserId[loginToken] = userId
        userServiceSchedulerScope.launch { removeTokenAfterDelay(loginToken) }
        return loginToken
    }

    private suspend fun removeTokenAfterDelay(loginToken: UUID) {
        delay(4.hours)
        userIssuedLoginTokenToUserId.remove(loginToken)
    }
}
