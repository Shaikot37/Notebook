package com.shaikot.notebook.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.api.services.drive.Drive
import com.shaikot.notebook.MainActivity
import com.shaikot.notebook.R
import com.shaikot.notebook.databinding.FragmentHomeBinding
import com.shaikot.notebook.google.GoogleDriveActivity
import com.shaikot.notebook.google.GoogleDriveApiDataRepository
import com.shaikot.notebook.model.Notes
import com.shaikot.notebook.ui.adapter.NotesAdapter
import com.shaikot.notebook.viewModel.NotesViewModel
import java.io.File


class HomeFragment : GoogleDriveActivity(), MenuProvider {

    lateinit var binding: FragmentHomeBinding

    var myOldNotes = arrayListOf<Notes>()
    var adapter: NotesAdapter? = null
    private var searchView:SearchView? = null

    var menuList: Menu? = null

    private var repository: GoogleDriveApiDataRepository? = null

    private val GOOGLE_DRIVE_DB_LOCATION = "Notes"
    private val GOOGLE_DRIVE_DB_LOCATION1 = "Notes-shm"
    private val GOOGLE_DRIVE_DB_LOCATION2 = "Notes-wal"

    private val DB_LOCATION = "/data/data/com.shaikot.notebook/databases/Notes"
    private val DB_LOCATION1 = "/data/data/com.shaikot.notebook/databases/Notes-shm"
    private val DB_LOCATION2 = "/data/data/com.shaikot.notebook/databases/Notes-wal"


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

        menuList = menu

        if(repository!=null){
            hideOption(R.id.signin, menuList!!)
        }
        else if(repository==null){
            showOption(R.id.signin, menuList!!)
        }

        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        Toast.makeText(requireContext(), menuList!!.size().toString(),Toast.LENGTH_SHORT).show()
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


    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

         when (menuItem.itemId) {

            R.id.signin -> {
                startGoogleDriveSignIn()
                return true
            }

            R.id.upload -> {
                if (repository == null) {
                    Toast.makeText(requireContext(), "Signed In Error", Toast.LENGTH_SHORT).show()
                    return true
                }
                uploadNotes()
                return true
            }


            R.id.download -> {
                if (repository == null) {
                    Toast.makeText(requireContext(), "Signed In Error", Toast.LENGTH_SHORT).show()
                    return true
                }
                if (isNetworkConnected() && internetIsConnected()) {
                    downloadNotes()

                } else {
                    Toast.makeText(requireContext(), "No internet connection!", Toast.LENGTH_SHORT).show()
                }

                return true
            }

             androidx.transition.R.id.home ->{
                 requireActivity().onBackPressed()
                 return true
             }


            else -> {
                return false
            }
        }



    }



    private fun hideOption(id: Int, menu:Menu) {
        Toast.makeText(requireContext(),"hide size:"+menuList?.size(),Toast.LENGTH_SHORT).show()
        val item: MenuItem = menu.findItem(id)
        item.isVisible = false
    }

    private fun showOption(id: Int, menu:Menu) {
        //Toast.makeText(this,"show size:"+menuList?.size(),Toast.LENGTH_SHORT).show()
        val item: MenuItem = menu.findItem(id)
        item.isVisible = true
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




    private fun uploadNotes(){
        val db_note = File(DB_LOCATION)
        val db_note_shm = File(DB_LOCATION1)
        val db_note_wal = File(DB_LOCATION2)

        repository!!.uploadFile(db_note, GOOGLE_DRIVE_DB_LOCATION)
            .addOnSuccessListener {

                repository!!.uploadFile(db_note_shm, GOOGLE_DRIVE_DB_LOCATION1)
                    .addOnSuccessListener {

                        repository!!.uploadFile(db_note_wal, GOOGLE_DRIVE_DB_LOCATION2)
                            .addOnSuccessListener {

                                Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_SHORT).show()
                            }
                    }

            }

        repository!!.uploadFile(db_note, GOOGLE_DRIVE_DB_LOCATION)
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Upload Error",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun downloadNotes(){
        val data_note = File(DB_LOCATION)
        val data_note_shm = File(DB_LOCATION1)
        val data_note_wal = File(DB_LOCATION2)

        data_note.parentFile?.mkdirs()
        data_note.delete()

        data_note_shm.parentFile?.mkdirs()
        data_note_shm.delete()

        data_note_wal.parentFile?.mkdirs()
        data_note_wal.delete()

        repository!!.downloadFile(data_note, GOOGLE_DRIVE_DB_LOCATION)
            .addOnSuccessListener {

                repository!!.downloadFile(data_note_shm, GOOGLE_DRIVE_DB_LOCATION1)
                    .addOnSuccessListener {

                        repository!!.downloadFile(data_note_wal, GOOGLE_DRIVE_DB_LOCATION2)
                            .addOnSuccessListener {

                                Toast.makeText(requireContext(), "Retrieved Successfully!", Toast.LENGTH_SHORT).show()

                                //refresh activity
                                refreshData(requireActivity(), Intent())
                            }
                    }

            } .addOnFailureListener{
                Toast.makeText(requireContext(), "Download Error", Toast.LENGTH_SHORT).show();
            }
    }


    private fun refreshData(context: Context, nextIntent: Intent?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("KEY_RESTART_INTENT", nextIntent)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
        Runtime.getRuntime().exit(0)
    }


    override fun onGoogleDriveSignedInSuccess(driveApi: Drive?) {
        Toast.makeText(requireContext(), "Client Sign in success!", Toast.LENGTH_SHORT).show()
        repository = GoogleDriveApiDataRepository(driveApi)

        /*if(repository!=null){
            hideOption(R.id.signin)
        }
        else if(repository==null){
            showOption(R.id.signin)
        }*/
    }


    override fun onGoogleDriveSignedInFailed(exception: ApiException?) {
        Toast.makeText(requireContext(), "Client Sign in failed!", Toast.LENGTH_SHORT).show()
        Log.e("error", "error google drive sign in", exception)

        /*if(repository!=null){
            hideOption(R.id.signin)
        }
        else if(repository==null){
            showOption(R.id.signin)
        }*/
    }


    private fun isNetworkConnected(): Boolean {
        val cm = requireActivity().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }

    private fun internetIsConnected(): Boolean {
        return try {
            val command = "ping -c 1 google.com"
            Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }


}