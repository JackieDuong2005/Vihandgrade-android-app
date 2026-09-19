# 📱 ĐẶC TẢ TECH STACK & LỘ TRÌNH NÂNG CẤP GIAO DIỆN ANDROID APP
## Hệ Thống Trợ Lý Chấm Điểm & Luyện Viết Tiểu Học — ViHand Grade Native

---

## 🎯 1. TỔNG QUAN VÀ MỤC TIÊU NÂNG CẤP

Tài liệu này xác định toàn bộ **Tech Stack**, **Kiến trúc phần mềm** và **Đặc tả chi tiết các màn hình** nhằm nâng cấp ứng dụng Android Native ([`android_app`](./)) từ phiên bản Demo/MVP hiện tại thành ứng dụng thương mại / thi đấu NCKH hoàn chỉnh, sở hữu **toàn bộ các tính năng tương đương và vượt trội so với hệ thống Web** ([`Web_sua_loi`](../)).

### 🌟 4 Trụ Cột Nâng Cấp Cốt Lõi:
1. **Phần cứng Camera thật (CameraX)**: Chụp ảnh bài thi thật với tự động căn nét (Auto-focus), hỗ trợ đèn Flash và nén ảnh Bitmap tối ưu.
2. **Trực quan hóa Bounding Box trên Ảnh thật (Dual-Mode Canvas)**: Xem trực tiếp bài viết tay của học sinh với các khung đỏ từ mô hình YOLOv8 đè lên chính xác từng nét chữ sai, hỗ trợ chụm 2 ngón tay thu phóng (Pinch-to-zoom).
3. **Kho bài đọc Chính tả SGK Động & Giọng đọc AI**: Đồng bộ hàng trăm bài đọc Sách giáo khoa Lớp 1–5 (Kết Nối Tri Thức, Chân Trời Sáng Tạo, Cánh Diều) và phát âm chuẩn sư phạm qua Edge-TTS.
4. **Báo cáo Sư phạm & Quản trị Lớp học**: Bổ sung màn hình Báo cáo với biểu đồ phân tích lỗi theo chuyên đề (phụ âm đầu, vần, thanh điệu) và quản lý danh sách học sinh.

---

## 🏗️ 2. MA TRẬN TECH STACK TOÀN DIỆN (FULL TECH STACK MATRIX)

Toàn bộ các thư viện dưới đây đều đã được khai báo sẵn trong danh mục quản lý phụ thuộc [`gradle/libs.versions.toml`](./gradle/libs.versions.toml):

```
┌────────────────────────────────────────────────────────────────────────┐
│                   TẦNG TRÌNH DIỄN (PRESENTATION LAYER)                 │
│  • Jetpack Compose BOM 2024.09.00 (Kotlin 2.2)                         │
│  • Material 3 Components & Dynamic Color System                        │
│  • Accompanist Permissions (Camera Runtime Permission)                 │
│  • Coil Compose 2.7.0 (Asynchronous Image Loading & Memory Caching)   │
│  • Canvas DrawScope (Custom Bounding Box Overlays & Vở ô ly)           │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
┌──────────────────────────────────┴─────────────────────────────────────┐
│                   TẦNG PHẦN CỨNG & MEDIA (HARDWARE & MEDIA)            │
│  • CameraX 1.5.0 (camera-core, camera-camera2, camera-lifecycle, view) │
│  • Android MediaPlayer (Stream giọng đọc Edge-TTS chất lượng cao)      │
│  • Android TextToSpeech Native Engine (Dự phòng ngoại tuyến)           │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
┌──────────────────────────────────┴─────────────────────────────────────┐
│                   TẦNG KẾT NỐI MẠNG (NETWORK & API CLIENT)              │
│  • Retrofit 2.12.0 + Moshi Converter 2.12.0 (Kotlin JSON Serialization)│
│  • OkHttp 4.10.0 + HttpLoggingInterceptor (Timeout 60s - 90s)          │
│  • Cloudflare Tunnel Client (HTTPS vihandgrade.click / Local IP Pi)    │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
┌──────────────────────────────────┴─────────────────────────────────────┐
│                   TẦNG DỮ LIỆU CỤC BỘ (LOCAL PERSISTENCE LAYER)        │
│  • Room Database 2.7.0 (SQLite KSP: GradeRecordDao, AppDatabase)       │
│  • DataStore Preferences 1.1.7 (Cấu hình Server URL, Theme, Session)   │
│  • Android Internal Storage (Lưu trữ file ảnh chụp bài thi)            │
└────────────────────────────────────────────────────────────────────────┘
```

