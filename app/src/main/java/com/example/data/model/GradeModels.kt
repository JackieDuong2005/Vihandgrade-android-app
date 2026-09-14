package com.example.data.model

data class ErrorBox(
    val id: String,
    val originalWord: String,
    val correctedWord: String,
    val errorType: String,
    val explanation: String,
    val penalty: Float,
    val x1: Float, // Normalized coordinate 0.0 .. 1.0
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val lineNumber: Int = 1
)

data class GradeCriteria(
    val spellingScore: Float,    // Max 4.0 (Tiêu chí Chính tả)
    val formatScore: Float,      // Max 3.0 (Tiêu chí Hình thức & Vở sạch chữ đẹp)
    val contentScore: Float,     // Max 2.0 (Tiêu chí Nội dung & Ngữ pháp)
    val creativityScore: Float,  // Max 1.0 (Tiêu chí Sáng tạo & Cảm xúc)
    val totalScore: Float        // Thang điểm 10.0
) {
    val ratingLevel: String
        get() = when {
            totalScore >= 9.0f -> "Hoàn thành Xuất sắc"
            totalScore >= 8.0f -> "Hoàn thành Tốt"
            totalScore >= 6.5f -> "Hoàn thành"
            else -> "Cần cố gắng rèn luyện"
        }

    val gradeBadge: String
        get() = when {
            totalScore >= 9.0f -> "A+"
            totalScore >= 8.0f -> "A"
            totalScore >= 6.5f -> "B"
            else -> "C"
        }
}

data class GradeResult(
    val id: String,
    val timestamp: Long,
    val studentName: String = "Học sinh Tiểu học",
    val className: String = "Lớp 3A",
    val essayTitle: String,
    val criteria: GradeCriteria,
    val pedagogicalComment: String,
    val extractedText: String,
    val correctedFullText: String,
    val errors: List<ErrorBox>,
    val processingTimeMs: Long = 1200L,
    val serverSource: String = "Raspberry Pi Server (Cloudflare)",
    val sampleImageResId: Int? = null,
    val isSample: Boolean = false,
    val sampleType: String? = null
)
