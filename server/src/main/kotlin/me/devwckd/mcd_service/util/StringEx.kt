package me.devwckd.mcd_service.util

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun generateRandomString(length: Int = 6) = (1..length)
    .map { kotlin.random.Random.nextInt(0, charPool.size) }
    .map(charPool::get)
    .joinToString("");

fun generateServerId(): String = "server-${generateRandomString()}"

fun generateRoomId(): String = "room-${generateRandomString()}"

fun generateProxyId(): String = "proxy-${generateRandomString()}"
