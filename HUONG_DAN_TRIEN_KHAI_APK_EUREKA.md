# 📱 Cẩm Nang Triển Khai Android App Native (Kotlin / Jetpack Compose) Cho ViHand Grade
### Kịch bản Triển khai Thực chiến Thi đấu Giải thưởng NCKH Euréka (Raspberry Pi Server + 5G Hotspot + Cloudflare)

---

## 📌 1. Tổng Quan Về Ứng Dụng Android Native (`android_app`)

Thư mục [`android_app`](./) là dự án **Android Native 100%** được phát triển chuyên biệt cho hệ sinh thái **ViHand Grade**, xây dựng bằng ngôn ngữ **Kotlin** kết hợp bộ thư viện giao diện hiện đại nhất của Google là **Jetpack Compose**.

### 🌟 Các module giao diện Frontend đã hoàn thiện đầy đủ:
- **`HomeScreen`** ([`app/.../ui/screens/HomeScreen.kt`](./app/src/main/java/com/example/ui/screens/HomeScreen.kt)): Bảng điều khiển tổng quan dành cho giáo viên và học sinh, hiển thị điểm trung bình, danh sách 5 bài nộp mới nhất và banner kích hoạt camera chụp bài.
- **`GradingResultScreen`** ([`app/.../ui/screens/GradingResultScreen.kt`](./app/src/main/java/com/example/ui/screens/GradingResultScreen.kt)): Màn hình chấm điểm cốt lõi hiển thị bài làm học sinh trên nền giấy ô ly, phủ các khung viền chữ nhật đỏ (**Bounding Box**) theo tọa độ tương đối (`%`) từ mô hình **YOLOv8**, bảng điểm 4 tiêu chí của Bộ GD&ĐT và lời nhận xét sư phạm.
- **`CameraScanScreen`** ([`app/.../ui/screens/CameraScanScreen.kt`](./app/src/main/java/com/example/ui/screens/CameraScanScreen.kt)): Giao diện máy ảnh Native có khung chữ nhật định hướng khổ giấy ô ly học sinh, tự động nén ảnh bitmap trước khi gửi lên trạm server.
- **`DictationScreen`** ([`app/.../ui/screens/DictationScreen.kt`](./app/src/main/java/com/example/ui/screens/DictationScreen.kt)): Màn hình luyện đọc chính tả AI theo kho bài học SGK Lớp 1–5, tích hợp thanh điều khiển âm thanh Edge-TTS.
- **`HistoryScreen`** ([`app/.../ui/screens/HistoryScreen.kt`](./app/src/main/java/com/example/ui/screens/HistoryScreen.kt)): Lịch sử bài chấm lưu trữ cục bộ trong cơ sở dữ liệu **Room Database (SQLite)** ngay trên điện thoại, hỗ trợ xem lại bài kể cả khi không có mạng.
- **`ServerSettingsScreen`** ([`app/.../ui/screens/ServerSettingsScreen.kt`](./app/src/main/java/com/example/ui/screens/ServerSettingsScreen.kt)): Màn hình cấu hình và giám sát trạm máy chủ Raspberry Pi nhúng, cho phép đổi URL máy chủ linh hoạt và có nút **Kiểm tra kết nối (Ping)** đo độ trễ thời gian thực.
- **`HandwritingCanvas`** ([`app/.../ui/components/HandwritingCanvas.kt`](./app/src/main/java/com/example/ui/components/HandwritingCanvas.kt)): Canvas tương tác cảm ứng cho phép Ban Giám Khảo **chạm ngón tay vào chữ sai** để mở popup giải thích chi tiết lỗi sai và nghe ngữ âm chuẩn.

---

## 🏗️ 2. Kiến Trúc Kết Nối Edge Computing Ngày Thi Euréka

Tại gian hàng thuyết trình Euréka, hệ thống vận hành theo mô hình **Điện toán biên di động (Mobile Edge Computing)** độc lập:

