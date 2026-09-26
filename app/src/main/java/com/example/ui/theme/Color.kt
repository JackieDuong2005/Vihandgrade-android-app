package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==============================================================================
// ViHand Grade Unified Educational Design System (Aligned with Web Next.js Theme)
// ==============================================================================

// 1. Primary & Brand (Emerald Sư phạm)
val EmeraldPrimary = Color(0xFF059669)      // Primary chuẩn Web (oklch 0.55 0.15 160)
val EmeraldDark = Color(0xFF047857)         // Emerald 700
val EmeraldLight = Color(0xFFECFDF5)        // Emerald 50 (Container nhạt)
val EmeraldAccent = Color(0xFF34D399)       // Emerald 400 (Dùng cho Dark theme)

// 2. Backgrounds & Surfaces (Nền ấm áp như vở ô ly)
val BackgroundCream = Color(0xFFFAF9F6)     // Nền sáng sư phạm dịu mắt
val BackgroundDark = Color(0xFF141724)      // Nền tối dịu mắt (Deep slate navy)
val SurfaceLight = Color(0xFFFFFFFF)        // Bề mặt thẻ trắng sáng
val SurfaceDark = Color(0xFF1E2235)         // Bề mặt thẻ dark mode
val CardElevatedLight = Color(0xFFF8FAFC)   // Thẻ elevated sáng
val CardElevatedDark = Color(0xFF262C42)    // Thẻ elevated tối

// 3. Borders & Dividers
val BorderLight = Color(0xFFE2E8F0)         // Slate 200
val BorderDark = Color(0xFF2D334A)          // Viền dark mode
val BorderSubtleLight = Color(0xFFF1F5F9)   // Slate 100

// 4. Typography Colors
val TextPrimaryLight = Color(0xFF0F172A)    // Slate 900
val TextSecondaryLight = Color(0xFF334155)  // Slate 700
val TextMutedLight = Color(0xFF64748B)      // Slate 500

val TextPrimaryDark = Color(0xFFF8FAFC)     // Slate 50
val TextSecondaryDark = Color(0xFFCBD5E1)   // Slate 300
val TextMutedDark = Color(0xFF94A3B8)       // Slate 400

// 5. Six Pedagogical Error Themes (6 Mã màu loại lỗi chuẩn Bộ GD&ĐT - Khớp 100% ERROR_THEMES trên Web)
val ErrorPhuAmDau = Color(0xFFF43F5E)       // Rose-500 (tr/ch, s/x, r/d/gi, l/n)
val ErrorPhuAmDauBg = Color(0xFFFFF1F2)     // Rose-50
val ErrorPhuAmDauText = Color(0xFFE11D48)   // Rose-600

val ErrorDauThanh = Color(0xFFA855F7)       // Purple-500 (Hỏi/Ngã, Sắc/Nặng)
val ErrorDauThanhBg = Color(0xFFFAF5FF)     // Purple-50
val ErrorDauThanhText = Color(0xFF9333EA)   // Purple-600

val ErrorVan = Color(0xFFF97316)            // Orange-500 (an/ang, en/eng, iên/iêng)
val ErrorVanBg = Color(0xFFFFF7ED)          // Orange-50
val ErrorVanText = Color(0xFFEA580C)        // Orange-600

val ErrorAmChinh = Color(0xFF10B981)        // Emerald-500 (Nguyên âm chính o/ô, u/ư, ai/ay)
val ErrorAmChinhBg = Color(0xFFECFDF5)      // Emerald-50
val ErrorAmChinhText = Color(0xFF059669)    // Emerald-600

val ErrorPhuAmCuoi = Color(0xFF0EA5E9)      // Sky-500 (Âm cuối t/c, n/ng, p/m)
val ErrorPhuAmCuoiBg = Color(0xFFF0F9FF)    // Sky-50
val ErrorPhuAmCuoiText = Color(0xFF0284C7)  // Sky-600

val ErrorVietHoa = Color(0xFFF59E0B)        // Amber-500 (Viết hoa đầu câu & tên riêng)
val ErrorVietHoaBg = Color(0xFFFFFBEB)      // Amber-50
val ErrorVietHoaText = Color(0xFFD97706)    // Amber-600

// 6. Score Evaluation Badges (Thông tư 27 Bộ GD&ĐT)
val ScoreXuatSac = Color(0xFF059669)        // Điểm >= 9: Xuất sắc (Emerald)
val ScoreTot = Color(0xFF0284C7)            // Điểm 7 - <9: Hoàn thành tốt (Sky)
val ScoreHoanThanh = Color(0xFFD97706)      // Điểm 5 - <7: Hoàn thành (Amber)
val ScoreCanCoGang = Color(0xFFDC2626)      // Điểm < 5: Cần cố gắng (Rose)

// 7. Backward compatibility aliases
val SlateBackground = BackgroundDark
val SlateSurface = SurfaceDark
val SlateCard = SurfaceDark
val SlateCardElevated = CardElevatedDark
val SlateBorder = BorderDark

val TextPrimary = TextPrimaryLight
val TextMuted = TextMutedLight
val TextSecondary = TextSecondaryLight

val AccentSky = ErrorPhuAmCuoi
val AccentAmber = ErrorVietHoa
val AccentCoral = Color(0xFFEF4444)
val AccentCoralLight = Color(0xFFF87171)
val AccentIndigo = Color(0xFF6366F1)
