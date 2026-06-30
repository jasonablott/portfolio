package com.a6server.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.a6server.model.User
import com.a6server.routing.request.LoginRequest
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.*
import at.favre.lib.crypto.bcrypt.BCrypt
import kotlin.text.toCharArray
// ADDED FOR CONTAINERIZATION---------------
import io.ktor.server.application.Application
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.util.*
// END-------------------------------------

// OLD VERSION BEFORE CONTAINERIZATION---------------------------------------------------------------------------------

//class JwtService(
//  private val application: Application,
//  private val userService: UserService,
//) {
//
//  private val secret = getConfigProperty("jwt.secret")
//  private val issuer = getConfigProperty("jwt.issuer")
//  private val audience = getConfigProperty("jwt.audience")
//
//  val realm = getConfigProperty("jwt.realm")
//
//  val jwtVerifier: JWTVerifier =
//    JWT
//      .require(Algorithm.HMAC256(secret))
//      .withAudience(audience)
//      .withIssuer(issuer)
//      .build()
//
//  fun createJwtToken(loginRequest: LoginRequest): String? {
//    val foundUser: User? = userService.findByUsername(loginRequest.username)
//
//    return if (foundUser != null &&
//      BCrypt.verifyer().verify(loginRequest.password.toCharArray(),
//        foundUser.hashedPassword).verified)
//      JWT.create()
//        .withAudience(audience)
//        .withIssuer(issuer)
//        .withClaim("username", loginRequest.username)
//        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
//        .sign(Algorithm.HMAC256(secret))
//    else
//      null
//  }
//
//  fun customValidator(
//    credential: JWTCredential,
//  ): JWTPrincipal? {
//    val username: String? = extractUsername(credential)
//    val foundUser: User? = username?.let(userService::findByUsername)
//
//    return foundUser?.let {
//      if (audienceMatches(credential))
//        JWTPrincipal(credential.payload)
//      else
//        null
//    }
//  }
//
//  private fun audienceMatches(
//    credential: JWTCredential,
//  ): Boolean =
//    credential.payload.audience.contains(audience)
//
//  private fun getConfigProperty(path: String) =
//    application.environment.config.property(path).getString()
//
//  private fun extractUsername(credential: JWTCredential): String? =
//    credential.payload.getClaim("username").asString()
//}

// END ----------------------------------------------------------------------------------------------------------------

// NEW CONTAINERIZED VERSION-------------------------------------------------------------------------------------------
class JwtService(
  private val application: Application,
  private val userService: UserService,
) {

  private val secret = getConfigProperty("jwt.secret")
  private val issuer = getConfigProperty("jwt.issuer")
  private val audience = getConfigProperty("jwt.audience")

  val realm = getConfigProperty("jwt.realm")

  val jwtVerifier: JWTVerifier =
    JWT
      .require(Algorithm.HMAC256(secret))
      .withAudience(audience)
      .withIssuer(issuer)
      .build()

  fun createJwtToken(loginRequest: LoginRequest): String? {
    println("creating JWT for ${loginRequest.username}")
    val principal = userService.ldapAuth(loginRequest)

    return principal?.let {
      println("LDAP auth succeeded")
      JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("username", loginRequest.username)
        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
        .sign(Algorithm.HMAC256(secret))
    }
  }

  fun customValidator(
    credential: JWTCredential,
  ): JWTPrincipal? {
    val username: String? = extractUsername(credential)
    println("Checking jwt for $username")
    val foundUser: User? = username?.let(userService::findByUsername)

    return foundUser?.let {
      if (audienceMatches(credential))
        JWTPrincipal(credential.payload)
      else
        null
    }
  }

  private fun audienceMatches(
    credential: JWTCredential,
  ): Boolean =
    credential.payload.audience.contains(audience)

  private fun getConfigProperty(path: String) =
    application.environment.config.property(path).getString()

  private fun extractUsername(credential: JWTCredential): String? =
    credential.payload.getClaim("username").asString()
}
// END-----------------------------------------------------------------------------------------------------------------

