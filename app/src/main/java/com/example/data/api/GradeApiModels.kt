package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GradeApiRequest(
    @Json(name = "imageBase64") val imageBase64: String,
    @Json(name = "studentGrade") val studentGrade: Int = 3,
    @Json(name = "gradingMode") val gradingMode: String = "dictation",
    @Json(name = "studentName") val studentName: String = "Học sinh",
    @Json(name = "className") val className: String = "",
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

// ============================================================
// DICTATION / KHO NGỮ LIỆU SGK MODELS (Phase 4)
// ============================================================
@JsonClass(generateAdapter = true)
data class DictationPassage(
    @Json(name = "id") val id: String? = null,
    @Json(name = "gradeLevel") val gradeLevel: Int = 3,
    @Json(name = "bookSet") val bookSet: String = "KetNoi",
    @Json(name = "unit") val unit: String = "",
    @Json(name = "title") val title: String,
    @Json(name = "content") val content: String,
    @Json(name = "difficultWords") val difficultWords: String? = null
)

@JsonClass(generateAdapter = true)
data class DictationPassagesResponse(
    @Json(name = "passages") val passages: List<DictationPassage>? = null
)

// ============================================================
// CLASS & STUDENT SYNC MODELS (Phase 5)
// ============================================================
@JsonClass(generateAdapter = true)
data class ClassItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "grade") val grade: Int? = null,
    @Json(name = "studentCount") val studentCount: Int? = 0,
    @Json(name = "teacherName") val teacherName: String? = null
)

@JsonClass(generateAdapter = true)
data class ClassesResponse(
    @Json(name = "classes") val classes: List<ClassItem>? = null
)

@JsonClass(generateAdapter = true)
data class StudentItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "username") val username: String? = null,
    @Json(name = "className") val className: String? = null
)

@JsonClass(generateAdapter = true)
data class StudentsResponse(
    @Json(name = "users") val users: List<StudentItem>? = null
)

@JsonClass(generateAdapter = true)
data class ServerGradeSyncRequest(
    @Json(name = "gradingMode") val gradingMode: String = "dictation",
    @Json(name = "studentName") val studentName: String,
    @Json(name = "assignmentTitle") val assignmentTitle: String,
    @Json(name = "className") val className: String,
    @Json(name = "originalText") val originalText: String,
    @Json(name = "fixedText") val fixedText: String = "",
    @Json(name = "score") val score: String, // e.g. "8.5/10"
    @Json(name = "scoreBreakdown") val scoreBreakdown: String,
    @Json(name = "corrections") val corrections: String = "[]",
    @Json(name = "pedagogicalComment") val pedagogicalComment: String = "",
    @Json(name = "feedback") val feedback: String = ""
)

