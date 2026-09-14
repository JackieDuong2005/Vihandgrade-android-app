package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grade_records")
data class GradeRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val studentName: String,
    val className: String,
    val essayTitle: String,
    val totalScore: Float,
    val spellingScore: Float,
    val formatScore: Float,
    val contentScore: Float,
    val creativityScore: Float,
    val pedagogicalComment: String,
    val extractedText: String,
    val correctedFullText: String,
    val errorsJson: String,
    val serverSource: String,
    val sampleType: String? = null
)
