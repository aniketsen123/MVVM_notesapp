package com.tec.mvvm_notesapp.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tec.mvvm_notesapp.models.Notes

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notes: Notes)

    @Delete
    suspend fun delete(notes: Notes)

    @Query("Select * from notes_table order by id Asc")
    fun getAllNotes():LiveData<List<Notes>>

    @Query("update notes_table Set title=:title,note=:note,subtitle=:subtitle WHERE id=:id")
    suspend fun update(id:Int?,title:String?,subtitle:String?,note:String?)
}