### Bảng Chi Tiết Thư Viện & Công Nghệ Cụ Thể:

| Tầng chức năng | Thư viện / Công nghệ | Phiên bản | Mục đích sử dụng |
|---|---|---|---|
| **UI Framework** | `androidx.compose.ui` | Compose BOM | Xây dựng giao diện khai báo (Declarative UI) mượt mà 60–120fps |
| **Design System** | `androidx.compose.material3` | Material 3 | Áp dụng chuẩn thiết kế mới nhất của Google, hỗ trợ Dark/Light Theme |
| **Quản lý Quyền** | `com.google.accompanist:accompanist-permissions` | 0.37.3 | Xin quyền truy cập Camera một cách mượt mà theo đúng chuẩn Android 14/15 |
| **Hiển thị Ảnh** | `io.coil-kt:coil-compose` | 2.7.0 | Hiển thị ảnh chụp bài thi của học sinh kèm cơ chế Cache bộ nhớ đệm |
| **Camera Phần cứng** | `androidx.camera:camera-camera2`, `camera-lifecycle`, `camera-view` | 1.5.0 | Mở luồng máy ảnh trực tiếp (Live Viewfinder), tự động lấy nét, chụp ảnh độ phân giải cao |
| **HTTP & REST API** | `com.squareup.retrofit2:retrofit` | 2.12.0 | Giao tiếp mạng với Backend Next.js qua các Endpoint `/api/mobile/grade`, `/api/classes`... |
| **JSON Parser** | `com.squareup.moshi:moshi-kotlin` | 1.15.2 | Chuyển đổi JSON sang Kotlin Data Classes với hiệu năng cao |
| **Local Database** | `androidx.room:room-runtime`, `room-ktx` | 2.7.0 | Lưu trữ lịch sử bài chấm, danh mục học sinh và bài đọc SGK để xem ngoại tuyến |
| **Xử lý Bất đồng bộ**| `kotlinx.coroutines` | 1.10.2 | Quản lý luồng xử lý IO, nén ảnh ngầm không gây đơ giao diện |
| **Kiến trúc State** | `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.8.7 | Mô hình MVVM với `StateFlow` và `collectAsStateWithLifecycle()` |

---

## 🎨 3. ĐẶC TẢ GIAO DIỆN CHI TIẾT CÁC MÀN HÌNH (UI SPECIFICATIONS)

Ứng dụng sẽ bao gồm **5 Tab chính** trên thanh điều hướng dưới (Bottom Navigation Bar) cùng các màn hình chi tiết:

```
[Tab 1: Tổng quan]   [Tab 2: Báo cáo]   [Tab 3: Chấm bài 📷]   [Tab 4: Chính tả]   [Tab 5: Cài đặt]
```

---

### 📷 MÀN HÌNH 1: `CameraScanScreen.kt` (Máy Ảnh Chụp Bài Thi Thực Tế)

*Nâng cấp từ canvas giả lập hiện tại thành kính ngắm máy ảnh thực thụ qua CameraX.*

#### Các thành phần giao diện bắt buộc:
1. **Live Camera Viewfinder**:
   - Sử dụng `AndroidView { PreviewView(it) }` hiển thị hình ảnh thời gian thực từ cảm biến camera điện thoại.
   - Khung ngắm chữ nhật định hướng khổ giấy A4 / Vở ô ly với 4 góc viền bo tròn màu ngọc lục bảo (`EmeraldPrimary`).
   - Laser quét chuyển động mềm mại (`ScanningLaserOverlay`) tạo hiệu ứng quét tài liệu công nghệ cao.
2. **Thanh Chọn Lớp & Học Sinh Nhanh (Quick Student Bar)**:
   - Đặt ở phía trên kính ngắm camera.
   - Gồm 2 Dropdown/Chip:
     - Chọn Lớp (VD: Lớp 3A1, 4B2...).
     - Chọn Học sinh (VD: Nguyễn Bảo Nam, Trần Mai Chi...).
     - Tùy chọn: *"Chấm ẩn danh"* (dành cho bài thi rọc phách).
3. **Thanh Chọn Chế Độ Chấm (Grading Mode Toggle)**:
   - Hai nút gạt dạng Pill:
     - **Chính tả (Nghe - Viết)**: Barem chuẩn 7đ Chính tả + 3đ Trình bày.
     - **Tập làm văn**: Barem chuẩn 4đ Chính tả + 3đ Hình thức + 2đ Nội dung + 1đ Sáng tạo.
4. **Cụm Phím Điều Khiển Dưới (Bottom Camera Controls)**:
   - **Nút Thư viện (Gallery)**: Chọn ảnh chụp sẵn trong máy.
   - **Nút Chụp trung tâm (Shutter Button)**: Nút tròn nổi 82dp, bấm vào sẽ tự động chớp flash trắng phản hồi, khóa nét và chụp ảnh.
   - **Nút Bật/Tắt Flash (Flash Toggle)**: Trợ sáng khi chụp bài thi ở phòng học thiếu sáng.
   - **Nút Đổi tỉ lệ khung hình**: Chuyển đổi giữa `4:3` (chuẩn chụp tài liệu) và `9:16` (toàn màn hình).

---

### 📝 MÀN HÌNH 2: `GradingResultScreen.kt` & `HandwritingCanvas.kt` (Xem Kết Quả & Chấm Điểm)

*Đây là màn hình cốt lõi nhất của ứng dụng, bổ sung chế độ xem kép và can thiệp điểm sư phạm.*

#### 1. Chế Độ Xem Kép Bài Thi (Dual-View Switcher):
Thanh Tab nhỏ ở đầu bài làm cho phép chuyển đổi tức thì:
* **Chế độ A — [Ảnh Chụp Thật] (Default)**:
  - Tải bức ảnh bài thi thật của học sinh bằng thư viện `Coil`.
  - Phủ một lớp `Canvas` trong suốt lên trên bề mặt ảnh.
  - Lấy danh sách lỗi `errors` từ server, tính toán tọa độ tương đối:
    $$\text{x} = \text{rel\_x1} \times \text{width}, \quad \text{y} = \text{rel\_y1} \times \text{height}, \quad \text{w} = \text{rel\_w} \times \text{width}, \quad \text{h} = \text{rel\_h} \times \text{height}$$
  - Vẽ khung chữ nhật màu đỏ viền nét đôi bao quanh đúng từ học sinh viết sai trên mặt giấy thật.
  - Hỗ trợ chụm 2 ngón tay thu phóng (`detectTransformGestures`) để giáo viên soi rõ từng nét mực.
* **Chế độ B — [Vở Ô Ly Kỹ Thuật Số]**:
  - Giữ lại giao diện giấy kẻ ô ly 4 ly hiện tại với font chữ viết tay tiểu học dành cho người dùng muốn đọc nhanh văn bản đã số hóa.

#### 2. Thẻ Điểm 4 Tiêu Chí Kèm Tính Năng Sửa Điểm (Teacher Override Score):
- Hiển thị 4 thanh tiến trình: Chính tả, Hình thức, Nội dung, Sáng tạo.
- Bấm vào biểu tượng cây bút ✏️ để mở chế độ **Giáo viên can thiệp điểm**:
  - Kéo Slider để tăng/giảm điểm từng tiêu chí theo nhận định sư phạm của thầy/cô.
  - Tổng điểm tự động cập nhật và xếp loại lại (Xuất sắc, Tốt, Khá, Trung bình).

#### 3. Thẻ Nhận Xét Sư Phạm Đa Dạng (Qwen SLM Suggestions):
- Hiển thị 3 thẻ Chip chứa **3 gợi ý lời phê khác nhau từ AI**:
  - *Gợi ý 1: Động viên, khích lệ nỗ lực.*
  - *Gợi ý 2: Tập trung chỉ rõ lỗi cần khắc phục.*
  - *Gợi ý 3: Khen ngợi nét chữ và sáng tạo.*
- Giáo viên chạm để chọn lời phê ưng ý nhất, hoặc bấm vào để tự gõ thêm nhận xét riêng.

#### 4. Thao Tác Lưu & Chia Sẻ:
- **Nút "Lưu vào sổ điểm"**: Gửi kết quả lên server trung tâm qua API `POST /api/grades`.
- **Nút "Chia sẻ / Xuất phiếu điểm"**: Xuất phiếu điểm đẹp mắt dạng ảnh kèm mã QR để gửi cho phụ huynh qua Zalo/Email.

---

### 🎧 MÀN HÌNH 3: `DictationScreen.kt` (Kho Bài Chính Tả SGK & Trợ Lý Đọc AI)

*Chuyển đổi từ 1 bài mẫu cố định thành kho bài học đầy đủ theo chuẩn chương trình GDPT 2018.*

#### Các thành phần giao diện:
1. **Bộ Lọc Sách Giáo Khoa (SGK Filter Chips)**:
   - **Khối lớp**: Chip chọn `Lớp 1` | `Lớp 2` | `Lớp 3` | `Lớp 4` | `Lớp 5`.
   - **Bộ sách**: Chip chọn `Kết Nối Tri Thức` | `Chân Trời Sáng Tạo` | `Cánh Diều`.
   - **Tuần học**: Dropdown chọn từ Tuần 1 đến Tuần 35.
2. **Danh Sách Bài Đọc (Passage List)**:
   - Tải từ API `GET /api/dictation/passages`.
   - Thẻ bài học hiển thị: Tên bài, Thể loại (Thơ / Văn xuôi), Số từ, Mục tiêu rèn luyện (VD: *Rèn phân biệt s/x, d/gi/r*).
3. **Trình Phát Giọng Đọc Sư Phạm (Edge-TTS Player)**:
   - Kết nối trực tiếp với API `/api/dictation/tts`:
     - Tùy chọn giọng đọc: **Cô Hoài My (Nữ miền Bắc)** | **Thầy Nam Minh (Nam miền Bắc)**.
     - Tùy chỉnh tốc độ: `0.75x (Chậm cho Lớp 1-2)` | `0.85x (Chuẩn)` | `1.0x (Nhanh)`.
   - Cơ chế đọc từng câu: Đọc xong 1 câu $\rightarrow$ đếm ngược thời gian nghỉ 5–10 giây cho học sinh viết $\rightarrow$ tự động đọc câu tiếp theo.
   - Tích hợp Android `TextToSpeech` nội bộ làm giải pháp dự phòng ngoại tuyến khi không có mạng.

---

### 📊 MÀN HÌNH 4: `ReportsAnalyticsScreen.kt` (Báo Cáo & Thống Kê Sư Phạm)

*Màn hình MỚI bổ sung vào Tab 2, mang toàn bộ sức mạnh phân tích của Web lên di động.*

#### Các thành phần giao diện:
1. **Bộ Lọc Lớp Học & Thời Gian**:
   - Chọn Lớp cần xem báo cáo.
   - Chọn khoảng thời gian: *Tuần này* | *Tháng này* | *Học kỳ 1* | *Cả năm*.
2. **Thẻ Thống Kê Tổng Quan**:
   - Điểm trung bình cả lớp (kèm chỉ số so sánh tăng/giảm so với tuần trước).
   - Tổng số bài đã chấm.
   - Tỉ lệ phân loại học sinh (Xuất sắc / Tốt / Khá / Cần cố gắng).
3. **Biểu Đồ Phân Tích Dạng Lỗi Hay Sai (Error Distribution Chart)**:
   - Vẽ bằng Compose Canvas trực tiếp (không cần thư viện nặng):
     - Lỗi phụ âm đầu ($s/x, tr/ch, d/gi/r, l/n$): chiếm bao nhiêu %.
     - Lỗi vần ($an/ang, uôn/uông$): chiếm bao nhiêu %.
     - Lỗi dấu thanh (Hỏi / Ngã): chiếm bao nhiêu %.
     - Lỗi viết hoa đầu dòng / tên riêng: chiếm bao nhiêu %.
4. **Danh Sách Học Sinh Cần Rèn Thêm**:
   - Liệt kê các em học sinh có điểm chính tả dưới 6.5 cùng dạng lỗi thường xuyên mắc phải nhất để giáo viên có kế hoạch phụ đạo.
5. **Nút Xuất Báo Cáo Excel/CSV**:
   - Tạo file bảng điểm tổng hợp để gửi Ban Giám hiệu nhà trường.

---

### ⚙️ MÀN HÌNH 5: `ServerSettingsScreen.kt` & Đăng Nhập (Quản Trị Kết Nối)

*Nâng cấp màn hình Trạm Pi hiện có.*

1. **Cấu hình Trạm Server**:
   - Nhập URL: Hỗ trợ Cloudflare (`https://vihandgrade.click`) hoặc IP nội bộ Pi (`http://192.168.43.xxx:3000`).
   - Nút **Ping Test**: Hiển thị trạng thái trạm máy chủ xanh/đỏ kèm thời gian phản hồi (ms).
