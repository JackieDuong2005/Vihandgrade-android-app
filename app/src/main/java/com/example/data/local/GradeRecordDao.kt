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

    @Query("SELECT * FROM grade_records ORDER BY timestamp DESC")
    suspend fun getAllRecordsList(): List<GradeRecordEntity>

    // ============================================================
    // ANALYTICS QUERIES — cho ReportsAnalyticsScreen
    // ============================================================

    /** Tổng số bài đã chấm */
    @Query("SELECT COUNT(*) FROM grade_records")
    fun getTotalCount(): Flow<Int>

    /** Điểm trung bình tổng */
    @Query("SELECT COALESCE(AVG(totalScore), 0.0) FROM grade_records")
    fun getAverageScore(): Flow<Float>

    /** Số bài đạt Xuất sắc (>= 9.0) */
    @Query("SELECT COUNT(*) FROM grade_records WHERE totalScore >= 9.0")
    fun getExcellentCount(): Flow<Int>

    /** Số bài đạt Tốt (>= 8.0 và < 9.0) */
    @Query("SELECT COUNT(*) FROM grade_records WHERE totalScore >= 8.0 AND totalScore < 9.0")
    fun getGoodCount(): Flow<Int>

    /** Số bài đạt Khá (>= 6.5 và < 8.0) */
    @Query("SELECT COUNT(*) FROM grade_records WHERE totalScore >= 6.5 AND totalScore < 8.0")
    fun getFairCount(): Flow<Int>

    /** Số bài Cần cố gắng (< 6.5) */
    @Query("SELECT COUNT(*) FROM grade_records WHERE totalScore < 6.5")
    fun getNeedsImprovementCount(): Flow<Int>

    /** Danh sách distinct className */
    @Query("SELECT DISTINCT className FROM grade_records ORDER BY className")
    fun getDistinctClasses(): Flow<List<String>>

    /** Lọc bản ghi theo lớp */
    @Query("SELECT * FROM grade_records WHERE className = :className ORDER BY timestamp DESC")
    fun getRecordsByClass(className: String): Flow<List<GradeRecordEntity>>

    /** Lọc bản ghi theo khoảng thời gian */
    @Query("SELECT * FROM grade_records WHERE timestamp >= :fromTimestamp ORDER BY timestamp DESC")
    fun getRecordsSince(fromTimestamp: Long): Flow<List<GradeRecordEntity>>

    /** Lọc bản ghi theo cả lớp VÀ khoảng thời gian */
    @Query("SELECT * FROM grade_records WHERE className = :className AND timestamp >= :fromTimestamp ORDER BY timestamp DESC")
    fun getRecordsByClassSince(className: String, fromTimestamp: Long): Flow<List<GradeRecordEntity>>

    /** Học sinh có điểm trung bình thấp nhất (nhóm theo tên) */
    @Query("SELECT studentName, className, AVG(totalScore) as avgScore, errorsJson FROM grade_records GROUP BY studentName HAVING AVG(totalScore) < 6.5 ORDER BY avgScore ASC LIMIT 5")
    fun getUnderperformingStudents(): Flow<List<UnderperformingStudentTuple>>
}

/** Tuple cho truy vấn học sinh cần rèn luyện */
data class UnderperformingStudentTuple(
    val studentName: String,
    val className: String,
    val avgScore: Float,
    val errorsJson: String
)
