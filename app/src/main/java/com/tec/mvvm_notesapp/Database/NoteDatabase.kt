package com.tec.mvvm_notesapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tec.mvvm_notesapp.models.Notes
import com.tec.mvvm_notesapp.utils.DATABASE_NAME

@Database(entities = arrayOf(Notes::class), version = 1, exportSchema = false)
abstract class NoteDatabase:RoomDatabase() {

    abstract fun getNoteDao():NoteDao

    companion object{
  @Volatile
  private var INSTANCE:NoteDatabase?=null

        fun getDatabase(context: Context):NoteDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance

                instance
            }
        }

    }

}