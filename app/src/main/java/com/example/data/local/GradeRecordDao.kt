package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeRecordDao {
    @Query("SELECT * FROM grade_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<GradeRecordEntity>>

    @Query("SELECT * FROM grade_records WHERE id = :id LIMIT 1")
    fun getRecordById(id: Long): Flow<GradeRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GradeRecordEntity): Long

    @Query("DELETE FROM grade_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM grade_records")
    suspend fun deleteAllRecords()
}
