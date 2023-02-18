package com.tec.mvvm_notesapp.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tec.mvvm_notesapp.Database.NoteDatabase
import com.tec.mvvm_notesapp.Database.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application):AndroidViewModel(application) {

    var allnotes:LiveData<List<Notes>>
    var repository: NotesRepository

   init {
       val dao=NoteDatabase.getDatabase(application).getNoteDao()
       repository= NotesRepository(dao)
       allnotes=repository.allnotes

   }

    fun delete(notes: Notes)=viewModelScope.launch(Dispatchers.IO) {
        repository.delete(notes)
    }
    fun insert(notes: Notes)=viewModelScope.launch (Dispatchers.IO){
        repository.insert(notes)
    }
    fun update(notes: Notes)=viewModelScope.launch(Dispatchers.IO) {
        repository.update(notes)
    }
}