package com.a6server.repository

import com.a6server.model.Note
import kotlin.collections.HashMap

class NotesRepository {
    private val repo = HashMap<String, MutableList<Note>>()

    fun publicNotesFor(username: String) =
        repo.getOrDefault(username, listOf()).filter{ it.public}

    fun allNotesFor(user: String) = repo.getOrDefault(user, listOf())

    fun postNote(username: String, note: Note){
        repo.getOrPut(username) { mutableListOf() }.add(note)
    }
}