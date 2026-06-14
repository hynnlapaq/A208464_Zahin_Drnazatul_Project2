package com.example.a208464_zahin_drnazatul_project2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val jobTitle: String,
    val company: String,
    val applicantName: String,
    val applicantEmail: String,
    val coverNote: String,
    val status: String = "Pending Review"
)

//tempat lepas isi dekat apply screen akan hantar ke viewmodel