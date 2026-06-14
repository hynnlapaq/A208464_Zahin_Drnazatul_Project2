package com.example.a208464_zahin_drnazatul_project2


data class JobPost(
    val id: Int,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val description: String,
    val requirements: String,
    val category: String
)

// Note: Application data class is kept for reference only.
// The app now uses ApplicationEntity (Room) for persistent storage.
data class Application(
    val jobTitle: String,
    val company: String,
    val applicantName: String,
    val applicantEmail: String,
    val coverNote: String,
    val status: String = "Pending Review"
)

data class ChatMessage(
    val sender: String,   // "user" or "employer"
    val message: String,
    val timestamp: String
)