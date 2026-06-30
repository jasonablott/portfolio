package com.a6server.routing

import com.a6server.model.Photo
import com.a6server.repository.PhotosRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

fun Route.photoRoutes(photosRepository: PhotosRepository, uploadsDir: File = File("/uploads")) {
    if (!uploadsDir.exists()) uploadsDir.mkdirs()

        // GET /api/photos -> return only the authenticated user's photos
        get {
            val principal = call.principal<JWTPrincipal>()
            val username = principal?.getClaim("username", String::class)

            if (username == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid token"))
                return@get
            }

            val userPhotos = photosRepository.findByOwner(username)
            call.respond(userPhotos)
        }

        // POST /api/photos -> upload file, tied to authenticated user
        post {
            val principal = call.principal<JWTPrincipal>()
            println("POST /api/photos principal: $principal")

            val username = principal?.getClaim("username", String::class)

            if (username == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing or invalid token"))
                return@post
            }

            val multipart = call.receiveMultipart()
            var savedPhoto: Photo? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val originalName = part.originalFileName ?: "upload-${UUID.randomUUID()}"
                        val ext = originalName.substringAfterLast('.', "")
                        val generatedName = "${UUID.randomUUID()}${if (ext.isNotBlank()) ".$ext" else ""}"
                        val destFile = File(uploadsDir, generatedName)

                        withContext(Dispatchers.IO) {
                            part.streamProvider().use { its ->
                                destFile.outputStream().buffered().use { out ->
                                    its.copyTo(out)
                                }
                            }
                        }

                        val relativePath = "/uploads/${destFile.name}"
                        val photo = Photo(
                            filename = originalName,
                            owner = username,
                            path = relativePath
                        )
                        photosRepository.add(photo)
                        savedPhoto = photo
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (savedPhoto != null) {
                call.respond(HttpStatusCode.Created, savedPhoto!!)
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No file received"))
            }
        }
}
