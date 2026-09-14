package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradeApiRequest(
    @Json(name = "imageBase64") val imageBase64: String,
    @Json(name = "studentGrade") val studentGrade: Int = 3,
    @Json(name = "essayType") val essayType: String = "spelling"
)

@JsonClass(generateAdapter = true)
data class ApiErrorBox(
    @Json(name = "id") val id: String? = null,
    @Json(name = "originalWord") val originalWord: String,
    @Json(name = "correctedWord") val correctedWord: String,
    @Json(name = "errorType") val errorType: String,
    @Json(name = "explanation") val explanation: String,
    @Json(name = "penalty") val penalty: Float = 0.5f,
    @Json(name = "x1") val x1: Float = 0f,
    @Json(name = "y1") val y1: Float = 0f,
    @Json(name = "x2") val x2: Float = 0f,
    @Json(name = "y2") val y2: Float = 0f,
    @Json(name = "lineNumber") val lineNumber: Int = 1
)

@JsonClass(generateAdapter = true)
data class ApiCriteria(
    @Json(name = "spellingScore") val spellingScore: Float,
    @Json(name = "formatScore") val formatScore: Float,
    @Json(name = "contentScore") val contentScore: Float,
    @Json(name = "creativityScore") val creativityScore: Float,
    @Json(name = "totalScore") val totalScore: Float
)

@JsonClass(generateAdapter = true)
data class GradeApiResponse(
    @Json(name = "status") val status: String? = "success",
    @Json(name = "essayTitle") val essayTitle: String? = null,
    @Json(name = "studentName") val studentName: String? = null,
    @Json(name = "criteria") val criteria: ApiCriteria? = null,
    @Json(name = "pedagogicalComment") val pedagogicalComment: String? = null,
    @Json(name = "extractedText") val extractedText: String? = null,
    @Json(name = "correctedFullText") val correctedFullText: String? = null,
    @Json(name = "errors") val errors: List<ApiErrorBox>? = null,
    @Json(name = "processingTimeMs") val processingTimeMs: Long? = null,
    @Json(name = "serverSource") val serverSource: String? = null
)
