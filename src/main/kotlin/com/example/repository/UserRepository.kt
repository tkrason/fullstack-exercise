package com.example.repository

import com.example.application.Mongo
import com.example.model.User
import com.example.model.UserStatus
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Singleton

@Singleton
class UserRepository(
    mongo: Mongo,
) : MongoCrudRepository<User>(
    mongo = mongo,
    databaseName = "example",
) {
    override fun MongoDatabase.selectRepositoryCollection() = getCollection<User>("users")

    suspend fun countUserNameOccurrences(userName: String) = withCollection {
        count(filter = {
            Filters.eq(User::userName.name, userName)
        })
    }

    suspend fun verifyUserEmailWhereTokenOrNull(token: String) = withCollection {
        findOneAndUpdate(
            filter = Filters.eq(User::emailVerificationToken.name, token),
            update = Updates.set(User::status.name, UserStatus.EMAIL_VERIFIED),
        )
    }

    suspend fun findUserByUserNameOrNull(userName: String) = withCollection {
        find(filter = Filters.eq(User::userName.name, userName)).firstOrNull()
    }
}
