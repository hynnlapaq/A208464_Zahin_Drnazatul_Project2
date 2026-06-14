package com.example.a208464_zahin_drnazatul_project2


import kotlinx.coroutines.flow.Flow
import kotlin.text.insert

class ApplicationRepository(private val dao: ApplicationDao) {

    val allApplications: Flow<List<ApplicationEntity>> = dao.getAll()

    suspend fun insert(application: ApplicationEntity) {
        dao.insert(application)
    } // dia terima dan terus akan pass databse ke dao

    suspend fun delete(application: ApplicationEntity) {
        dao.delete(application)
    }
}