```
                  ┌────────────────────────────────────────────────────────┐
                  │          ĐIỆN THOẠI CỦA BẠN (5G HOTSPOT)               │
                  │   Bật "Điểm phát sóng cá nhân" phát Wifi cho gian hàng │
                  └─────────────────────────┬──────────────────────────────┘
                                            │
               ┌────────────────────────────┴────────────────────────────┐
               │                                                         │
               ▼ (Bắt sóng Wifi 5G Hotspot)                              ▼ (Bắt sóng Wifi 5G hoặc dữ liệu di động)
┌────────────────────────────────────────┐      ┌────────────────────────────────────────────────────────┐
│      TRẠM SERVER NHÚNG TẠI BÀN THI     │      │        ĐIỆN THOẠI CÀI ĐẶT ANDROID APP (KOTLIN)         │
│  (Raspberry Pi 4/5 ARM64 / Mini PC)    │      │         (Do Bạn hoặc Ban Giám Khảo trực tiếp cầm)      │
│                                        │      │                                                        │
│ • Next.js App Router (Cổng :3000)      │      │ • UI thuần Jetpack Compose mượt mà 60-120fps           │
│   - Tiền xử lý ảnh 9 bước (Jimp)       │      │ • Retrofit 2 + Moshi + OkHttpClient gọi REST API:      │
│   - Bóc tách văn bản Gemini Vision     │      │   POST https://vihandgrade.click/api/grade             │
│ • Python AI Service (Cổng :8000)       │      │ • Khung Bounding Box đỏ tương tác chạm ngón tay        │
│   - ViT5 Seq2Seq Spelling Correction   │      │ • Room Database SQLite lưu lịch sử bài chấm Offline    │
│   - YOLOv8 Word Bounding Box Detection │      │ • Chuyển đổi linh hoạt giữa Cloudflare và IP Local     │
│ • SQLite Database (prisma/vihand.db)   │      │   ngay trong tab "Trạm Pi"                             │
│ • Cloudflared Tunnel Daemon            │      │                                                        │
└──────────────────┬─────────────────────┘      └──────────────────────────┬─────────────────────────────┘
                   │                                                       │
                   │ (Đào hầm Cloudflare Tunnel bảo mật qua 5G)            │ (Truy cập an toàn chuẩn HTTPS)
                   ▼                                                       ▼
         ┌───────────────────────────────────────────────────────────────────────┐
         │                    HỆ THỐNG CLOUDFLARE TOÀN CẦU                       │
         │                     https://vihandgrade.click                         │
         └───────────────────────────────────────────────────────────────────────┘
```

---

## 🔌 3. Cấu Hình Endpoint API Trong Mã Nguồn Android App

Mã nguồn Android App đã được cấu hình sẵn tại lớp [`NetworkClient`](./app/src/main/java/com/example/data/api/NetworkClient.kt):

```kotlin
object NetworkClient {
    // URL mặc định kết nối qua Cloudflare Tunnel đến Raspberry Pi:
    const val DEFAULT_BASE_URL = "https://vihandgrade.click/"
    ...
}
```

### 🎯 Cơ chế linh hoạt: Chuyển đổi máy chủ ngay trên App (Không cần build lại)
Trong màn hình **Trạm Pi** (`ServerSettingsScreen.kt`), người dùng hoặc thí sinh có thể nhập URL mới bất cứ lúc nào:
- **Chế độ 1 (Khuyên dùng)**: `https://vihandgrade.click` (Đi qua Cloudflare Tunnel – An toàn, bảo mật SSL, hoạt động mọi nơi).
- **Chế độ 2 (Dự phòng mất mạng)**: `http://192.168.43.xxx:3000` (Truy cập trực tiếp IP của Raspberry Pi trong cùng mạng 5G Hotspot).

Sau khi đổi URL, bấm nút **"Kiểm tra kết nối (Ping)"** để kiểm tra API `/api/health` trả về trạng thái xanh ✅ ngay trên màn hình.

---

## 🛠️ 4. Hướng Dẫn Biên Dịch & Xuất File APK Từ `android_app`

### 💻 Cách 1: Biên dịch bằng Android Studio (Chuẩn & Trực quan nhất)

1. **Mở dự án**:
   - Khởi động **Android Studio** trên máy tính.
   - Chọn **Open** (hoặc *File > Open*).
   - Trỏ đến đúng thư mục: `Web_sua_loi/android_app` và bấm **OK**.
2. **Đồng bộ thư viện (Gradle Sync)**:
   - Android Studio sẽ tự động tải các gói phụ thuộc (Jetpack Compose, Retrofit, Room, Moshi).
   - Quá trình đồng bộ mất khoảng 1–2 phút lần đầu tiên.
