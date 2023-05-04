package com.example.noteit.model

import androidx.lifecycle.LiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    val title: String,
    val content: String,
    val date: String,
    val date1: String,
    val color: Int = -1,


    ):Serializable
