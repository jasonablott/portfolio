package com.a6server.repository

import com.a6server.model.User
import io.ktor.server.auth.*
import java.util.*
import javax.naming.Context
import javax.naming.directory.BasicAttribute
import javax.naming.directory.BasicAttributes
import javax.naming.directory.InitialDirContext
// NEW IMPORTS FOR CONTAINERIZATION------------------
import com.a6server.routing.request.LoginRequest
import com.a6server.routing.request.UserRequest
import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.auth.ldap.ldapAuthenticate
import javax.naming.ldap.LdapName
// END-------------------------------------------

// OLD VERSION PRE-CONTAINERIZATION------------------------------------------------------------------------------------

//class UserRepository {
//
//  private val users = mutableListOf<User>()
//
//  fun findAll(): List<User> =
//    users
//
//  fun findById(id: UUID): User? =
//    users.firstOrNull { it.id == id }
//
//  fun findByUsername(username: String): User? =
//    users.firstOrNull { it.username == username }
//
//  fun save(user: User): Boolean =
//    users.add(user)
//}

// END-----------------------------------------------------------------------------------------------------------------

// NEW VERSION FOR CONTAINERIZATION------------------------------------------------------------------------------------
class UserRepository {

  val ldapURL = "ldap://ldap.asd.msd.localhost:389"

  //private val users = mutableListOf<User>()

//    fun findAll(): List<User> =
//        users

//    fun findById(id: UUID): User? =
//        users.firstOrNull { it.id == id }

  fun findByUsername(username: String): User? {
    println("userRepo.findByUsername: $username")
    val dc = InitialDirContext(Hashtable<String, Any?>().apply {
      this[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
      this[Context.PROVIDER_URL] = ldapURL
      //The new openLDAP container doesn't allow anonymous searches
      //so we must log in as admin here, just to do a query
      this[Context.SECURITY_CREDENTIALS] = "shouldntMatter"
      this[Context.SECURITY_PRINCIPAL] = "cn=admin,dc=ldap,dc=asd,dc=msd,dc=localhost"
    })

    val answer = dc.search(
      "dc=ldap,dc=asd,dc=msd,dc=localhost",
      BasicAttributes(true).apply {
        put(BasicAttribute("cn", username))
      },
      arrayOf("cn")
    )

    println("answer: $answer")
    val answerList = answer.toList()

    println("LDAP answerList: $answerList")

    return if (answerList.size == 1)
      User(answerList.first().attributes["cn"].toString())
    else null

  }

  fun save(user: UserRequest): Boolean {
    //create a new user here
    try {
      //admin context
      //log in as admin
      val env = Hashtable<String?, Any?>()
      env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
      env[Context.PROVIDER_URL] = ldapURL
      env[Context.SECURITY_CREDENTIALS] = "shouldntMatter"
      env[Context.SECURITY_PRINCIPAL] = "cn=admin,dc=ldap,dc=asd,dc=msd,dc=localhost"

      val dirContext = InitialDirContext(env)

      //create info about the new user
      val cn = user.username
      //TODO: return error if username has special chars in it
      val username = LdapName("cn=$cn,dc=ldap,dc=asd,dc=msd,dc=localhost")
      val attributes = BasicAttributes(true).apply {
        put(BasicAttribute("cn", cn))
        put(BasicAttribute("sn", "lastNameIsUnusedButRequired"))
        put("userPassword", user.password)
        put(BasicAttribute("objectClass").apply {
          add("inetOrgPerson")
        })
      }


      //add that user info to LDAP
      val result = dirContext.createSubcontext(username, attributes)
      return true

    } catch (ex: Exception) {
      println("Create user failed: ${ex.message}")
      println("stack trace: ${ex.stackTraceToString()}")
      return false
    }
  }

  private fun nameToDN(name: String) = LdapName("cn=$name,dc=ldap,dc=asd,dc=msd,dc=localhost")

  fun ldapAuth(loginRequest: LoginRequest): UserIdPrincipal? {
    val pwdCred = UserPasswordCredential(loginRequest.username, loginRequest.password)
    return ldapAuthenticate(
      pwdCred,
      ldapURL,
      // possible injection attack here, should really sanitize the username
      nameToDN(loginRequest.username).toString()
    )
  }
}
// END-----------------------------------------------------------------------------------------------------------------