package com.shaikot.notebook.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.shaikot.notebook.database.NotesDatabase
import com.shaikot.notebook.model.Notes
import com.shaikot.notebook.repository.NotesRepository

class NotesViewModel(application: Application): AndroidViewModel(application) {

    val repository: NotesRepository

    init {
        val dao = NotesDatabase.getDatabaseInstance(application).myNotesDao()
        repository = NotesRepository(dao)
    }

    fun addNote(notes: Notes){
        repository.insertNote(notes)
    }

    fun getNotes():LiveData<List<Notes>> = repository.getAllNotes()

    fun getHighPriorityNotes():LiveData<List<Notes>>{
        return repository.getHighPriorityNotes()
    }

    fun getMediumPriorityNotes():LiveData<List<Notes>>{
        return repository.getMediumPriorityNotes()
    }

    fun getLowPriorityNotes():LiveData<List<Notes>>{
        return repository.getLowPriorityNotes()
    }

    fun deleteNote(id: Int) = repository.deleteNote(id)

    fun updateNote(notes: Notes) = repository.updateNote(notes)
}