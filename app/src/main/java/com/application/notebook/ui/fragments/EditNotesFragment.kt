package com.application.notebook.ui.fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.application.notebook.R
import com.application.notebook.databinding.FragmentEditNotesBinding
import com.application.notebook.model.Notes
import com.application.notebook.viewModel.NotesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class EditNotesFragment : Fragment(), MenuProvider {

    val note by navArgs<EditNotesFragmentArgs>()
    lateinit var binding: FragmentEditNotesBinding
    lateinit var priority: String
    val viewModel: NotesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEditNotesBinding.inflate(layoutInflater, container, false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Navigation.findNavController(view!!).navigate(R.id.action_editNotesFragment_to_homeFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.title.setText(note.data.title)
        binding.subtitle.setText(note.data.subTitle)
        binding.notes.setText(note.data.notes)


        when (note.data.priority){
            "1" -> {binding.pGreen.setImageResource(R.drawable.ic_baseline_done_24)}
            "2" -> {binding.pYellow.setImageResource(R.drawable.ic_baseline_done_24)}
            "3" -> {binding.pRed.setImageResource(R.drawable.ic_baseline_done_24)}
        }

        priority = note.data.priority

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
            updateNote(it)
        }


        return binding.root
    }

    private fun updateNote(it: View?) {
        val title = binding.title.text.toString()
        val subTitle = binding.subtitle.text.toString()
        val notes = binding.notes.text.toString()

        val d = Date()
        val notesDate: CharSequence = DateFormat.format("MMMM d, yyyy", d.time)

        val data = Notes(note.data.id, title, subTitle, notes, notesDate.toString(), priority)

        viewModel.updateNote(data)

        Toast.makeText(requireActivity(), "Note updated successfully", Toast.LENGTH_SHORT).show()

        Navigation.findNavController(it!!).navigate(R.id.action_editNotesFragment_to_homeFragment)

    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.menu_delete){
            val bottomSheet = BottomSheetDialog(requireContext(),R.style.BottomSheetStyle)
            bottomSheet.setContentView(R.layout.delete_layout)

            val yesButton = bottomSheet.findViewById<TextView>(R.id.yesButton)
            val noButton = bottomSheet.findViewById<TextView>(R.id.noButton)

            yesButton?.setOnClickListener {
                viewModel.deleteNote(note.data.id!!)
                bottomSheet.dismiss()
                requireActivity().onBackPressed()
            }

            noButton?.setOnClickListener {
                bottomSheet.dismiss()
            }

            bottomSheet.show()
        }
        else if (menuItem.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
            return true
        }
        return true
    }





}