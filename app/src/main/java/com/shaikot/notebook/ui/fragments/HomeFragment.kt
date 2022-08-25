package com.shaikot.notebook.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shaikot.notebook.MainActivity
import com.shaikot.notebook.R
import com.shaikot.notebook.databinding.FragmentHomeBinding
import com.shaikot.notebook.model.Notes
import com.shaikot.notebook.ui.adapter.NotesAdapter
import com.shaikot.notebook.viewModel.NotesViewModel


class HomeFragment : Fragment(), MenuProvider {

    lateinit var binding: FragmentHomeBinding

    var myOldNotes = arrayListOf<Notes>()
    var adapter: NotesAdapter? = null
    private var searchView:SearchView? = null

    private val viewModel: NotesViewModel by viewModels()

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



        //val item = (activity as MainActivity).menuList?.findItem(R.id.app_bar_search)
        //Toast.makeText(requireContext(),item.toString(),Toast.LENGTH_SHORT).show()
        //searchView = item?.actionView as SearchView
        //searchView?.queryHint = "Enter notes here.."
       /* searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                notesFiltering(newText)
                return true
            }
        })*/


        adapter?.notifyDataSetChanged()

        return binding.root
    }



    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main_menu, menu)
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        Toast.makeText(requireContext(), "oncreate",Toast.LENGTH_SHORT).show()

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        //val item = menuItem.findItem(R.id.app_bar_search)
        Toast.makeText(requireContext(), "onselect",Toast.LENGTH_SHORT).show()

        when (menuItem.itemId) {
            R.id.app_bar_search -> {

                searchView?.queryHint = "Enter notes here.."

                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        notesFiltering(newText)
                        return true
                    }
                })
                return true
            }
            else -> {
                return false
            }
        }


    }

    private fun notesFiltering(text: String?) {
        val newFilterList = arrayListOf<Notes>()
        for(notes in myOldNotes){
            if(notes.title.contains(text!!) || notes.subTitle.contains(text!!)){
                newFilterList.add(notes)
            }
        }
        adapter?.filtering(newFilterList)
    }



}