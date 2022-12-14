package com.shaikot.notebook

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController


class MainActivity : AppCompatActivity() , MenuProvider {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragmentContainerView)
        setupActionBarWithNavController(navController)

    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.main_menu, menu)
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        /*when (menuItem.itemId) {
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
                val id = preference.getInt("id", 0)

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

        }*/
        return true
    }
}



