package com.shaikot.notebook.ui.fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import com.shaikot.notebook.R
import com.shaikot.notebook.databinding.FragmentCreateNotesBinding
import com.shaikot.notebook.model.Notes
import com.shaikot.notebook.viewModel.NotesViewModel
import java.util.*


class CreateNotesFragment : Fragment(), MenuProvider{

    lateinit var binding: FragmentCreateNotesBinding

    var priority: String = "1"

    private val viewModel: NotesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateNotesBinding.inflate(layoutInflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Navigation.findNavController(view!!).navigate(R.id.action_createNotesFragment_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.pGreen.setImageResource(R.drawable.ic_baseline_done_24)

        binding.pGreen.setOnClickListener {
            priority = "1"
            binding.pGreen.setImageResource(R.drawable.ic_baseline_done_24)
            binding.pYellow.setImageResource(0)
            binding.pRed.setImageResource(0)
        }

        binding.pRed.setOnClickListener {
            priority = "3"
            binding.pRed.setImageResource(R.drawable.ic_baseline_done_24)
            binding.pYellow.setImageResource(0)
            binding.pGreen.setImageResource(0)
        }

        binding.pYellow.setOnClickListener {
            priority = "2"
            binding.pYellow.setImageResource(R.drawable.ic_baseline_done_24)
            binding.pGreen.setImageResource(0)
            binding.pRed.setImageResource(0)
        }

        binding.saveBtn.setOnClickListener {
            createNote(it)
        }

        return binding.root
    }

    private fun createNote(it: View?) {
        val title = binding.title.text.toString()
        val subTitle = binding.subtitle.text.toString()
        val note = binding.notes.text.toString()

        val d = Date()
        val notesDate: CharSequence = DateFormat.format("MMMM d, yyyy", d.time)

        val data = Notes(null, title, subTitle, note, notesDate.toString(), priority)

        viewModel.addNote(data)

        Toast.makeText(requireActivity(), "Note added successfully", Toast.LENGTH_SHORT).show()

        Navigation.findNavController(it!!).navigate(R.id.action_createNotesFragment_to_homeFragment)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> {
                false
            }
        }
    }


}