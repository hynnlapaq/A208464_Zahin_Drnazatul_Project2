package com.example.a208464_zahin_drnazatul_project2


import android.app.Application

class JobApplication : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
    val repository: ApplicationRepository by lazy {
        ApplicationRepository(database.applicationDao())
    }
}