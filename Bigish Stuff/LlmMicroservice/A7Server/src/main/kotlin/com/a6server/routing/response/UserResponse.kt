package com.a6server.routing.response

import com.a6server.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserResponse(
  // @Serializable(with = UUIDSerializer::class) // REMOVED FOR CONTAINERIZATION
  // val id: UUID, // REMOVED FOR CONTAINERIZATION
  val username: String,
)