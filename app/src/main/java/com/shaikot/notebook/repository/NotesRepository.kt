package com.shaikot.notebook.repository

import androidx.lifecycle.LiveData
import com.shaikot.notebook.dao.NotesDao
import com.shaikot.notebook.model.Notes

class NotesRepository(val dao: NotesDao) {

    fun getAllNotes():LiveData<List<Notes>>{
        return dao.getNotes()
    }

    fun getHighPriorityNotes():LiveData<List<Notes>>{
        return dao.getHighNotes()
    }

    fun getMediumPriorityNotes():LiveData<List<Notes>>{
        return dao.getMediumNotes()
    }

    fun getLowPriorityNotes():LiveData<List<Notes>>{
        return dao.getLowNotes()
    }

    fun insertNote(notes: Notes){
        return dao.insertNote(notes)
    }

    fun deleteNote(id: Int){
        return dao.deleteNote(id)
    }

    fun updateNote(notes: Notes){
        return dao.updateNote(notes)
    }
}