2. **Thông Tin Đăng Nhập Giáo Viên**:
   - Hiển thị tên giáo viên đang đăng nhập, trường học, danh sách lớp phụ trách.
   - Nút **Đồng bộ hóa dữ liệu**: Đồng bộ toàn bộ các bài chấm lưu tạm khi offline lên CSDL trường học khi vừa có mạng trở lại.

---

## 🔌 4. ĐẶC TẢ API GATEWAY CHO MOBILE (`/api/mobile/grade`)

Để điện thoại không phải gửi ảnh nhiều lần, Server Next.js sẽ cung cấp một **Endpoint hợp nhất (BFF Gateway)**:

### Request: `POST /api/mobile/grade`
```json
{
  "imageBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRg...",
  "studentGrade": 3,
  "gradingMode": "dictation",
  "studentId": "std_101",
  "classId": "cls_3A",
  "hinh_thuc": 3.0,
  "noi_dung": 2.0,
  "penalty_per_error": 0.5
}
```

### Response JSON Chuẩn Hóa Cho Android:
```json
{
  "status": "success",
  "essayTitle": "Quạt cho bà ngủ",
  "studentName": "Nguyễn Bảo Nam",
  "className": "Lớp 3A1",
  "criteria": {
    "spellingScore": 2.5,
    "formatScore": 3.0,
    "contentScore": 2.0,
    "creativityScore": 0.8,
    "totalScore": 8.3
  },
  "pedagogicalComment": "Con có ý thức rèn chữ tốt, chữ viết đều nét. Chú ý phân biệt âm đầu ch/tr để bài viết hoàn thiện hơn!",
  "pedagogicalComments": [
    "Con có ý thức rèn chữ tốt, chữ viết đều nét. Chú ý phân biệt âm đầu ch/tr để bài viết hoàn thiện hơn!",
    "Bài viết sạch đẹp, trình bày đúng lề ô ly. Cần chú ý thêm dấu thanh hỏi/ngã nhé con.",
    "Cô khen con đã hoàn thành bài viết đúng tiến độ. Tiếp tục phát huy nét chữ thanh thoát này nhé!"
  ],
  "extractedText": "Ơi chích chòe ơi! Chim đừng hót nữa, Bà em ốm rồi...",
  "correctedFullText": "Ơi chích chòe ơi! Chim đừng hót nữa, Bà em ốm rồi...",
  "errors": [
    {
      "id": "err_0",
      "originalWord": "chổ hoa",
      "correctedWord": "trổ hoa",
      "errorType": "phu_am_dau",
      "explanation": "Viết đúng chính tả: 'trổ hoa' viết bằng âm đầu 'tr', không viết bằng 'ch'.",
      "penalty": 0.5,
      "lineNumber": 2,
      "rel_x1": 0.42,
      "rel_y1": 0.35,
      "rel_w": 0.18,
      "rel_h": 0.06
    }
  ],
  "processingTimeMs": 3420,
  "serverSource": "Raspberry Pi 4 ARM64 (Next.js + YOLOv8 + ViT5)"
}
```

