package com.example.noteit.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import converters.Converters
import java.io.Serializable


@Entity
@TypeConverters(Converters::class)
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    val title: String,
    val content: String,
    val date: String,
    val date1: String,
    val color: Int = -1,
    var imageUrl: String? = null

    ):Serializable
