package com.tec.mvvm_notesapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.Window
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tec.mvvm_notesapp.Adapter.NotesAdapter
import com.tec.mvvm_notesapp.Database.NoteDao
import com.tec.mvvm_notesapp.Database.NoteDatabase
import com.tec.mvvm_notesapp.databinding.ActivityMainBinding
import com.tec.mvvm_notesapp.databinding.DialogDeleteBinding
import com.tec.mvvm_notesapp.databinding.ListItemBinding
import com.tec.mvvm_notesapp.models.NoteViewModel
import com.tec.mvvm_notesapp.models.Notes
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(),NotesAdapter.NotesClickListner
      {
    lateinit var binding: ActivityMainBinding
    lateinit var noteDatabase: NoteDatabase
    lateinit var noteViewModel: NoteViewModel
    lateinit var notesAdapter: NotesAdapter
    lateinit var listItemBinding: ListItemBinding
    private lateinit var selectednote:Notes
    private var updateNote=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode==Activity.RESULT_OK)
        {
            val note=result.data?.getSerializableExtra("note") as? Notes
            if(note!=null)
            {
                noteViewModel.update(note)
            }
        }

    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listItemBinding= ListItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        noteViewModel=ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        noteViewModel.allnotes.observe(this,{list->
            list?.let{

                notesAdapter.updatelist(list)

            }
        })
        noteDatabase=NoteDatabase.getDatabase(this)


        listItemBinding.deleteButton.setOnClickListener {
            val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_delete)
                val yesButton = dialog.findViewById<Button>(R.id.delete_yes)
                yesButton.setOnClickListener {
                    dialog.dismiss()
                }

                val noButton = dialog.findViewById<Button>(R.id.delete_no)
                noButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()

        }


    }

    private fun initUi() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager=StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        notesAdapter= NotesAdapter(this,this)
        binding.recyclerView.adapter=notesAdapter
        val getcontent=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode==Activity.RESULT_OK)
            {
                val note=result.data?.getSerializableExtra("note") as? Notes
                if(note!=null)
                {
                    noteViewModel.insert(note)
                }
            }

        }
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, add_note::class.java)
            getcontent.launch(intent)
        }


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=null)
                {
                    notesAdapter.filterList(newText)
                }
                return true
            }

        })
    }

    override fun OnItemClick(notes: Notes) {
        val intent = Intent(this, add_note::class.java)
        intent.putExtra("current_note", notes)
        updateNote.launch(intent)
    }

          override fun OnLongItemCLick(notes: Notes, cardView: CardView) {
              val builder = AlertDialog.Builder(this)
              val dialogView = layoutInflater.inflate(R.layout.dialog_delete, null)
              builder.setView(dialogView)
              builder.setCancelable(false) // Disable dialog dismiss
              val dialogYes = dialogView.findViewById<Button>(R.id.delete_yes)
              val dialogNo = dialogView.findViewById<Button>(R.id.delete_no)
              val alertDialog = builder.create()
              alertDialog.show()
              dialogYes.setOnClickListener {
                  selectednote=notes
                  lifecycleScope.launch {
                      noteViewModel.delete(selectednote)
                  }
                  alertDialog.dismiss()
              }

              dialogNo.setOnClickListener {
                  alertDialog.dismiss()
              }
          }










      }



