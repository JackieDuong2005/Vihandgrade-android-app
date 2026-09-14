package com.example.data.repository

import com.example.data.model.ErrorBox
import com.example.data.model.GradeCriteria
import com.example.data.model.GradeResult

object SampleEssays {

    val sample1Eureka = GradeResult(
        id = "sample_eureka_1",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 15,
        studentName = "Nguyễn Bảo Nam",
        className = "Lớp 3A2 - Trường Tiểu học Chu Văn An",
        essayTitle = "Chính tả (Nghe - Viết): Tiếng Chim Buổi Sáng",
        criteria = GradeCriteria(
            spellingScore = 2.0f,     // Trừ 2.0đ do 3 lỗi chính tả
            formatScore = 2.5f,       // Trừ 0.5đ căn lề ô ly
            contentScore = 1.8f,      // Đoạn văn mạch lạc
            creativityScore = 0.7f,   // Diễn đạt tốt
            totalScore = 7.0f
        ),
        pedagogicalComment = "Em viết bài tương đối đều nét, trình bày sạch sẽ. Cần đặc biệt chú ý quy tắc phân biệt âm đầu ch/tr và thanh hỏi/ngã (chổ hoa -> trổ hoa, giửa trời -> giữa trời). Khuyên em luyện đọc thêm bài tập đọc hàng ngày để rèn từ láy chuẩn xác!",
        extractedText = """
Buổi sáng mùa thu thật mát mẻ.
Trên cành cây bàng, chim hót ríu rít.
Cây bàng ngoài sân trường đang chổ hoa.
Đàn chim vỗ cánh bay lượn giửa trời xanh thắm.
""".trimIndent(),
        correctedFullText = """
Buổi sáng mùa thu thật mát mẻ.
Trên cành cây bàng, chim hót ríu rít.
Cây bàng ngoài sân trường đang trổ hoa.
Đàn chim vỗ cánh bay lượn giữa trời xanh thắm.
""".trimIndent(),
        errors = listOf(
            ErrorBox(
                id = "err_1",
                originalWord = "chổ hoa",
                correctedWord = "trổ hoa",
                errorType = "Phụ âm đầu (ch/tr)",
                explanation = "Quy tắc chính tả: 'Trổ hoa' / 'trổ tài' viết bằng âm đầu 'tr', không viết bằng 'ch'.",
                penalty = -1.0f,
                x1 = 0.48f,
                y1 = 0.52f,
                x2 = 0.72f,
                y2 = 0.61f,
                lineNumber = 3
            ),
            ErrorBox(
                id = "err_2",
                originalWord = "giửa trời",
                correctedWord = "giữa trời",
                errorType = "Thanh điệu (Hỏi / Ngã)",
                explanation = "Quy tắc dấu thanh: 'Giữa' mang thanh Ngã (~), chỉ vị trí trung tâm (ở giữa, giữa trưa, giữa trời).",
                penalty = -1.0f,
                x1 = 0.55f,
                y1 = 0.68f,
                x2 = 0.82f,
                y2 = 0.78f,
                lineNumber = 4
            )
        ),
        processingTimeMs = 1240L,
        serverSource = "Raspberry Pi 4 ARM64 (Cloudflare Tunnel :3000)",
        isSample = true,
        sampleType = "eureka_demo"
    )

    val sample2Excellence = GradeResult(
        id = "sample_excellence_2",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 2,
        studentName = "Trần Mai Chi",
        className = "Lớp 4B - Giải Vở Sạch Chữ Đẹp",
        essayTitle = "Tập làm văn: Mái Trường Em Yêu",
        criteria = GradeCriteria(
            spellingScore = 4.0f,     // Chuẩn xác 100%
            formatScore = 3.0f,       // Nét thanh nét đậm chuẩn ô ly
            contentScore = 2.0f,      // Bố cục mở - thân - kết rõ ràng
            creativityScore = 0.9f,   // Giàu hình ảnh nhân hóa
            totalScore = 9.9f
        ),
        pedagogicalComment = "Bài viết xuất sắc! Nét chữ thanh thoát, giữ vở sạch đẹp đúng chuẩn chữ viết tiểu học. Câu văn giàu cảm xúc, sử dụng từ ngữ gợi cảm và biện pháp so sánh rất tự nhiên. Thầy cô khen ngợi em!",
        extractedText = "Dưới ánh nắng ban mai rực rỡ, mái trường thân yêu của em hiện lên thật khang trang và tươi đẹp. Những hàng cây phượng vĩ xanh rì rì rào reo vui trong gió như đón chào chúng em mỗi sớm mai đến lớp.",
        correctedFullText = "Dưới ánh nắng ban mai rực rỡ, mái trường thân yêu của em hiện lên thật khang trang và tươi đẹp. Những hàng cây phượng vĩ xanh rì rì rào reo vui trong gió như đón chào chúng em mỗi sớm mai đến lớp.",
        errors = emptyList(),
        processingTimeMs = 980L,
        serverSource = "Raspberry Pi 4 ARM64 (Cloudflare Tunnel :3000)",
        isSample = true,
        sampleType = "excellence_demo"
    )

