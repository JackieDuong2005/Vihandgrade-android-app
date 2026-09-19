package com.example.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import retrofit2.http.Query

interface GradeApiService {
    @POST("api/mobile/grade")
    suspend fun submitForGrading(
        @Body request: GradeApiRequest
    ): Response<GradeApiResponse>

    @GET("api/health")
    suspend fun checkHealth(): Response<Map<String, Any>>

    // Phase 4: Ngữ liệu SGK chính tả
    @GET("api/dictation/passages")
    suspend fun getDictationPassages(
        @Query("gradeLevel") gradeLevel: Int? = null,
        @Query("bookSet") bookSet: String? = null
    ): Response<DictationPassagesResponse>

    // Phase 5: Danh sách lớp học & học sinh
    @GET("api/classes")
    suspend fun getClasses(): Response<ClassesResponse>

    @GET("api/users")
    suspend fun getStudents(
        @Query("role") role: String = "student"
    ): Response<StudentsResponse>

    // Phase 5: Đồng bộ sổ điểm lên máy chủ
    @POST("api/grades")
    suspend fun syncGrade(
        @Body request: ServerGradeSyncRequest
    ): Response<Map<String, Any>>
}
