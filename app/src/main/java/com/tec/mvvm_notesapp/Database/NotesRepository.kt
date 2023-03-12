package com.tec.mvvm_notesapp.Database

import androidx.lifecycle.LiveData
import com.tec.mvvm_notesapp.models.Notes

class NotesRepository(private val notesdao:NoteDao) {

    val allnotes: LiveData<List<Notes>> =notesdao.getAllNotes()
    suspend fun insert(notes:Notes)
    {
        notesdao.insert(notes)
    }
    suspend fun delete(notes: Notes)
    {
        notesdao.delete(notes)
    }
    suspend fun update(notes: Notes)
    {
        notesdao.update(notes.id,notes.title,notes.subtitle,notes.note)
    }
}