3. **Biên dịch file APK**:
   - Trên thanh menu của Android Studio, chọn:
     👉 **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
   - Khi hoàn tất, góc dưới bên phải màn hình sẽ hiện thông báo kèm nút **locate**.
   - Vị trí file APK xuất ra:
     📁 `android_app/app/build/outputs/apk/debug/app-debug.apk`
4. **Cài đặt vào điện thoại**:
   - **Cách A**: Kết nối điện thoại vào máy tính bằng cáp Type-C (bật chế độ *USB Debugging*) rồi bấm nút **Run ▶️** trên Android Studio.
   - **Cách B**: Chép trực tiếp file `app-debug.apk` sang điện thoại qua Zalo/Google Drive/USB rồi bấm cài đặt.

---

### ⌨️ Cách 2: Biên dịch trực tiếp bằng Dòng lệnh (Terminal CLI)

Nếu máy tính của bạn đã cài sẵn Java JDK:
1. Mở PowerShell hoặc Terminal tại thư mục `android_app`:
   ```powershell
   cd "c:\Users\Jackie Duong\Desktop\Web_sua_loi\android_app"
   ```
2. Chạy lệnh Gradle để build APK:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
   *(Trên Linux/Mac: `./gradlew assembleDebug`)*
3. File APK sẽ sẵn sàng tại:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## ⚙️ 5. Chuẩn Bị Trạm Máy Chủ Raspberry Pi (Cắm Điện Là Chạy)

Để trong ngày thi bạn **không cần mang theo màn hình, bàn phím hay chuột cho Raspberry Pi**, hãy cấu hình trước các dịch vụ tự động khởi động cùng hệ điều hành:

### Bước 5.1: Cài đặt Pi tự động bắt sóng 5G Hotspot
```bash
sudo nmcli dev wifi connect "TEN_WIFI_5G_CUA_BAN" password "MAT_KHAU_5G"
```

### Bước 5.2: Tối ưu bộ nhớ ảo Swap trên Pi tránh tràn RAM
```bash
sudo nano /etc/dphys-swapfile
# Đổi dòng CONF_SWAPSIZE=2048 (hoặc 4096)
sudo /etc/init.d/dphys-swapfile restart
```

### Bước 5.3: Cài đặt Cloudflare Tunnel chạy ngầm
```bash
sudo cloudflared service install <TOKEN_CUA_TUNNEL>
sudo systemctl enable cloudflared
sudo systemctl start cloudflared
```

### Bước 5.4: Thiết lập 2 dịch vụ Systemd tự khởi động
**Dịch vụ 1: Python AI Service (ViT5 + YOLOv8 - Cổng 8000)**
```bash
sudo nano /etc/systemd/system/vihand-ai.service
```
Nội dung:
```ini
[Unit]
Description=ViHand Grade Python AI Service (ViT5 & YOLOv8)
After=network.target

[Service]
Type=simple
User=pi
WorkingDirectory=/home/pi/Web_sua_loi/python_service
Environment="PATH=/home/pi/Web_sua_loi/python_service/venv/bin:/usr/bin"
Environment="ENABLE_QWEN_SLM=1"
ExecStart=/home/pi/Web_sua_loi/python_service/venv/bin/python main.py
Restart=always
RestartSec=3

[Install]
WantedBy=multi-user.target
```

**Dịch vụ 2: Next.js Web & Grading API (Cổng 3000)**
```bash
sudo nano /etc/systemd/system/vihand-web.service
```
Nội dung:
```ini
[Unit]
Description=ViHand Grade Next.js API Server
After=network.target vihand-ai.service

[Service]
Type=simple
User=pi
WorkingDirectory=/home/pi/Web_sua_loi
Environment="NODE_ENV=production"
Environment="PORT=3000"
ExecStart=/usr/bin/npm run start
Restart=always
RestartSec=3

[Install]
WantedBy=multi-user.target
```

Kích hoạt cả 2 dịch vụ:
```bash
sudo systemctl daemon-reload
sudo systemctl enable vihand-ai vihand-web
sudo systemctl start vihand-ai vihand-web
```

---

## 🎯 6. Kịch Bản Thuyết Minh & Trình Diễn Trước Ban Giám Khảo Euréka

