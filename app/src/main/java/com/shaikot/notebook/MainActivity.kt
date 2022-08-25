package com.shaikot.notebook

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.api.services.drive.Drive
import com.shaikot.notebook.google.GoogleDriveActivity
import com.shaikot.notebook.google.GoogleDriveApiDataRepository
import com.shaikot.notebook.viewModel.NotesViewModel
import java.io.File


class MainActivity : GoogleDriveActivity() {

    lateinit var navController: NavController

    var menuList: Menu? = null

    private var repository: GoogleDriveApiDataRepository? = null

    private val GOOGLE_DRIVE_DB_LOCATION = "Notes"
    private val GOOGLE_DRIVE_DB_LOCATION1 = "Notes-shm"
    private val GOOGLE_DRIVE_DB_LOCATION2 = "Notes-wal"

    private val DB_LOCATION = "/data/data/com.shaikot.notebook/databases/Notes"
    private val DB_LOCATION1 = "/data/data/com.shaikot.notebook/databases/Notes-shm"
    private val DB_LOCATION2 = "/data/data/com.shaikot.notebook/databases/Notes-wal"


    private val viewModel: NotesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragmentContainerView)
        setupActionBarWithNavController(navController)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuList = menu

        val title:CharSequence? = supportActionBar?.title
            //Toast.makeText(this, menuList!!.size().toString(),Toast.LENGTH_SHORT).show()

        when (title.toString()) {
            "All Notes" -> {
                //Toast.makeText(this,"All Notes",Toast.LENGTH_SHORT).show()
                menuInflater.inflate(R.menu.main_menu, menu)

                if(repository!=null){
                    hideOption(R.id.signin)
                }
                else if(repository==null){
                    showOption(R.id.signin)
                }
            }
            "Edit Note" -> {
                //Toast.makeText(this,"Edit Note",Toast.LENGTH_SHORT).show()
                menuInflater.inflate(R.menu.delete_menu, menu)
            }

        }

        return true
    }

    private fun hideOption(id: Int) {
        //Toast.makeText(this,"hide size:"+menuList?.size(),Toast.LENGTH_SHORT).show()
        val item: MenuItem = menuList!!.findItem(id)
        item.isVisible = false
    }

    private fun showOption(id: Int) {
        //Toast.makeText(this,"show size:"+menuList?.size(),Toast.LENGTH_SHORT).show()
        val item: MenuItem = menuList!!.findItem(id)
        item.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_delete -> {
                val bottomSheet = BottomSheetDialog(this, R.style.BottomSheetStyle)
                bottomSheet.setContentView(R.layout.delete_layout)

                val yesButton = bottomSheet.findViewById<TextView>(R.id.yesButton)
                val noButton = bottomSheet.findViewById<TextView>(R.id.noButton)

                //Retrieve from SharedPreference
                val preference = getSharedPreferences("DeleteID", Context.MODE_PRIVATE)
                val id = preference.getInt("id",0)

                yesButton?.setOnClickListener {
                    viewModel.deleteNote(id)
                    preference.edit().clear().apply()
                    bottomSheet.dismiss()
                    onBackPressed()
                }

                noButton?.setOnClickListener {
                    bottomSheet.dismiss()
                }

                bottomSheet.show()
            }

            R.id.signin -> {
                startGoogleDriveSignIn()
                return true
            }

            R.id.upload -> {
                if (repository == null) {
                    Toast.makeText(this, "Signed In Error", Toast.LENGTH_SHORT).show()
                    return true
                }
                uploadNotes()
                return true
            }


            R.id.download -> {
                if (repository == null) {
                    Toast.makeText(this, "Signed In Error", Toast.LENGTH_SHORT).show()
                    return true
                }
                if (isNetworkConnected() && internetIsConnected()) {
                    downloadNotes()

                } else {
                    Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show()
                }

                return true
            }
        }

        return true
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

                                Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
                            }
                    }

            }

        repository!!.uploadFile(db_note, GOOGLE_DRIVE_DB_LOCATION)
            .addOnFailureListener {
                Toast.makeText(
                    this,
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

                                Toast.makeText(this, "Retrieved Successfully!", Toast.LENGTH_SHORT).show()

                                //refresh activity
                                refreshData(this,Intent())
                            }
                    }

            } .addOnFailureListener{
                Toast.makeText(this, "Download Error", Toast.LENGTH_SHORT).show();
            }
    }


    private fun refreshData(context: Context, nextIntent: Intent?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("KEY_RESTART_INTENT", nextIntent)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
        Runtime.getRuntime().exit(0)
    }


    override fun onGoogleDriveSignedInSuccess(driveApi: Drive?) {
        Toast.makeText(this, "Client Sign in success!", Toast.LENGTH_SHORT).show()
        repository = GoogleDriveApiDataRepository(driveApi)

        if(repository!=null){
            hideOption(R.id.signin)
        }
        else if(repository==null){
            showOption(R.id.signin)
        }
    }


    override fun onGoogleDriveSignedInFailed(exception: ApiException?) {
        Toast.makeText(this, "Client Sign in failed!", Toast.LENGTH_SHORT).show()
        Log.e("error", "error google drive sign in", exception)

        if(repository!=null){
            hideOption(R.id.signin)
        }
        else if(repository==null){
            showOption(R.id.signin)
        }
    }


    private fun isNetworkConnected(): Boolean {
        val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
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


