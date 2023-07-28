package com.example.utils.extensions

import org.bson.types.ObjectId

class InvalidObjectIdException : RuntimeException()

fun String.toValidObjectIdOrThrow() = if (!ObjectId.isValid(this)) throw InvalidObjectIdException() else ObjectId(this)
