package com.tec.mvvm_notesapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View.OnCreateContextMenuListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tec.mvvm_notesapp.Database.NoteDao
import com.tec.mvvm_notesapp.Database.NoteDatabase
import com.tec.mvvm_notesapp.Database.NotesRepository
import com.tec.mvvm_notesapp.databinding.ActivityAddNoteBinding
import com.tec.mvvm_notesapp.models.NoteViewModel
import com.tec.mvvm_notesapp.models.Notes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class add_note : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private val RQ = 1
    private lateinit var note: Notes
    private lateinit var old_note: Notes
    lateinit var repository: NotesRepository
    lateinit var outputTV: TextView
    lateinit var micIV: ImageView
    var isUpdate = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dao = NoteDatabase.getDatabase(application).getNoteDao()
        repository = NotesRepository(dao)

        try {
            old_note = intent.getSerializableExtra("current_note") as Notes
            binding.etTitle.setText(old_note.title)
            binding.edtSubtitle.setText(old_note.subtitle)
            binding.etNotes.setText(old_note.note)
            isUpdate = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Share Notes feature
        binding.share.setOnClickListener {
            val message = binding.etNotes.text.toString()
            val title = binding.etTitle.text.toString()
            val shareNote = "${"Title: $title"}\n${message} "
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plane"
            myIntent.putExtra(Intent.EXTRA_TEXT, shareNote)
            this.startActivity(myIntent)
        }
        binding.check.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val subtitle = binding.edtSubtitle.text.toString()
            val note_desc = binding.etNotes.text.toString()
            if (title.isNotEmpty() && subtitle.isNotEmpty() && note_desc.isNotEmpty()) {
                val formaltime = SimpleDateFormat("EEE, d.MMM.yyyy (HH:mm a)")
                if (isUpdate) {
                    note = Notes(old_note.id, title, subtitle, note_desc, formaltime.format(Date()))
                } else {
                    note = Notes(null, title, subtitle, note_desc, formaltime.format(Date()))
                }

                //Toast.makeText(this,"$title  $note_desc",Toast.LENGTH_SHORT).show()
                val intent = Intent()
                intent.putExtra("note", note)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Fill all entries", Toast.LENGTH_SHORT).show()
            }
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }

        outputTV = findViewById(R.id.et_notes)
        micIV = findViewById(R.id.speechbtn)

        micIV.setOnClickListener {
            // on below line we are calling speech recognizer intent.
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            // on below line we are passing language model
            // and model free form in our intent
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            // on below line we are passing our
            // language as a default language.
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            // on below line we are specifying a prompt
            // message as speak to text on below line.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            // on below line we are specifying a try catch block.
            // in this block we are calling a start activity
            // for result method and passing our result code.
            try {
                startActivityForResult(intent, RQ)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
                Toast
                    .makeText(
                        this@add_note, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == RQ) {
            // on below line we are checking if result code is ok
            if (resultCode == RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                outputTV.setText(
                    Objects.requireNonNull(res)[0]
                )
            }

        }



    }


}