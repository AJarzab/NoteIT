package com.example.noteit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.noteit.R
import com.example.noteit.databinding.ActivityMainBinding
import com.example.noteit.db.NoteDatabase
import com.example.noteit.repository.NoteRepository
import com.example.noteit.viewModel.NoteActivityViewModel
import com.example.noteit.viewModel.NoteActivityViewModelFactory


class MainActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel: NoteActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

        try {
            setContentView(binding.root)
            val noteRepository= NoteRepository(NoteDatabase(this))
            val noteActivityModelFactory= NoteActivityViewModelFactory(noteRepository)
            noteActivityViewModel= ViewModelProvider(this, noteActivityModelFactory)[NoteActivityViewModel::class.java]
        }catch (e: Exception)
        {
            Log.d("TAG", "Error")
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}