package com.a6server.routing

import com.a6server.routing.request.LoginRequest
import com.a6server.service.JwtService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoute(jwtService: JwtService) {

  post {
    val loginRequest = call.receive<LoginRequest>()

    val token: String? = jwtService.createJwtToken(loginRequest)

    token?.let {
      call.respond(hashMapOf("token" to token))
    } ?: call.respond(
      message = HttpStatusCode.Unauthorized
    )
  }

}