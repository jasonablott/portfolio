package com.a6server.repository

import com.a6server.model.Photo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.ConcurrentHashMap

// This class was added for A6 to allow photo storage/retrieval
class PhotosRepository(private val metadataDir: File = File("/data")) {

    private val photos = ConcurrentHashMap<String, Photo>()
    private val photosFile = File(metadataDir, "photos.json")

    init {
        if (!metadataDir.exists()) metadataDir.mkdirs()
        if (photosFile.exists()) {
            try {
                val jsonText = photosFile.readText()
                if (jsonText.isNotBlank()) {
                    val list: List<Photo> = Json.decodeFromString(jsonText)
                    list.forEach { photos[it.id] = it }
                }
            } catch (e: Exception) {
                println("Error reading photos.json: $e")
            }
        } else {
            photosFile.createNewFile()
        }
    }

    private fun persist() {
        try {
            val list = photos.values.toList()
            photosFile.writeText(Json.encodeToString(list))
        } catch (e: Exception) {
            println("Error writing photos.json: $e")
        }
    }

    fun add(photo: Photo): Photo {
        photos[photo.id] = photo
        persist()  // save on every add
        return photo
    }

    fun findById(id: String): Photo? = photos[id]

    fun findAll(): List<Photo> = photos.values.sortedByDescending { it.uploadedAt }

    fun findByOwner(owner: String): List<Photo> = photos.values.filter { it.owner == owner }
}
