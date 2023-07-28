package com.example.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId override val id: ObjectId?,
    val email: String,
    val userName: String,
    val bcryptHashedPassword: String,
    val status: UserStatus,
    val emailVerificationToken: String,
) : Model

enum class UserStatus {
    EMAIL_NOT_VERIFIED,
    EMAIL_VERIFIED,
    BLOCKED,
}
