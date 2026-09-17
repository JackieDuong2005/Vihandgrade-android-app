package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradeApiRequest(
    @Json(name = "imageBase64") val imageBase64: String,
    @Json(name = "studentGrade") val studentGrade: Int = 3,
    @Json(name = "gradingMode") val gradingMode: String = "dictation",
    @Json(name = "studentId") val studentId: String? = null,
    @Json(name = "classId") val classId: String? = null,
    @Json(name = "hinh_thuc") val hinhThuc: Float = 3.0f,
    @Json(name = "noi_dung") val noiDung: Float = 2.0f,
    @Json(name = "penalty_per_error") val penaltyPerError: Float = 0.5f,
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
    @Json(name = "lineNumber") val lineNumber: Int = 1,
    @Json(name = "rel_x1") val relX1: Float? = null,
    @Json(name = "rel_y1") val relY1: Float? = null,
    @Json(name = "rel_w") val relW: Float? = null,
    @Json(name = "rel_h") val relH: Float? = null
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
    @Json(name = "className") val className: String? = null,
    @Json(name = "criteria") val criteria: ApiCriteria? = null,
    @Json(name = "pedagogicalComment") val pedagogicalComment: String? = null,
    @Json(name = "pedagogicalComments") val pedagogicalComments: List<String>? = null,
    @Json(name = "extractedText") val extractedText: String? = null,
    @Json(name = "correctedFullText") val correctedFullText: String? = null,
    @Json(name = "errors") val errors: List<ApiErrorBox>? = null,
    @Json(name = "processingTimeMs") val processingTimeMs: Long? = null,
    @Json(name = "serverSource") val serverSource: String? = null
)
