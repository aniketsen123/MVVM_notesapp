package com.tec.mvvm_notesapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Database
import com.tec.mvvm_notesapp.Database.NoteDatabase
import com.tec.mvvm_notesapp.Database.NotesRepository
import com.tec.mvvm_notesapp.databinding.ActivityAddNoteBinding
import com.tec.mvvm_notesapp.models.Notes
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class add_note : AppCompatActivity() {
    private lateinit var binding:ActivityAddNoteBinding
    private lateinit var note:Notes
    private lateinit var old_note:Notes
    lateinit var repository: NotesRepository
    var isUpdate=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dao=NoteDatabase.getDatabase(application).getNoteDao()
        repository= NotesRepository(dao)

        try {
            old_note=intent.getSerializableExtra("current_note") as Notes
            binding.etTitle.setText(old_note.title)
            binding.etNotes.setText(old_note.note)
            isUpdate=true
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        binding.check.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val note_desc = binding.etNotes.text.toString()
            if (title.isNotEmpty() && note_desc.isNotEmpty()) {
                val formaltime = SimpleDateFormat("EEE, d.MMM.yyyy (HH:mm a)")
                if (isUpdate) {
                    note = Notes(old_note.id, title, note_desc, formaltime.format(Date()))
                } else {
                    note = Notes(null, title, note_desc, formaltime.format(Date()))
                }
//Toast.makeText(this,"$title   $note_desc",Toast.LENGTH_SHORT).show()

                val intent = Intent()
                intent.putExtra("note", note)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else
            {
                Toast.makeText(this,"Enter Title and Note",Toast.LENGTH_SHORT).show()
            }
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }

    }
}