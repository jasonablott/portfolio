package com.a6server.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

// This data class has been added to the sample code for A6
// This class represents the data for a photo
@Serializable
data class Photo (
    val id: String = UUID.randomUUID().toString(),
    val filename: String,
    val owner: String? = null,
    val path: String,
    val uploadedAt: Long = Instant.now().toEpochMilli()
)