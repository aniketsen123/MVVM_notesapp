package com.tec.mvvm_notesapp.Adapter

import android.content.Context
import android.os.Build
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.tec.mvvm_notesapp.R
import com.tec.mvvm_notesapp.models.Notes
import java.util.*
import kotlin.random.Random

class NotesAdapter(private val context:Context,
                   val listner:NotesClickListner,

                   ): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    private val Noteslist=ArrayList<Notes>()
    private val Fulllist=ArrayList<Notes>()

    inner class ViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        val notes_layout=itemview.findViewById<CardView>(R.id.card_layout)
        val title=itemview.findViewById<TextView>(R.id.tv_title)
        val subtitle=itemview.findViewById<TextView>(R.id.tv_subtitle)
        val date=itemview.findViewById<TextView>(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentnote=Noteslist[position]
        holder.title.text=currentnote.title
        holder.title.isSelected=true
        holder.subtitle.text=currentnote.subtitle
        holder.subtitle.isSelected=true
        holder.date.text=currentnote.date
        holder.date.isSelected=true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.notes_layout.setCardBackgroundColor(holder.itemView.resources.getColor(RandomColor(),null))
        }
        holder.notes_layout.setOnClickListener {
            listner.OnItemClick(Noteslist[holder.adapterPosition])
        }
        holder.notes_layout.setOnLongClickListener {
            listner.OnLongItemCLick(Noteslist[holder.adapterPosition],holder.notes_layout)
            true
        }

    }

    fun updatelist(newList:List<Notes>)
    {
        Fulllist.clear()
        Fulllist.addAll(newList)
        Noteslist.clear()
        Noteslist.addAll(newList)

        notifyDataSetChanged()
    }

    fun filterList(search:String)
    {
        Noteslist.clear()
        for(item in Fulllist){
            if(item.title?.lowercase()?.contains(search.lowercase())==true || item.note?.lowercase()?.contains(search.lowercase())==true)
            {
                Noteslist.add(item)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return Noteslist.size
    }

    fun RandomColor():Int{
        val list= ArrayList<Int>()
        list.add(R.color.blue)
        list.add(R.color.green)
        list.add(R.color.indigo)
        list.add(R.color.rose)
        list.add(R.color.choco)
        list.add(R.color.pink)

        val seed=System.currentTimeMillis().toInt()
        val randomIndex= Random(seed.toLong()).nextInt(list.size)
        return list[randomIndex]

    }

    interface NotesClickListner{
        fun OnItemClick(notes: Notes)
        fun OnLongItemCLick(notes:Notes,cardView: CardView)
    }
}