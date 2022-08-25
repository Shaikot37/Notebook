package com.shaikot.notebook.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.shaikot.notebook.R
import com.shaikot.notebook.databinding.ItemNotesBinding
import com.shaikot.notebook.model.Notes
import com.shaikot.notebook.ui.fragments.HomeFragmentDirections

class NotesAdapter(val requireContext: Context, var notesList: List<Notes>) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    fun filtering(newFilterList: ArrayList<Notes>) {
        notesList = newFilterList
        notifyDataSetChanged()
    }

    class NotesViewHolder(val binding: ItemNotesBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            ItemNotesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val data = notesList[position]
        holder.binding.notesTitle.text = data.title
        holder.binding.notesSubtitle.text = data.subTitle
        holder.binding.notesDate.text = data.date

        when (data.priority){
            "1" -> {holder.binding.viewPriority.setBackgroundResource(R.drawable.green_dot)}
            "2" -> {holder.binding.viewPriority.setBackgroundResource(R.drawable.yellow_dot)}
            "3" -> {holder.binding.viewPriority.setBackgroundResource(R.drawable.red_dot)}
        }


        holder.binding.root.setOnClickListener{

            //Store in SharedPreference
            val preference=requireContext.getSharedPreferences("DeleteID", Context.MODE_PRIVATE)
            val editor=preference.edit()
            editor.putInt("id",data.id!!)
            editor.commit()

            val action = HomeFragmentDirections.actionHomeFragmentToEditNotesFragment(data)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }
}