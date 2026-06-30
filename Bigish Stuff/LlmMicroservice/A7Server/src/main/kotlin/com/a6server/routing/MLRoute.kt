package com.a6server.routing

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun Route.mlRoutes(client: HttpClient) {

    // POST route forwards image to llm for analysis, and parses result for caption
    post {
        // get image from client
        val multipartData = call.receiveMultipart()
        var imageBytes: ByteArray? = null

        multipartData.forEachPart { part ->
            if (part is PartData.FileItem) {
                imageBytes = part.streamProvider().readBytes()
            }
            part.dispose()
        }

        // handle error
        if (imageBytes == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No image provided"))
            return@post
        }

        try {
            // forward to ML model server which is in a separate docker container
            val rawResponse: String = client.post("http://mlserver:8000/analyze") {
                setBody(MultiPartFormDataContent(formData {
                    append("image", imageBytes!!, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"; filename=\"image.jpg\"")
                    })
                }))
            }.bodyAsText() // get the raw text first

            // attempt to parse JSON
            val json = Json.parseToJsonElement(rawResponse).jsonObject
            val caption = json["caption"]?.jsonPrimitive?.content

            if (caption != null) {
                call.respond(mapOf("caption" to caption))
            } else {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "No caption returned by ML server"))
            }

        } catch (e: Exception) {
            call.application.environment.log.error("ML server request failed", e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "ML server error: ${e.localizedMessage}"))
        }
    }
}
