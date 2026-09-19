# 📑 TÀI LIỆU ĐẶC TẢ KỸ THUẬT HỆ THỐNG (SOFTWARE TECHNICAL SPECIFICATION)
## ỨNG DỤNG DI ĐỘNG NATIVE VIHAND GRADE (ANDROID CLIENT)
### Hệ Thống Điện Toán Biên Hỗ Trợ Chấm Điểm Chữ Viết Tay & Luyện Viết Tiểu Học

---

| **Thuộc tính** | **Nội dung** |
|---|---|
| **Tên dự án** | **ViHand Grade Mobile Native** |
| **Mã dự án** | `android_app` (Thuộc hệ sinh thái ViHand Grade) |
| **Nền tảng mục tiêu** | Android (Kotlin / Jetpack Compose) |
| **Tiêu chuẩn thiết kế** | Material 3 Design System (Google MAD - Modern Android Development) |
| **Mô hình vận hành** | Mobile Edge Computing (Client ── 5G/Cloudflare ── Raspberry Pi / Cloud) |
| **Phiên bản tài liệu** | 2.0 (Phiên bản Hoàn thiện & Thi đấu Giải thưởng NCKH Euréka) |
| **Ngày ban hành** | Tháng 9 / 2026 |

---

## 📌 MỤC LỤC

