package com.example.data.repository

import com.example.data.api.DictationPassage

/**
 * Kho ngữ liệu bài đọc chính tả mẫu chuẩn SGK Tiếng Việt Tiểu học (Lớp 1 đến 5)
 * Hỗ trợ các bộ sách hiện hành: Kết Nối Tri Thức, Cánh Diều, Chân Trời Sáng Tạo.
 * Đóng vai trò Offline Fallback khi thiết bị không có mạng hoặc máy chủ chưa sẵn sàng.
 */
object LocalDictationPassages {

    val allPassages: List<DictationPassage> = listOf(
        // ==========================================
        // LỚP 1
        // ==========================================
        DictationPassage(
            id = "sgk_1_1",
            gradeLevel = 1,
            bookSet = "KetNoi",
            unit = "Tuần 28",
            title = "Đi học",
            content = "Hôm qua em tới trường\nMẹ dắt tay từng bước\nHôm nay mẹ lên nương\nMột mình em tới lớp\nĐường xa quá là xa\nNhưng có bạn cùng đi.",
            difficultWords = "trường, dắt tay, từng bước, lên nương, đường xa"
        ),
        DictationPassage(
            id = "sgk_1_2",
            gradeLevel = 1,
            bookSet = "CanhDieu",
            unit = "Tuần 30",
            title = "Cái bống",
            content = "Cái bống là cái bống bang\nKhéo sảy khéo sàng cho mẹ nấu cơm\nMột mình gánh nặng đường trơn\nBống đi gánh đỡ còng lưng cho bà.",
            difficultWords = "bống bang, khéo sảy, khéo sàng, gánh nặng, đường trơn"
        ),
        DictationPassage(
            id = "sgk_1_3",
            gradeLevel = 1,
            bookSet = "ChanTroi",
            unit = "Tuần 32",
            title = "Mẹ của em",
            content = "Mẹ em là cô giáo\nHiền lành và dịu dàng\nMỗi ngày bên trang sách\nUốn từng nét chữ ngoan.",
            difficultWords = "cô giáo, dịu dàng, trang sách, nét chữ ngoan"
        ),

        // ==========================================
        // LỚP 2
        // ==========================================
        DictationPassage(
            id = "sgk_2_1",
            gradeLevel = 2,
            bookSet = "KetNoi",
            unit = "Tuần 5",
            title = "Bà em ốm rồi",
            content = "Ơi chích chòe ơi! Chim đừng hót nữa,\nBà em ốm rồi, Lặng nghe bà ngủ.\nBàn tay bé nhỏ, Vẫy quạt thật đều,\nNgấn nắng thiu thiu, Đậu trên tường trắng.\nCăn nhà đã vắng, Cốc chén nằm im,\nĐôi mắt lim dim, Ngủ ngon bà nhé!",
            difficultWords = "chích chòe, hót nữa, vẫy quạt, thiu thiu, lim dim"
        ),
        DictationPassage(
            id = "sgk_2_2",
            gradeLevel = 2,
            bookSet = "CanhDieu",
            unit = "Tuần 12",
            title = "Làm việc thật là vui",
            content = "Quanh ta, mọi vật, mọi người đều làm việc. Cái đồng hồ tích tắc báo phút, báo giờ. Con gà trống gáy vang te te báo trời sáng. Tu hú kêu tu hú, báo mùa vải chín. Chim sâu bắt sâu bảo vệ mùa màng.",
            difficultWords = "tích tắc, gà trống, te te, tu hú, bảo vệ mùa màng"
        ),
        DictationPassage(
            id = "sgk_2_3",
            gradeLevel = 2,
            bookSet = "ChanTroi",
            unit = "Tuần 18",
            title = "Bé Mai đã lớn",
            content = "Mai say sưa giúp mẹ nhặt rau, quét dọn bàn ghế. Bố mỉm cười bảo: Bé Mai hôm nay đã ra dáng một cô bé ngoan ngoãn, biết thương bố mẹ và phụ giúp việc nhà.",
            difficultWords = "say sưa, nhặt rau, quét dọn, mỉm cười, ngoan ngoãn"
        ),

        // ==========================================
        // LỚP 3
        // ==========================================
        DictationPassage(
            id = "sgk_3_1",
            gradeLevel = 3,
            bookSet = "KetNoi",
            unit = "Tuần 8",
            title = "Chiếc áo búp bê",
            content = "Búp bê của tôi có một chiếc áo choàng thật đẹp. Áo làm bằng vải hoa nhỏ màu hồng đào, viền đăng ten trắng muốt. Trên ngực áo có đính ba chiếc cúc tròn xoe như những hạt ngọc.",
            difficultWords = "búp bê, áo choàng, đăng ten, trắng muốt, cúc tròn xoe"
        ),
        DictationPassage(
            id = "sgk_3_2",
            gradeLevel = 3,
            bookSet = "CanhDieu",
            unit = "Tuần 15",
            title = "Mùa thảo quả",
            content = "Gió tây lướt thướt bay qua rừng, quyến hương thảo quả đi, rải theo triền núi, đưa hương thảo quả ngọt lựng, thơm nồng vào những thôn xóm Đản Khao. Cây thảo quả chín đỏ rực, từng chùm sum sê đượm sắc.",
            difficultWords = "lướt thướt, rải theo, ngọt lựng, thơm nồng, sum sê"
        ),
        DictationPassage(
            id = "sgk_3_3",
            gradeLevel = 3,
            bookSet = "ChanTroi",
            unit = "Tuần 22",
            title = "Mưa đá",
            content = "Mưa đá bất ngờ đổ xuống bản làng sau cơn giông lớn. Những viên đá tròn xoe, trong veo lăn rào rào trên mái ngói. Lũ trẻ thích thú reo hò, nhặt từng nắm đá mát rượi trong lòng bàn tay.",
            difficultWords = "mưa đá, rào rào, mái ngói, thích thú, mát rượi"
        ),

        // ==========================================
        // LỚP 4
        // ==========================================
        DictationPassage(
            id = "sgk_4_1",
            gradeLevel = 4,
            bookSet = "KetNoi",
            unit = "Tuần 1",
            title = "Dế Mèn bênh vực kẻ yếu",
            content = "Một hôm, qua một vùng cỏ xước xanh dài, tôi chợt nghe tiếng khóc tỉ tê. Đi vài bước nữa, tôi gặp chị Nhà Trò ngồi gục đầu bên tảng đá cuội. Chị mặc áo thâm dài, đôi cánh mỏng manh như cánh bướm non.",
            difficultWords = "cỏ xước, tỉ tê, Nhà Trò, gục đầu, đá cuội, mỏng manh"
        ),
        DictationPassage(
            id = "sgk_4_2",
            gradeLevel = 4,
            bookSet = "CanhDieu",
            unit = "Tuần 10",
            title = "Truyện cổ nước mình",
            content = "Tôi yêu truyện cổ nước tôi\nVừa nhân hậu lại tuyệt vời sâu xa\nThương người rồi mới thương ta\nYêu nhau dù mấy cách xa cũng tìm\nỞ hiền thì lại gặp hiền\nNgười ngay thì gặp người tiên độ trì.",
            difficultWords = "truyện cổ, nhân hậu, tuyệt vời, sâu xa, độ trì"
        ),
        DictationPassage(
            id = "sgk_4_3",
            gradeLevel = 4,
            bookSet = "ChanTroi",
            unit = "Tuần 16",
            title = "Cánh diều tuổi thơ",
            content = "Tuổi thơ của tôi được nâng lên từ những cánh diều. Chiều chiều, trên bãi cỏ ven sông, lũ trẻ chúng tôi thả những cánh diều sáo vút cao. Tiếng sáo diều vi vu trầm bổng làm êm ả cả một góc trời chiều quê hương.",
            difficultWords = "tuổi thơ, thả diều, sáo vút, vi vu, trầm bổng, êm ả"
        ),

        // ==========================================
        // LỚP 5
        // ==========================================
        DictationPassage(
            id = "sgk_5_1",
            gradeLevel = 5,
            bookSet = "KetNoi",
            unit = "Tuần 1",
            title = "Quang cảnh làng mạc ngày mùa",
            content = "Mùa đông, giữa ngày mùa, làng quê toàn màu vàng, những màu vàng rất khác nhau. Lúa chín rộ dưới đồng vàng xuộm. Nắng nhạt màu vàng hoe. Vàng lịm là màu trái xoan. Màu vàng ối của những chùm chuỗi quả bưởi chín.",
            difficultWords = "làng mạc, vàng xuộm, vàng hoe, vàng lịm, trái xoan, vàng ối"
        ),
        DictationPassage(
            id = "sgk_5_2",
            gradeLevel = 5,
            bookSet = "CanhDieu",
            unit = "Tuần 7",
            title = "Hạt gạo làng ta",
            content = "Hạt gạo làng ta\nCó vị phù sa\nCủa sông Kinh Thầy\nCó hương sen thơm\nTrong hồ nước đầy\nCó lời mẹ hát\nNgọt bùi đắng cay.",
            difficultWords = "hạt gạo, phù sa, Kinh Thầy, hương sen, ngọt bùi"
        ),
        DictationPassage(
            id = "sgk_5_3",
            gradeLevel = 5,
            bookSet = "ChanTroi",
            unit = "Tuần 14",
            title = "Kì diệu rừng xanh",
            content = "Loanh quanh trong rừng, chúng tôi bắt gặp một lối đi đầy nấm dại. Những chiếc nấm to bằng cái ô con, màu sắc sặc sỡ như những kiến trúc tí hon. Ánh nắng rọi qua kẽ lá tạo nên những dải sáng lung linh kỳ ảo.",
            difficultWords = "loanh quanh, nấm dại, sặc sỡ, kiến trúc, tí hon, lung linh"
        )
    )

    fun getPassagesByGrade(grade: Int): List<DictationPassage> {
        return allPassages.filter { it.gradeLevel == grade }
    }

    fun getPassagesByGradeAndBookSet(grade: Int, bookSet: String?): List<DictationPassage> {
        return allPassages.filter {
            it.gradeLevel == grade && (bookSet.isNullOrBlank() || it.bookSet.equals(bookSet, ignoreCase = true))
        }
    }
}
