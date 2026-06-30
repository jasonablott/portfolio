package com.a6server

import com.a6server.plugins.configureSecurity
import com.a6server.plugins.configureSerialization
import com.a6server.repository.NotesRepository
import com.a6server.repository.UserRepository
import com.a6server.repository.PhotosRepository
import com.a6server.routing.configureRouting
import com.a6server.routing.mlRoutes
import com.a6server.service.JwtService
import com.a6server.service.UserService
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.net.http.HttpClient

fun main(args: Array<String>) {
  io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
  val userRepository = UserRepository()
  val userService = UserService(userRepository)
  val jwtService = JwtService(this, userService)

  val notesRepository = NotesRepository()
  val photosRepository = PhotosRepository() // added for A6

  val client = HttpClient() // Added for A7
  routing { // also added for A7
    mlRoutes(client)
  }

  configureSerialization()
  configureSecurity(jwtService)
  configureRouting(jwtService, userService, notesRepository, photosRepository) // added photosRepository for A6
}
