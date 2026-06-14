package com.example.a208464_zahin_drnazatul_project2


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(application: ApplicationEntity) //dia masuk ke dalam database

    // DAO ni yang betul-betul tulis ke dalam database

    @Query("SELECT * FROM applications ORDER BY id DESC")
    fun getAll(): Flow<List<ApplicationEntity>> // dalam dao ni yg pantau database

    @Delete
    suspend fun delete(application: ApplicationEntity)
}