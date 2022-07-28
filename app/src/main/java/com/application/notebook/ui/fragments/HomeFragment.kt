package com.application.notebook.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.application.notebook.R
import com.application.notebook.databinding.FragmentHomeBinding
import com.application.notebook.model.Notes
import com.application.notebook.ui.adapter.NotesAdapter
import com.application.notebook.viewModel.NotesViewModel


class HomeFragment : Fragment(), MenuProvider {

    lateinit var binding: FragmentHomeBinding

    var myOldNotes = arrayListOf<Notes>()
    lateinit var adapter: NotesAdapter

    val viewModel: NotesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.getNotes().observe(viewLifecycleOwner) { notesList ->
            myOldNotes = notesList as ArrayList<Notes>
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            binding.recyclerView.layoutManager = staggeredGridLayoutManager
            adapter = NotesAdapter(requireContext(), notesList)
            binding.recyclerView.adapter = adapter
        }


        binding.filterHigh.setOnClickListener {
            viewModel.getHighPriorityNotes().observe(viewLifecycleOwner) { notesList ->
                myOldNotes = notesList as ArrayList<Notes>
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                binding.recyclerView.layoutManager = staggeredGridLayoutManager
                adapter = NotesAdapter(requireContext(), notesList)
                binding.recyclerView.adapter = adapter
            }
        }

        binding.filterMedium.setOnClickListener {
            viewModel.getMediumPriorityNotes().observe(viewLifecycleOwner) { notesList ->
                myOldNotes = notesList as ArrayList<Notes>
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                binding.recyclerView.layoutManager = staggeredGridLayoutManager
                adapter = NotesAdapter(requireContext(), notesList)
                binding.recyclerView.adapter = adapter
            }
        }

        binding.filterLow.setOnClickListener {
            viewModel.getLowPriorityNotes().observe(viewLifecycleOwner) { notesList ->
                myOldNotes = notesList as ArrayList<Notes>
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                binding.recyclerView.layoutManager = staggeredGridLayoutManager
                adapter = NotesAdapter(requireContext(), notesList)
                binding.recyclerView.adapter = adapter
            }
        }

        binding.allNotes.setOnClickListener {
            viewModel.getNotes().observe(viewLifecycleOwner) { notesList ->
                myOldNotes = notesList as ArrayList<Notes>
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                //binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.recyclerView.layoutManager = staggeredGridLayoutManager
                adapter = NotesAdapter(requireContext(), notesList)
                binding.recyclerView.adapter = adapter
            }
        }


        binding.addButton.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_createNotesFragment)
        }
        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.app_bar_search)
        val searchView = item.actionView as SearchView

        searchView.queryHint = "Enter notes here.."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                notesFiltering(newText)
                return true
            }
        })
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }

    private fun notesFiltering(text: String?) {
        val newFilterList = arrayListOf<Notes>()
        for(notes in myOldNotes){
            if(notes.title.contains(text!!) || notes.subTitle.contains(text!!)){
                newFilterList.add(notes)
            }
        }
        adapter.filtering(newFilterList)
    }

}