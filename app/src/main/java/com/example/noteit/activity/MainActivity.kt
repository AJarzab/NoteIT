package com.example.noteit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.noteit.R
import com.example.noteit.databinding.ActivityMainBinding
import com.example.noteit.db.NoteDatabase
import com.example.noteit.repository.NoteRepository
import com.example.noteit.viewModel.NoteActivityViewModel
import com.example.noteit.viewModel.NoteActivityViewModelFactory


class MainActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var binding:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding= ActivityMainBinding.inflate(layoutInflater)

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
}