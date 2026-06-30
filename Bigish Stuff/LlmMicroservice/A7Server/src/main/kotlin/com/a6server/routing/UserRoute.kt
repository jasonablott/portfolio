package com.a6server.routing

import com.a6server.model.User
import com.a6server.routing.request.UserRequest
import com.a6server.routing.response.UserResponse
import com.a6server.service.UserService
import com.a6server.util.hashPassword
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


// OLD VERSION BEFORE CONTAINERIZATION--------------------------------------------------------------------------------

//fun Route.userRoute(userService: UserService) {
//
//  // create a new user
//  post {
//    val userRequest = call.receive<UserRequest>()
//    println("user request: ${userRequest}")
//    val createdUser = userService.save(
//      user = userRequest.toModel()
//    ) ?: return@post call.respond(HttpStatusCode.BadRequest)
//
//    call.response.header(
//      name = "id",
//      value = createdUser.id.toString()
//    )
//    call.respond(
//      message = HttpStatusCode.Created
//    )
//  }
//
//  authenticate("default") {
//    get {
//      val users = userService.findAll()
//
//      call.respond(
//        message = users.map(User::toResponse)
//      )
//    }
//  }
//
//  authenticate("default") {
//    get("/{id}") {
//      val id: String = call.parameters["id"]
//        ?: return@get call.respond(HttpStatusCode.BadRequest)
//
//      val foundUser = userService.findById(id)
//        ?: return@get call.respond(HttpStatusCode.NotFound)
//
//      if (foundUser.username != extractPrincipalUsername(call))
//        return@get call.respond(HttpStatusCode.NotFound)
//
//      call.respond(
//        message = foundUser.toResponse()
//      )
//    }
//  }
//}
//
//private fun UserRequest.toModel(): User =
//  User(
//    id = UUID.randomUUID(),
//    username = this.username,
//    hashedPassword = hashPassword(this.password)
//  )
//
//private fun User.toResponse(): UserResponse =
//  UserResponse(
//    id = this.id,
//    username = this.username,
//  )

// END----------------------------------------------------------------------------------------------------------------

// NEW FOR CONTAINERIZATION-------------------------------------------------------------------------------------------

fun Route.userRoute(userService: UserService) {

  // create a new user
  post {
    val userRequest = call.receive<UserRequest>()
    println("user request: ${userRequest}")
    val createdUser = userService.save(
      user = userRequest
    ) ?: return@post call.respond(HttpStatusCode.BadRequest)

    call.response.header(
      name = "username",
      value = createdUser.username.toString()
    )
    call.respond(
      message = HttpStatusCode.Created
    )
  }
}

private fun UserRequest.toModel(): User =
  User(
    //id = UUID.randomUUID(),
    username = this.username,
    //hashedPassword = hashPassword(this.password)
  )

private fun User.toResponse(): UserResponse =
  UserResponse(
    //id = this.id,
    username = this.username
  )

// END-----------------------------------------------------------------------------------------------------------------

