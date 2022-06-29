package me.devwckd.mcd_service.bungee.util

private val baseUrl = System.getenv("MCD_BASE_URL") as String

fun mcdUrl(path: String) = "${baseUrl.removeSuffix("/")}/$path"