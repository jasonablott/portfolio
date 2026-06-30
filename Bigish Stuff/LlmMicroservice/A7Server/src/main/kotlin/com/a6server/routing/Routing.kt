package com.a6server.routing

import com.a6server.repository.NotesRepository
import com.a6server.repository.PhotosRepository
import com.a6server.service.JwtService
import com.a6server.service.UserService
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting(
  jwtService: JwtService,
  userService: UserService,
  notesRepository: NotesRepository,
  photosRepository: PhotosRepository // added for a6 to use photos repository
) {
  routing {

    route("/api/auth") {
      authRoute(jwtService)
    }

    route("/api/user") {
      userRoute(userService)
    }

    authenticate("default") {

      route("/api") {

        route("/notes") {
          noteRoute(notesRepository)
        }

        route("/upload") {
          uploadRoute()
        }

        route("/photos") {
          photoRoutes(photosRepository)
        }

        route ("/analyze"){ // added this route in auth block for A7
          mlRoutes(client= HttpClient())
        }
      }
    }
    static("/uploads"){
      files("/uploads")
    }
  }
}

  fun extractPrincipalUsername(call: ApplicationCall): String? =
    call.principal<JWTPrincipal>()
      ?.payload
      ?.getClaim("username")
      ?.asString()