---

## 🚀 5. KẾ HOẠCH TRIỂN KHAI 6 BƯỚC (STEP-BY-STEP ACTION PLAN)

```
┌────────────────────────────────────────────────────────────────────────┐
│  BƯỚC 1: Mở khóa Dependencies trong libs.versions.toml & build.gradle  │
│  Bật CameraX, Accompanist Permissions, OkHttp Timeout 60s             │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
┌──────────────────────────────────┴─────────────────────────────────────┐
│  BƯỚC 2: Viết Endpoint /api/mobile/grade trên Next.js Server           │
│  Gom Gemini OCR + YOLOv8 + Chấm điểm vào 1 request duy nhất           │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
┌──────────────────────────────────┴─────────────────────────────────────┐
│  BƯỚC 3: Nâng cấp CameraScanScreen.kt tích hợp CameraX Thật            │
│  Mở PreviewView, Auto-focus, chụp ảnh thật và nén Bitmap chuẩn        │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
┌──────────────────────────────────┴─────────────────────────────────────┐
│  BƯỚC 4: Nâng cấp HandwritingCanvas.kt & GradingResultScreen.kt        │
│  Vẽ Bounding Box đè lên ảnh thật, thêm thanh sửa điểm và chọn nhận xét│
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
┌──────────────────────────────────┴─────────────────────────────────────┐
│  BƯỚC 5: Nâng cấp DictationScreen.kt kết nối API SGK Lớp 1-5           │
│  Tải bài đọc SGK từ backend, tích hợp Edge-TTS giọng đọc chuẩn        │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
┌──────────────────────────────────┴─────────────────────────────────────┐
│  BƯỚC 6: Bổ sung Màn hình Báo cáo Sư phạm & Đóng gói Release APK       │
│  Vẽ biểu đồ phân tích dạng lỗi, xuất bảng điểm và build APK tối ưu    │
└────────────────────────────────────────────────────────────────────────┘
```

---
*Tài liệu được thiết kế riêng cho hệ sinh thái ViHand Grade — Sẵn sàng cho việc thực thi code.*
