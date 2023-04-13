package com.example.noteit.repository

import com.example.noteit.db.NoteDatabase
import com.example.noteit.model.Note

class NoteRepository(private val db: NoteDatabase) {

    fun getNote()= db.getNoteDao().getALLNote();

    fun searchNote(query: String)=db.getNoteDao().searchNote(query)

    suspend fun addNote(note: Note)= db.getNoteDao().addNote(note)

    suspend fun updateNote(note: Note)= db.getNoteDao().updateNote(note)

    suspend fun deleteNote(note: Note)= db.getNoteDao().deleteNote(note)


}