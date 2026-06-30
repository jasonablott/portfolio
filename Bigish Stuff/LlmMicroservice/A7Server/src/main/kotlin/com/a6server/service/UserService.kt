package com.a6server.service

import com.a6server.model.User
import com.a6server.repository.UserRepository
import java.util.*
// ADDED FOR CONTAINERIZATION---------------------
import com.a6server.routing.request.LoginRequest
import com.a6server.routing.request.UserRequest
import io.ktor.server.auth.Principal
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.auth.ldap.ldapAuthenticate
import javax.naming.ldap.LdapName
// END--------------------------------------------

// OLD VERSION BEFORE CONTAINERIZATION---------------------------------------------------------------------------------

//class UserService(
//  private val userRepository: UserRepository
//) {
//
//  fun findAll(): List<User> =
//    userRepository.findAll()
//
//  fun findById(id: String): User? =
//    userRepository.findById(
//      id = UUID.fromString(id)
//    )
//
//  fun findByUsername(username: String): User? =
//    userRepository.findByUsername(username)
//
//  fun save(user: User): User? {
//    val foundUser = userRepository.findByUsername(user.username)
//
//    return if (foundUser == null) {
//      userRepository.save(user)
//      user
//    } else null
//  }
//}

// END-----------------------------------------------------------------------------------------------------------------

// NEW VERSION FOR CONTAINERIZATION------------------------------------------------------------------------------------

class UserService(
  private val userRepository: UserRepository
) {
  fun findByUsername(username: String): User? =
    userRepository.findByUsername(username)

  fun save(user: UserRequest): User? {
    return if(userRepository.save(user)) User(user.username) else null
  }
  fun ldapAuth(loginRequest: LoginRequest) = userRepository.ldapAuth(loginRequest)
}

// END-----------------------------------------------------------------------------------------------------------------