    val sample3DialectMistakes = GradeResult(
        id = "sample_dialect_3",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 60 * 5,
        studentName = "Lê Hoàng Khôi",
        className = "Lớp 2C - Luyện viết chính tả",
        essayTitle = "Chính tả: Cảnh Đồng Quê Mùa Gặt",
        criteria = GradeCriteria(
            spellingScore = 2.5f,     // Trừ 1.5đ lỗi s/x và l/n
            formatScore = 2.0f,       // Nét chữ còn nguệch ngoạc
            contentScore = 1.5f,      // Ý văn tương đối
            creativityScore = 0.5f,
            totalScore = 6.5f
        ),
        pedagogicalComment = "Em đã viết đủ số câu theo yêu cầu. Tuy nhiên cần rèn thêm cách cầm bút để giữ nét chữ ngay ngắn trên đường kẻ ô ly. Lưu ý phân biệt âm s/x và l/n khi viết (sớm mai, lẩn nấp). Em cố gắng lên nhé!",
        extractedText = "Mùa gặt về, cánh đồng lúa chín vàng rực. Bác nông dân thức dậy từ xớm mai để ra đồng. Chú cào cào xanh nấp sâu dưới kẽ lá sen.",
        correctedFullText = "Mùa gặt về, cánh đồng lúa chín vàng rực. Bác nông dân thức dậy từ sớm mai để ra đồng. Chú cào cào xanh nấp sâu dưới kẽ lá sen.",
        errors = listOf(
            ErrorBox(
                id = "err_d1",
                originalWord = "xớm mai",
                correctedWord = "sớm mai",
                errorType = "Phụ âm đầu (s/x)",
                explanation = "'Sớm mai', 'buổi sớm' viết bằng 's' nhẹ quặt (s), không viết 'x'.",
                penalty = -0.5f,
                x1 = 0.44f,
                y1 = 0.45f,
                x2 = 0.65f,
                y2 = 0.55f,
                lineNumber = 2
            ),
            ErrorBox(
                id = "err_d2",
                originalWord = "sâu dưới",
                correctedWord = "sâu dưới (dùng từ)",
                errorType = "Cấu trúc ngữ nghĩa",
                explanation = "Từ 'sâu' ở đây học sinh viết lẫn lộn giữa con sâu và tính từ ẩn sâu. Cần tách rõ 'ẩn nấp dưới kẽ lá'.",
                penalty = -0.5f,
                x1 = 0.52f,
                y1 = 0.70f,
                x2 = 0.76f,
                y2 = 0.79f,
                lineNumber = 3
            )
        ),
        processingTimeMs = 1110L,
        serverSource = "Raspberry Pi 4 ARM64 (Cloudflare Tunnel :3000)",
        isSample = true,
        sampleType = "dialect_demo"
    )

