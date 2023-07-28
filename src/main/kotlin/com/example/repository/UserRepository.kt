package com.example.repository

import com.example.application.Mongo
import com.example.model.User
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
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
}
