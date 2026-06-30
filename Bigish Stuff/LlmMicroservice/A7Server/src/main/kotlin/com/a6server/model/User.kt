package com.a6server.model

import java.util.UUID

data class User(
  // val id: UUID, // REMOVED FOR CONTAINERIZATION-----------------------
  val username: String,
  // val hashedPassword: String //bcrypt so it includes salt, etc REMOVED FOR CONTAINERIZATION-----------------
)