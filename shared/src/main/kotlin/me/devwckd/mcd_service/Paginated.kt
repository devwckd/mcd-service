package me.devwckd.mcd_service

import kotlinx.serialization.Serializable

@Serializable
data class Paginated<T>(
    val currentPage: Int,
    val maxPages: Int,
    val itemsPerPage: Int,
    val items: Collection<T>
)