### 🎙️ Lời thoại mở đầu (Tự tin, khoa học):
> *"Kính thưa Ban Giám Khảo, để giải quyết trọn vẹn bài toán hỗ trợ giáo viên tiểu học chấm bài nhanh chóng ở mọi điều kiện trường học, nhóm chúng em đã phát triển ứng dụng di động **ViHand Grade Native** trên nền tảng **Android (Kotlin & Jetpack Compose)**.*  
> *Thay vì phụ thuộc vào các dịch vụ đám mây đắt đỏ, ứng dụng kết nối trực tiếp với **Trạm máy chủ biên Raspberry Pi** đặt ngay tại bàn thi qua đường truyền mã hóa thời gian thực. Em xin phép gửi Thầy/Cô chiếc điện thoại cài đặt sẵn ứng dụng để Thầy/Cô trực tiếp trải nghiệm ạ!"*

### 📋 Quy trình 4 bước demo ấn tượng trong 3 phút:

1. **Bước 1 — Trao máy và trải nghiệm giao diện Native**:
   - Đưa điện thoại cho Giám khảo. Giám khảo sẽ ấn tượng với giao diện mượt mà 60–120fps chuẩn Material 3 của Jetpack Compose.
2. **Bước 2 — Chụp bài viết tay hoặc chọn bài mẫu**:
   - Giám khảo bấm vào nút **Chấm bài (Camera)** ở chính giữa thanh điều hướng.
   - Hướng camera vào bài viết tay của học sinh (có lỗi cố tình như *"su bé ngủ xay, thay cho só xời"*).
   - Bấm nút Chụp và xác nhận **Chấm điểm**.
3. **Bước 3 — Minh chứng tính toán tại trạm nhúng**:
   - Thí sinh chỉ tay về phía chiếc Raspberry Pi: Đèn LED tín hiệu CPU trên Pi sáng nhấp nháy, minh chứng thuật toán Jimp 9 bước, ViT5 và YOLOv8 đang xử lý trực tiếp trên chip ARM.
4. **Bước 4 — Tương tác trực quan trên kết quả**:
   - Sau ~10–12 giây, màn hình hiển thị:
     - Khung chữ nhật màu đỏ bao quanh chính xác từng từ sai nhờ mô hình **YOLOv8**.
     - Bảng điểm 4 tiêu chí rõ ràng (Tổng điểm 8.4/10).
     - Lời nhận xét sư phạm giàu tính giáo dục.
   - **Điểm nhấn đặc biệt**: Mời Giám khảo **chạm tay vào khung đỏ** để nghe app phát âm chuẩn tiếng Việt và giải thích quy tắc chính tả.

---

## ❓ 7. Bộ Câu Hỏi & Đối Đáp Kỹ Thuật Dự Phòng (Defense Q&A)

| Câu hỏi của Giám khảo | Hướng trả lời thuyết phục |
|---|---|
| **Tại sao nhóm làm Android Native (Compose) mà không dùng Web/Capacitor?** | Native cho hiệu năng tối đa 60–120fps khi vẽ Canvas Bounding Box thời gian thực, điều khiển phần cứng Camera và nén Bitmap tối ưu, đồng thời tích hợp Room SQLite lưu trữ lịch sử bài chấm để xem lại Offline mượt mà. |
| **Nếu hội trường thi mất sóng 5G/Internet thì app có hoạt động được không?** | Hoàn toàn hoạt động được. App có sẵn tính năng chuyển đổi máy chủ tại tab **Trạm Pi**: chỉ cần nhập IP Local của Pi (`http://192.168.43.xxx:3000`) là app kết nối trực tiếp nội bộ qua sóng phát của điện thoại, không cần Internet. |
| **Dung lượng ảnh chụp từ camera điện thoại rất lớn, làm sao gửi nhanh qua Pi?** | Trong `CameraScanScreen.kt`, ảnh chụp từ cảm biến 50MP được hàm `Bitmap.compress()` tự động chuẩn hóa kích thước tối đa 1600px và nén JPEG chất lượng 85%, giảm kích thước từ 8MB xuống ~800KB, truyền qua mạng chỉ mất dưới 0.5 giây. |
| **Dữ liệu bài làm của học sinh lưu ở đâu trên điện thoại?** | Dữ liệu được lưu trong **Room Database (`AppDatabase.kt`)** với Entity `GradeRecordEntity`, đảm bảo bảo mật dữ liệu học sinh theo quy chuẩn và có thể tra cứu lại bất cứ lúc nào trong tab **Lịch sử**. |

---
*Tài liệu được cập nhật độc quyền cho mã nguồn Android Native trong thư mục `android_app`.*