1. [TỔNG QUAN HỆ THỐNG & MỤC TIÊU KỸ THUẬT](#1-tổng-quan-hệ-thống--mục-tiêu-kỹ-thuật)
2. [KIẾN TRÚC PHẦN MỀM & LUỒNG DỮ LIỆU (ARCHITECTURE)](#2-kiến-trúc-phần-mềm--luồng-dữ-liệu-architecture)
3. [MA TRẬN CÔNG NGHỆ (TECH STACK SPECIFICATION)](#3-ma-trận-công-nghệ-tech-stack-specification)
4. [ĐẶC TẢ CƠ SỞ DỮ LIỆU CỤC BỘ (LOCAL DATABASE - ROOM SQLITE)](#4-đặc-tả-cơ-sở-dữ-liệu-cục-bộ-local-database---room-sqlite)
5. [ĐẶC TẢ GIAO THỨC TRUYỀN THÔNG & API GATEWAY](#5-đặc-tả-giao-thức-truyền-thông--api-gateway)
6. [ĐẶC TẢ XỬ LÝ ẢNH & TÍCH HỢP MÔ HÌNH AI (EDGE PIPELINE)](#6-đặc-tả-xử-lý-ảnh--tích-hợp-mô-hình-ai-edge-pipeline)
7. [ĐẶC TẢ BAREM CHẤM ĐIỂM SƯ PHẠM BỘ GD&ĐT](#7-đặc-tả-barem-chấm-điểm-sư-phạm-bộ-gdđt)
8. [ĐẶC TẢ CHI TIẾT CÁC MODULE MÀN HÌNH (UI/UX SPECIFICATION)](#8-đặc-tả-chi-tiết-các-module-màn-hình-uiux-specification)
9. [YÊU CẦU PHI CHỨC NĂNG, HIỆU NĂNG & BẢO MẬT](#9-yêu-cầu-phi-chức-năng-hiệu-năng--bảo-mật)
10. [HƯỚNG DẪN BIÊN DỊCH & XUẤT BẢN PRODUCTION APK](#10-hướng-dẫn-biên-dịch--xuất-bản-production-apk)

---

## 🎯 1. TỔNG QUAN HỆ THỐNG & MỤC TIÊU KỸ THUẬT

### 1.1 Bối Cảnh Bài Toán
Trong giáo dục tiểu học tại Việt Nam, việc chấm bài viết tay (chính tả và tập làm văn) chiếm từ 2–3 giờ mỗi ngày của giáo viên. Các công cụ OCR thương mại hiện nay (Google Lens, ABBYY) chủ yếu phục vụ chữ in, không hỗ trợ nhận diện chữ viết tay tiểu học trên nền giấy ô ly, không hiểu quy tắc ngữ âm phương ngữ tiếng Việt (s/x, tr/ch, d/gi/r, hỏi/ngã) và không chấm điểm theo đúng barem 4 tiêu chí của Bộ Giáo dục và Đào tạo.

### 1.2 Giải Pháp Kỹ Thuật
**ViHand Grade Native Android App** là phân hệ di động cao cấp nhất của hệ thống, mang toàn bộ năng lực AI sư phạm tới tận tay giáo viên và học sinh:
* Sử dụng phần cứng máy ảnh Native (**CameraX**) bắt nét trang vở ô ly.
* Kết nối trạm máy chủ biên (**Raspberry Pi 4/5 ARM64**) qua đường truyền mã hóa thời gian thực (Cloudflare Tunnel HTTPS hoặc Wifi 5G Local).
* Hiển thị trực quan các khung chữ nhật đỏ (**Bounding Box**) của mô hình **YOLOv8** đè lên đúng từng nét chữ sai trên trang giấy thật.
* Cung cấp kho bài đọc Sách Giáo Khoa Lớp 1–5 và phát âm chuẩn sư phạm qua công nghệ **Edge-TTS**.
* Hoạt động bền bỉ, an toàn dữ liệu ngoại tuyến nhờ cơ sở dữ liệu nhúng **Room SQLite**.

---

## 🏗️ 2. KIẾN TRÚC PHẦN MỀM & LUỒNG DỮ LIỆU (ARCHITECTURE)

### 2.1 Kiến Trúc Mạng Tổng Thể (Mobile Edge Computing Architecture)

```
┌────────────────────────────────────────────────────────────────────────┐
│                   ĐIỆN THOẠI DI ĐỘNG (5G HOTSPOT)                      │
│        Phát Wifi nội bộ cho toàn bộ gian hàng / lớp học                │
└──────────────────┬─────────────────────────────────┬───────────────────┘
                   │                                 │
                   ▼ (Bắt sóng Wifi 5G)              ▼ (Bắt sóng Wifi 5G)
┌──────────────────────────────────────┐  ┌──────────────────────────────┐
│    TRẠM SERVER BIÊN RASPBERRY PI     │  │   ĐIỆN THOẠI ANDROID NATIVE  │
│                                      │  │                              │
│ • Next.js App Router (Port 3000)     │  │ • Jetpack Compose UI (M3)    │
│   - BFF Gateway: /api/mobile/grade   │  │ • CameraX Hardware Pipeline   │
│   - Tiền xử lý ảnh 9 bước (Jimp)     │  │ • Retrofit 2 + OkHttp 60s     │
│   - Gemini 3.1 Flash Lite OCR Engine │  │ • Room SQLite Local DB        │
│ • Python AI Service (Port 8000)      │  │ • Dual Canvas Bounding Box    │
│   - YOLOv8 Word Bounding Box         │  │ • MediaPlayer Edge-TTS        │
│   - ViT5 Seq2Seq Spelling Corrector  │  │                              │
│   - Qwen 2.5 SLM Pedagogical Comm.   │  │                              │
│ • SQLite Database (vihand.db)        │  │                              │
└──────────────────┬───────────────────┘  └──────────────┬───────────────┘
                   │                                     │
                   │ (Đào hầm HTTPS bảo mật)             │ (Giao tiếp REST JSON)
                   ▼                                     ▼
         ┌─────────────────────────────────────────────────────┐
         │             HỆ THỐNG CLOUDFLARE TUNNEL              │
         │              https://vihandgrade.click              │
         └─────────────────────────────────────────────────────┘
```

### 2.2 Kiến Trúc Ứng Dụng Phía Client (Clean Architecture + MVVM)

Mã nguồn được tổ chức theo chuẩn **Unidirectional Data Flow (UDF)**:
1. **Presentation Layer (`com.example.ui`)**:
   - `screens/`: Các màn hình Compose (`HomeScreen`, `CameraScanScreen`, `GradingResultScreen`, `DictationScreen`, `ReportsAnalyticsScreen`, `ServerSettingsScreen`).
   - `components/`: Các thành phần tái sử dụng (`HandwritingCanvas`, `ErrorDetailCard`, `CriteriaScoreCard`, `NotebookBackground`).
   - `theme/`: Design System (`Color.kt`, `Theme.kt`, `Type.kt`).
2. **Domain & ViewModel Layer (`com.example.ui.viewmodel`)**:
   - `MainViewModel.kt`: Quản lý trạng thái giao diện (`GradingUiState`), điều phối tác vụ bất đồng bộ qua `viewModelScope`.
3. **Data Layer (`com.example.data`)**:
   - `api/`: Lớp mạng (`NetworkClient.kt`, `GradeApiService.kt`, `GradeApiModels.kt`).
   - `local/`: Lớp lưu trữ cục bộ (`AppDatabase.kt`, `GradeRecordDao.kt`, `GradeRecordEntity.kt`).
   - `model/`: Domain Models (`GradeResult`, `ErrorBox`, `GradeCriteria`).
   - `repository/`: `GradeRepository.kt` thực thi gom dữ liệu từ Local và Remote.

---

## 🛠️ 3. MA TRẬN CÔNG NGHỆ (TECH STACK SPECIFICATION)

| Hạng mục | Tên gói thư viện | Phiên bản | Vai trò & Mục đích kỹ thuật |
|---|---|---|---|
| **Ngôn ngữ** | Kotlin | `2.2.10` | Ngôn ngữ phát triển Native chính, hỗ trợ Null-safety và Coroutines |
| **Build Tool** | Android Gradle Plugin (AGP) | `9.1.1` | Biên dịch Gradle với Kotlin DSL (`.kts`) |
| **Android SDK** | `compileSdk: 36`, `targetSdk: 35`, `minSdk: 24` | Android 7.0 – 16 | Tương thích 96.4% thiết bị Android trên toàn cầu |
| **Giao diện (UI)**| `androidx.compose.bom` | `2024.09.00` | Bộ thư viện giao diện hiện đại khai báo (Declarative UI) |
| **Design System** | `androidx.compose.material3:material3` | BOM | Thành phần giao diện Material Design 3, màu động (Dynamic Color) |
| **Máy ảnh** | `androidx.camera:camera-camera2`, `view`, `lifecycle` | `1.5.0` | Điều khiển cảm biến máy ảnh, Preview thời gian thực, Auto-focus |
| **Quyền ứng dụng**| `com.google.accompanist:accompanist-permissions` | `0.37.3` | Quản lý vòng đời cấp quyền Camera an toàn theo chuẩn Android Runtime |
| **Tải ảnh** | `io.coil-kt:coil-compose` | `2.7.0` | Tải ảnh chụp bài thi, giải mã Bitmap ngầm và quản lý bộ nhớ đệm (Cache) |
| **Mạng (Network)**| `com.squareup.retrofit2:retrofit` | `2.12.0` | Khách hàng HTTP REST API type-safe |
| **JSON Parser** | `com.squareup.moshi:moshi-kotlin` | `1.15.2` | Bộ chuyển đổi JSON cực nhanh với KSP Code Generation |
| **HTTP Client** | `com.squareup.okhttp3:okhttp` | `4.10.0` | Tầng vận chuyển mạng, cấu hình Timeout 60s và Interceptor |
| **Cơ sở dữ liệu**| `androidx.room:room-runtime`, `room-ktx` | `2.7.0` | CSDL nhúng SQLite cục bộ, hỗ trợ Reactive Flow queries |
| **Annotation** | `googleDevtoolsKsp` | `2.3.5` | Bộ xử lý chú thích Kotlin Symbol Processing (KSP) tăng tốc độ build |
| **Bất đồng bộ** | `org.jetbrains.kotlinx:kotlinx-coroutines-android` | `1.10.2` | Xử lý luồng ngầm phi phong tỏa (Non-blocking I/O) |

---

## 🗄️ 4. ĐẶC TẢ CƠ SỞ DỮ LIỆU CỤC BỘ (LOCAL DATABASE - ROOM SQLITE)

CSDL được định nghĩa tại [`AppDatabase.kt`](file:///c:/Users/Jackie%20Duong/Desktop/Web_sua_loi/android_app/app/src/main/java/com/example/data/local/AppDatabase.kt) lưu trữ trong thư mục nội bộ của ứng dụng (`/data/data/vn.edu.tdtu.vihandgrade/databases/vihand_grade.db`).

### 4.1 Bảng `grade_records` (`GradeRecordEntity.kt`)
Lưu trữ toàn bộ kết quả các bài chấm để tra cứu ngoại tuyến:

| Tên trường (Column) | Kiểu dữ liệu (SQLite) | Thuộc tính | Diễn giải sư phạm & Kỹ thuật |
|---|---|---|---|
| `id` | `INTEGER` | PRIMARY KEY, AUTOGEN | Mã định danh duy nhất của bản ghi |
| `timestamp` | `INTEGER` | NOT NULL | Thời điểm chấm bài (Epoch Milliseconds) |
| `studentName` | `TEXT` | NOT NULL | Họ và tên học sinh (VD: "Nguyễn Bảo Nam") |
| `className` | `TEXT` | NOT NULL | Tên lớp học (VD: "Lớp 3A1") |
| `essayTitle` | `TEXT` | NOT NULL | Tiêu đề bài viết (VD: "Chính tả: Quạt cho bà ngủ") |
| `totalScore` | `REAL` | NOT NULL | Tổng điểm bài làm (Thang điểm 10.0) |
| `spellingScore` | `REAL` | NOT NULL | Điểm tiêu chí [A] Chính tả (Tối đa 7.0 hoặc 4.0) |
| `formatScore` | `REAL` | NOT NULL | Điểm tiêu chí [B] Hình thức & Trình bày (Tối đa 3.0) |
| `contentScore` | `REAL` | NOT NULL | Điểm tiêu chí [C] Nội dung diễn đạt (Tối đa 2.0) |
| `creativityScore` | `REAL` | NOT NULL | Điểm tiêu chí [D] Sáng tạo & Cảm xúc (Tối đa 1.0) |
| `pedagogicalComment`| `TEXT` | NOT NULL | Lời nhận xét sư phạm giàu tính giáo dục |
| `extractedText` | `TEXT` | NOT NULL | Văn bản gốc OCR nhận diện được từ nét chữ viết tay |
| `correctedFullText` | `TEXT` | NOT NULL | Văn bản mẫu chuẩn sau khi sửa toàn bộ lỗi |
| `errorsJson` | `TEXT` | NOT NULL | Chuỗi JSON chứa danh sách mảng các `ErrorBox` |
| `originalImagePath`| `TEXT` | NULLABLE | Đường dẫn file ảnh chụp bài thi lưu trong bộ nhớ máy |
| `serverSource` | `TEXT` | NOT NULL | Nguồn máy chủ thực thi (Pi 4 / Cloud / Offline) |
| `sampleType` | `TEXT` | NULLABLE | Định danh bài mẫu nếu chạy chế độ demo |

### 4.2 Cấu Trúc Bảng Lớp & Học Sinh (Bổ sung để đồng bộ với Server)
```sql
CREATE TABLE IF NOT EXISTS classes (
    classId TEXT PRIMARY KEY NOT NULL,
    className TEXT NOT NULL,
    gradeLevel INTEGER NOT NULL,
    studentCount INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
    studentId TEXT PRIMARY KEY NOT NULL,
    classId TEXT NOT NULL,
    fullName TEXT NOT NULL,
    gender TEXT,
    FOREIGN KEY(classId) REFERENCES classes(classId) ON DELETE CASCADE
);
```

---

## 🔌 5. ĐẶC TẢ GIAO THỨC TRUYỀN THÔNG & API GATEWAY

Để bảo vệ thiết bị di động khỏi việc nghẽn mạng 5G và tiêu hao pin, ứng dụng Android giao tiếp với máy chủ thông qua **BFF API Gateway** duy nhất cho việc chấm bài.

### 5.1 Endpoint Chấm Điểm Toàn Diện: `POST /api/mobile/grade`

* **Mục đích**: Android chỉ cần gửi **1 Request duy nhất** chứa ảnh. Máy chủ tự chạy song song Gemini OCR + YOLOv8 + Chấm điểm và trả kết quả trọn gói.
* **Thời gian đáp ứng (SLA)**: 3.5s – 8.0s (trên mạng 5G / trạm Pi).
* **Mã phản hồi**: `200 OK`, `400 Bad Request` (ảnh hỏng), `503 Service Unavailable` (quá tải).

#### Request Body (JSON):
```json
{
  "imageBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ...",
  "studentGrade": 3,
  "gradingMode": "dictation",
  "studentName": "Nguyễn Bảo Nam",
  "className": "Lớp 3A1",
  "the_loai": "tho",
  "hinh_thuc": 3.0,
  "noi_dung": 2.0,
  "penalty_per_error": 0.5
}
```

#### Response Body (JSON):
```json
{
  "status": "success",
  "essayTitle": "Chính tả (Nghe - Viết): Quạt cho bà ngủ",
  "studentName": "Nguyễn Bảo Nam",
  "className": "Lớp 3A1",
  "criteria": {
    "spellingScore": 2.5,
    "formatScore": 3.0,
    "contentScore": 2.0,
    "creativityScore": 0.8,
    "totalScore": 8.3
  },
  "pedagogicalComment": "Con viết bài đều nét, giữ vở sạch đẹp. Cần chú ý phân biệt quy tắc âm đầu ch/tr (chổ hoa -> trổ hoa) nhé con.",
  "pedagogicalComments": [
    "Con viết bài đều nét, giữ vở sạch đẹp. Cần chú ý phân biệt quy tắc âm đầu ch/tr (chổ hoa -> trổ hoa) nhé con.",
    "Bài viết sạch sẽ, đúng dòng kẻ ô ly. Khuyên con luyện đọc thêm để ghi nhớ cách viết âm đầu chính xác!",
    "Cô khen ngợi ý thức hoàn thành bài cẩn thận của con. Hãy phát huy nét chữ thanh thoát này nhé!"
  ],
  "extractedText": "Ơi chích chòe ơi! Chim đừng hót nữa, Bà em ốm rồi, Lặng nghe bà ngủ. Bàn tay bé nhỏ, Vẫy quạt thật đều...",
  "correctedFullText": "Ơi chích chòe ơi! Chim đừng hót nữa, Bà em ốm rồi, Lặng nghe bà ngủ. Bàn tay bé nhỏ, Vẫy quạt thật đều...",
  "errors": [
    {
      "id": "err_0",
      "originalWord": "chổ hoa",
      "correctedWord": "trổ hoa",
      "errorType": "phu_am_dau",
      "explanation": "Quy tắc chính tả: 'trổ hoa', 'trổ cành' viết bằng âm đầu 'tr', không viết bằng 'ch'.",
      "penalty": 0.5,
      "lineNumber": 3,
      "rel_x1": 0.4215,
      "rel_y1": 0.3540,
      "rel_w": 0.1820,
      "rel_h": 0.0580
    }
  ],
  "processingTimeMs": 3140,
  "serverSource": "Raspberry Pi 4 ARM64 (Local AI Node)"
}
```

### 5.2 Danh Mục Các API Bổ Trợ

| Phương thức | Endpoint | Chức năng | Dữ liệu trả về |
|---|---|---|---|
| `GET` | `/api/health` | Kiểm tra tình trạng máy chủ & đo độ trễ Ping | `{ "status": "ok", "uptime": 12040 }` |
| `GET` | `/api/dictation/passages` | Tải danh mục bài học SGK Lớp 1–5 theo tuần | Danh sách bài đọc chính tả chuẩn |
| `GET` | `/api/dictation/tts` | Lấy luồng âm thanh phát âm chuẩn Edge-TTS | File âm thanh `audio/mpeg` |
| `GET` | `/api/classes` | Lấy danh sách lớp học quản lý từ CSDL trường | `[ { "id": "3a1", "name": "3A1" } ]` |
| `GET` | `/api/users?role=student`| Lấy danh sách học sinh theo lớp | Danh sách học sinh trong lớp |
| `POST` | `/api/grades` | Lưu kết quả chấm điểm vào sổ điểm trung tâm | `{ "success": true, "recordId": "..." }` |

---

## 📷 6. ĐẶC TẢ XỬ LÝ ẢNH & TÍCH HỢP MÔ HÌNH AI (EDGE PIPELINE)

### 6.1 Chu Trình Thu Nhận & Chuẩn Hóa Ảnh Tại Android Client
1. **CameraX Capture**: Chụp từ cảm biến độ phân giải cao bằng `ImageCapture.OnImageCapturedCallback`.
2. **Cân chỉnh góc xoay EXIF**: Đọc thuộc tính `Orientation` từ ảnh gốc, tự động xoay Bitmap về đúng chiều thẳng đứng $0^\circ$.
3. **Thu nhỏ động (Adaptive Downscaling)**:
   $$\text{Nếu } \max(\text{Width}, \text{Height}) > 1600\text{px} \implies \text{Scale factor } s = \frac{1600}{\max(W, H)}$$
4. **Nén Bitmap**: Nén sang định dạng `JPEG` với chất lượng $85\%$. Kích thước tệp giảm từ $\sim 8\text{MB}$ xuống còn $\sim 600\text{KB} - 800\text{KB}$, truyền qua 5G chỉ mất $\approx 0.2$ giây mà không làm suy giảm độ chính xác của OCR.
5. **Base64 Encoding**: Mã hóa với cờ `Base64.NO_WRAP`.

### 6.2 Công Thức Chiếu Khung Bounding Box Lên Màn Hình Cảm Ứng (Coordinate Projection)

YOLOv8 trả về các tọa độ tương đối độc lập với kích thước màn hình:
$$\text{rel\_x1}, \text{rel\_y1} \in [0.0, 1.0] \quad (\text{Tọa độ góc trên bên trái})$$
$$\text{rel\_w}, \text{rel\_h} \in [0.0, 1.0] \quad (\text{Chiều rộng và chiều cao tương đối})$$

Khi hiển thị trên Jetpack Compose `Canvas` với kích thước khung hình điện thoại là $W_{\text{canvas}} \times H_{\text{canvas}}$:
$$\text{Screen } X = \text{rel\_x1} \times W_{\text{canvas}} \times \text{scale} + \text{offsetX}$$
$$\text{Screen } Y = \text{rel\_y1} \times H_{\text{canvas}} \times \text{scale} + \text{offsetY}$$
$$\text{Box Width} = \text{rel\_w} \times W_{\text{canvas}} \times \text{scale}$$
$$\text{Box Height} = \text{rel\_h} \times H_{\text{canvas}} \times \text{scale}$$

* **Visual Effect**: Khung chữ nhật vẽ bằng nét viền `Stroke(width = 2.dp.toPx())`, màu đỏ tươi `#DC2626`. Khi chạm ngón tay vào ô (`onSelectError`), khung chuyển sang viền kép màu hổ phách `#D97706` kèm hiệu ứng nhịp thở mờ ảo (`pulseAlpha` từ 0.35 đến 0.85).

---

## ⚖️ 7. ĐẶC TẢ BAREM CHẤM ĐIỂM SƯ PHẠM BỘ GD&ĐT

Hệ thống nhúng barem chấm điểm tiểu học Việt Nam thành 2 chế độ riêng biệt:

### 7.1 Chế Độ "Chính Tả" (Nghe - Viết / Tập Chép) — Thang Điểm 10.0
* **[A] Tiêu chí Chính tả (Tối đa 7.0 điểm)**:
  - Điểm khởi đầu: $7.0$ điểm.
  - Công thức trừ điểm: Mỗi lỗi chính tả khác nhau trừ $0.5$ điểm.
  $$\text{Điểm Chính tả} = \max(0.0, \, 7.0 - \text{Số lỗi} \times 0.5)$$
  - *Quy tắc giảm trừ nhân văn*: Lỗi lặp lại cùng một từ chỉ trừ điểm 1 lần duy nhất.
* **[B] Tiêu chí Hình thức & Trình bày (Tối đa 3.0 điểm)**:
  - Đúng dòng kẻ ô ly, thụt đầu dòng đúng quy định, giữ vở sạch, không gạch xóa lem mực.

### 7.2 Chế Độ "Tập Làm Văn" (Đoạn Văn / Bài Văn Miêu Tả) — Thang Điểm 10.0
* **[A] Chính tả & Ngữ pháp (Tối đa 4.0 điểm)**: Trừ $0.5$đ/lỗi sai âm đầu, vần, dấu thanh, viết hoa.
* **[B] Hình thức & Chữ viết (Tối đa 3.0 điểm)**: Trình bày sạch đẹp, bố cục rõ ràng.
* **[C] Nội dung & Ý tưởng (Tối đa 2.0 điểm)**: Đủ ý, mạch lạc, đúng chủ đề bài học.
* **[D] Sáng tạo & Cảm xúc (Tối đa 1.0 điểm)**: Có hình ảnh so sánh, nhân hóa, vốn từ phong phú.

### 7.3 Bảng Mã Phân Loại Lỗi Chính Tả (Error Taxonomies)
1. `viet_hoa`: Không viết hoa đầu câu, đầu dòng thơ, tên riêng hoặc viết hoa tùy tiện.
2. `phu_am_dau`: Nhầm lẫn phụ âm phương ngữ ($s/x, tr/ch, d/gi/r, l/n, c/k/q, g/gh$).
3. `van`: Sai vần và âm cuối ($an/ang, iên/iêng, uôn/uông, at/ac, ay/ai$).
4. `dau_thanh`: Đặt sai dấu, nhầm thanh Hỏi (?) và thanh Ngã (~).
5. `bo_sot_them`: Viết thiếu chữ hoặc lặp thừa chữ.
6. `dau_cau`: Thiếu dấu ngắt câu hoặc đặt sai vị trí dấu chấm, dấu phẩy.

---

## 📱 8. ĐẶC TẢ CHI TIẾT CÁC MODULE MÀN HÌNH (UI/UX SPECIFICATION)

### Module 1: `HomeScreen.kt` (Bảng Điều Khiển Tổng Quan)
* Thẻ chào mừng giáo viên kèm ngày tháng tiếng Việt.
* Thống kê nhanh: Điểm trung bình cả lớp, Tổng số bài đã chấm, Tỷ lệ bài đạt loại Tốt/Xuất sắc.
* Banner nổi bật: Kích hoạt nhanh Camera chấm bài.
* Danh sách 5 bài chấm mới nhất, vuốt ngang để xem chi tiết.
* Lối tắt mở nhanh bài thi mẫu Euréka để diễn tập không cần mạng.

### Module 2: `CameraScanScreen.kt` (Kính Ngắm Máy Ảnh Tài Liệu)
* Kính ngắm `PreviewView` độ sáng cao.
* Khung chữ nhật định hướng trang vở ô ly kèm tia laser quét chuyển động êm dịu.
* Thanh công cụ phía trên: Dropdown chọn Lớp & Tên Học Sinh, Toggle chọn Chế độ Chấm (*Chính tả* / *Tập làm văn*).
* Cụm nút chụp phía dưới: Nút chọn Thư viện ảnh, Nút Chụp chính giữa (có hiệu ứng chớp flash 90ms), Nút Bật/Tắt đèn Flash trợ sáng.

### Module 3: `GradingResultScreen.kt` (Màn Hình Kết Quả & Tương Tác Sư Phạm)
* **Thanh chuyển đổi chế độ xem (View Mode)**:
  - *Xem Ảnh Thật*: Hiển thị ảnh chụp từ camera có khung đỏ bao quanh từ viết sai.
  - *Xem Vở Ô Ly*: Hiển thị bản text chuẩn hóa trên nền lưới 4 ly.
* **Thẻ Điểm 4 Tiêu Chí**: Kèm nút bút chì ✏️ cho phép giáo viên can thiệp chỉnh sửa lại điểm số theo nhận định cá nhân.
* **Thẻ Nhận Xét Sư Phạm**: Hiển thị 3 gợi ý nhận xét đa dạng từ Qwen SLM để giáo viên bấm chọn 1 chạm.
* **Danh sách lỗi sai (`ErrorDetailCard`)**: Hiển thị từ viết sai (gạch ngang đỏ) $\rightarrow$ từ viết đúng (màu xanh lá) kèm lý do sư phạm và nút nghe phát âm.
* **Cụm hành động**: Nút *"Lưu vào sổ điểm trường"* và *"Chia sẻ phiếu điểm qua Zalo"*.

### Module 4: `DictationScreen.kt` (Kho Chính Tả SGK & Trợ Lý Đọc AI)
* **Bộ lọc Chip**: Khối Lớp (1–5) $\times$ Bộ sách (*Kết Nối*, *Chân Trời*, *Cánh Diều*) $\times$ Tuần học.
* **Trình phát âm thanh Edge-TTS**:
  - Chọn giọng đọc: Cô Hoài My (Nữ miền Bắc) / Thầy Nam Minh (Nam miền Bắc).
  - Thanh chỉnh tốc độ đọc ($0.75\text{x}, 0.85\text{x}, 1.0\text{x}$).
  - Hiển thị từng câu thơ/câu văn đang đọc kèm đồng hồ cát đếm ngược thời gian nghỉ để học sinh chép bài.

### Module 5: `ReportsAnalyticsScreen.kt` (Báo Cáo Phân Tích Lỗi Sai)
* Biểu đồ tròn/cột phân bố dạng lỗi học sinh hay mắc nhiều nhất trong tuần/tháng.
* Bảng phổ điểm của lớp và danh sách các em học sinh có điểm dưới trung bình cần hỗ trợ kèm cặp thêm.
* Nút xuất file Excel/CSV báo cáo nộp Ban Giám hiệu.

### Module 6: `ServerSettingsScreen.kt` (Giám Sát Trạm Pi & Mạng)
* Đổi nhanh URL trạm: Cloudflare HTTPS hoặc IP nội bộ Pi.
* Nút **Ping Test**: Đo độ trễ thời gian thực (hiển thị đèn tín hiệu xanh khi $< 50\text{ms}$).
* Trạng thái đồng bộ: Đẩy toàn bộ bản ghi offline lên máy chủ trường khi có mạng.

---

## 🔒 9. YÊU CẦU PHI CHỨC NĂNG, HIỆU NĂNG & BẢO MẬT

### 9.1 Hiệu Năng Vận Hành (Performance Requirements)
* **Tốc độ khung hình giao diện**: Đạt $60\text{fps}$ liên tục, hỗ trợ tần số quét $90\text{Hz} / 120\text{Hz}$ trên màn hình AMOLED/OLED.
* **Mức chiếm dụng bộ nhớ RAM**: $\le 180\text{MB}$ trong suốt phiên chụp ảnh và render Canvas Bounding Box.
* **Thời gian xử lý toàn trình (End-to-End Latency)**: Dưới $10$ giây từ lúc bấm chụp đến khi kết quả hiện ra màn hình.
* **Dung lượng cài đặt ứng dụng (APK Size)**: $\le 25\text{MB}$ sau khi áp dụng ProGuard/R8.

### 9.2 An Toàn & Bảo Mật Thông Tin Học Sinh (Security & Privacy)
* **Mã hóa đường truyền**: Bắt buộc giao thức **HTTPS (TLS 1.3)** qua Cloudflare Tunnel khi truyền ảnh học sinh qua Internet.
* **Hỗ trợ Cleartext cục bộ**: Cho phép `usesCleartextTraffic="true"` duy nhất trong dải mạng nội bộ `192.168.x.x` để dự phòng tình huống mất Internet hoàn toàn tại bàn thi.
* **Bảo vệ quyền riêng tư**: Không chia sẻ ảnh chụp bài thi của học sinh cho bên thứ ba; dữ liệu lưu trong Room Database được cô lập trong Sandbox của ứng dụng.

---

## 🚀 10. HƯỚNG DẪN BIÊN DỊCH & XUẤT BẢN PRODUCTION APK

### 10.1 Cấu Hình Tối Ưu Proguard / R8 (`proguard-rules.pro`)
Để tránh lỗi mất trường dữ liệu khi Moshi giải mã JSON trên bản Release:
```proguard
# Giữ nguyên Data Classes cho Moshi và Retrofit
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-dontwarn com.squareup.moshi.**

# Giữ nguyên Room Database Entities & DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
```

### 10.2 Lệnh Biên Dịch Dòng Lệnh (CLI)
Mở PowerShell tại thư mục `Web_sua_loi/android_app`:

* **Biên dịch bản Debug (Dùng thử nghiệm trên máy):**
  ```powershell
  .\gradlew.bat assembleDebug
  ```
  *Đường dẫn xuất file:* `app/build/outputs/apk/debug/app-debug.apk`

* **Biên dịch bản Release (Tối ưu dung lượng thi đấu):**
  ```powershell
  .\gradlew.bat assembleRelease
  ```
  *Đường dẫn xuất file:* `app/build/outputs/apk/release/app-release.apk`

---

## 🏆 KẾT LUẬN

Tài liệu này là căn cứ kỹ thuật chuẩn xác nhất để đội ngũ lập trình triển khai đồng bộ hóa các tính năng từ Web sang Android Native, đảm bảo hệ sinh thái **ViHand Grade** đạt tiêu chuẩn xuất sắc nhất cả về mặt **Học thuật Sư phạm** lẫn **Công nghệ Di động Hiện đại**.
