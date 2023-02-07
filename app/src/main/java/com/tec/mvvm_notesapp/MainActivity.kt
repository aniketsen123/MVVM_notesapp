package com.tec.mvvm_notesapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tec.mvvm_notesapp.Adapter.NotesAdapter
import com.tec.mvvm_notesapp.Database.NoteDatabase
import com.tec.mvvm_notesapp.databinding.ActivityMainBinding
import com.tec.mvvm_notesapp.models.NoteViewModel
import com.tec.mvvm_notesapp.models.Notes

class MainActivity : AppCompatActivity(),NotesAdapter.NotesClickListner,
    PopupMenu.OnMenuItemClickListener {
    lateinit var binding: ActivityMainBinding
    lateinit var noteDatabase: NoteDatabase
    lateinit var noteViewModel: NoteViewModel
    lateinit var notesAdapter: NotesAdapter
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
    noteViewModel=ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        noteViewModel.allnotes.observe(this,{list->
            list?.let{

                notesAdapter.updatelist(list)

            }
        })
        noteDatabase=NoteDatabase.getDatabase(this)
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
            val intent=Intent(this,add_note::class.java)
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
        val intent=Intent(this,add_note::class.java)
        intent.putExtra("current_note",notes)
        updateNote.launch(intent)
    }

    override fun OnLongItemCLick(notes: Notes, cardView: CardView) {
        selectednote=notes
        popupDisplay(cardView)
    }

    private fun popupDisplay(cardView: CardView) {
          val popupMenu=PopupMenu(this,cardView)
        popupMenu.setOnMenuItemClickListener(this@MainActivity)
         popupMenu.inflate(R.menu.pop_up_menu)
        popupMenu.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId==R.id.delete_node)
        {
            noteViewModel.delete(selectednote)
            return true
        }
        return false
    }
}