    val sample2Good = GradeResult(
        id = "sample_good_2",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 2,
        studentName = "Nguyễn Văn An",
        className = "Lớp 3A1 - TH Chu Văn An",
        essayTitle = "Bài thơ: Quạt Cho Bà Ngủ",
        criteria = GradeCriteria(
            spellingScore = 3.5f,     // -0.5đ (2 lỗi chính tả)
            formatScore = 2.8f,       // Thẳng hàng ô ly
            contentScore = 2.0f,      // Đủ bài
            creativityScore = 0.9f,   // Nét mềm
            totalScore = 9.2f
        ),
        pedagogicalComment = "Em viết chữ ngay ngắn, sạch đẹp và đúng cự ly dòng kẻ ô ly. Em cần chú ý rèn thêm quy tắc viết hoa chữ cái đầu mỗi dòng thơ và phân biệt âm r/s nhé!",
        extractedText = """
Ơi chích chòe ơi!
Chim đừng hót nữa,
Bà em ốm rồi,
Lặng nghe bà ngủ.
Bàn tay bé nhỏ,
Vẫy quạt thật đều,
su bé ngủ xay,
Đậu chên tường trắng.
""".trimIndent(),
        correctedFullText = """
Ơi chích chòe ơi!
Chim đừng hót nữa,
Bà em ốm rồi,
Lặng nghe bà ngủ.
Bàn tay bé nhỏ,
Vẫy quạt thật đều,
Ru bà ngủ say,
Đậu trên tường trắng.
""".trimIndent(),
        errors = listOf(
            ErrorBox(
                id = "err_good_1",
                originalWord = "su bé ngủ xay",
                correctedWord = "Ru bà ngủ say",
                errorType = "Phụ âm đầu & vần (r/s, ay/ey)",
                explanation = "Dòng thơ bắt buộc viết hoa chữ cái đầu (\"Ru bà\"). Từ \"say\" viết bằng âm s nhẹ, không viết \"xay\" (xay lúa).",
                penalty = -0.3f,
                x1 = 0.20f,
                y1 = 0.65f,
                x2 = 0.70f,
                y2 = 0.76f,
                lineNumber = 7
            ),
            ErrorBox(
                id = "err_good_2",
                originalWord = "Đậu chên",
                correctedWord = "Đậu trên",
                errorType = "Phụ âm đầu (ch/tr)",
                explanation = "Quy tắc chính tả: 'Đậu trên cành/tường' viết bằng âm đầu 'tr', không viết 'ch'.",
                penalty = -0.2f,
                x1 = 0.20f,
                y1 = 0.77f,
                x2 = 0.55f,
                y2 = 0.88f,
                lineNumber = 8
            )
        ),
        processingTimeMs = 1040L,
        serverSource = "Raspberry Pi 4 ARM64 (Cloudflare Tunnel :3000)",
        isSample = true,
        sampleType = "good_demo"
    )

    val sample3Spelling = GradeResult(
        id = "sample_spelling_3",
        timestamp = System.currentTimeMillis() - 1000L * 60 * 15,
        studentName = "Trần Mai Hoa",
        className = "Lớp 3A1",
        essayTitle = "Chính tả: Người Mẹ",
        criteria = GradeCriteria(
            spellingScore = 3.5f,
            formatScore = 2.5f,
            contentScore = 1.8f,
            creativityScore = 0.7f,
            totalScore = 8.5f
        ),
        pedagogicalComment = "Bài viết đầy đủ, chữ viết đều và sạch sẽ. Cần lưu ý các dấu thanh hỏi ngã.",
        extractedText = "Ở một gia đình kia, có một người mẹ trẻ chăm sóc đứa con thơ ốm nặng. Đêm đông lạnh buốt...",
        correctedFullText = "Ở một gia đình kia, có một người mẹ trẻ chăm sóc đứa con thơ ốm nặng. Đêm đông lạnh buốt...",
        errors = listOf(
            ErrorBox(
                id = "err_spelling_1",
                originalWord = "lạnh buốt",
                correctedWord = "lạnh buốt",
                errorType = "Vần & Dấu thanh",
                explanation = "Từ láy mô tả rét buốt, cần chú ý phụ âm cuối.",
                penalty = -0.5f,
                x1 = 0.35f,
                y1 = 0.45f,
                x2 = 0.65f,
                y2 = 0.55f,
                lineNumber = 2
            )
        ),
        processingTimeMs = 980L,
        serverSource = "Raspberry Pi 4 ARM64 (Cloudflare Tunnel :3000)",
        isSample = true,
        sampleType = "spelling_demo"
    )

    val allSamples = listOf(sample2Good, sample1Eureka, sample3Spelling, sample2Excellence, sample3DialectMistakes)
}
