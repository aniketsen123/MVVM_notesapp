package com.tec.mvvm_notesapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tec.mvvm_notesapp.Adapter.NotesAdapter
import com.tec.mvvm_notesapp.Database.NoteDatabase
import com.tec.mvvm_notesapp.databinding.ActivityMainBinding
import com.tec.mvvm_notesapp.models.NoteViewModel
import com.tec.mvvm_notesapp.models.Notes
import kotlinx.coroutines.launch


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
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        setUpFab()
    noteViewModel=ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        noteViewModel.allnotes.observe(this,{list->
            list?.let{

                notesAdapter.updatelist(list)

            }
        })
        noteDatabase=NoteDatabase.getDatabase(this)
    }

    private fun setUpFab() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fabText.visibility == View.VISIBLE) {
                    binding.fabText.visibility = View.GONE
                } else if (dy < 0 && binding.fabText.visibility != View.VISIBLE) {
                    binding.fabText.visibility = View.VISIBLE
                }
            }
        })
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
        binding.fab.setOnClickListener {
            val intent=Intent(this,add_note::class.java)
            getcontent.launch(intent)
        }
        binding.fabCircle.setOnClickListener {
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
            val builder = AlertDialog.Builder(this)

            builder.setMessage("Are you sure you want to delete this note?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    noteViewModel.delete(selectednote)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()





            return true
        }
        return false
    }

}