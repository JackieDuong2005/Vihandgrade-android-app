package com.example.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GradeApiService {
    @POST("api/grade")
    suspend fun submitForGrading(
        @Body request: GradeApiRequest
    ): Response<GradeApiResponse>

    @GET("api/health")
    suspend fun checkHealth(): Response<Map<String, Any>>
}
