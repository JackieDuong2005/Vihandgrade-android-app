package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.UserData

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("vihand_user_session", Context.MODE_PRIVATE)

    fun saveUser(user: UserData) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, user.id)
            putString(KEY_NAME, user.name)
            putString(KEY_USERNAME, user.username)
            putString(KEY_ROLE, user.role)
            putString(KEY_CLASS_NAME, user.className ?: "")
            putStringSet(KEY_CLASSES, user.classes?.toSet() ?: emptySet())
            apply()
        }
    }

    fun getUser(): UserData? {
        if (!isLoggedIn()) return null
        val id = prefs.getString(KEY_USER_ID, "") ?: ""
        val name = prefs.getString(KEY_NAME, "Giáo viên") ?: "Giáo viên"
        val username = prefs.getString(KEY_USERNAME, "") ?: ""
        val role = prefs.getString(KEY_ROLE, "teacher") ?: "teacher"
        val className = prefs.getString(KEY_CLASS_NAME, "") ?: ""
        val classes = prefs.getStringSet(KEY_CLASSES, emptySet())?.toList() ?: emptyList()

        return UserData(
            id = id,
            name = name,
            username = username,
            role = role,
            className = className,
            classes = classes
        )
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun loginAsGuest(role: String = "teacher", name: String = "Cô Nguyễn Mai Hương") {
        saveUser(
            UserData(
                id = "guest_${System.currentTimeMillis()}",
                name = name,
                username = "guest",
                role = role,
                className = if (role == "student") "Lớp 3A" else "",
                classes = listOf("Lớp 3A", "Lớp 3B", "Lớp 4A", "Lớp 5A")
            )
        )
    }

    // Pedagogical & Teacher Settings (Thông tư 27)
    fun getTeacherSchool(): String = prefs.getString(KEY_SCHOOL_NAME, "Trường Tiểu học Chu Văn An") ?: "Trường Tiểu học Chu Văn An"
    fun setTeacherSchool(school: String) = prefs.edit().putString(KEY_SCHOOL_NAME, school).apply()
    fun getSchoolName(): String = getTeacherSchool()
    fun setSchoolName(name: String) = setTeacherSchool(name)

    fun getPenaltyPerError(): Float = prefs.getFloat(KEY_PENALTY_PER_ERROR, 0.5f)
    fun setPenaltyPerError(penalty: Float) = prefs.edit().putFloat(KEY_PENALTY_PER_ERROR, penalty).apply()

    fun getAutoEncouragement(): Boolean = prefs.getBoolean(KEY_AUTO_ENCOURAGEMENT, true)
    fun isAutoEncouragement(): Boolean = getAutoEncouragement()
    fun setAutoEncouragement(enabled: Boolean) = prefs.edit().putBoolean(KEY_AUTO_ENCOURAGEMENT, enabled).apply()

    fun isAutoBoundingBox(): Boolean = prefs.getBoolean(KEY_AUTO_BOUNDING_BOX, true)
    fun setAutoBoundingBox(enabled: Boolean) = prefs.edit().putBoolean(KEY_AUTO_BOUNDING_BOX, enabled).apply()

    fun getDefaultEssayType(): String = prefs.getString(KEY_DEFAULT_ESSAY_TYPE, "spelling") ?: "spelling"
    fun setDefaultEssayType(type: String) = prefs.edit().putString(KEY_DEFAULT_ESSAY_TYPE, type).apply()

    fun getTtsVoice(): String = prefs.getString(KEY_TTS_VOICE, "voice_female_north") ?: "voice_female_north"
    fun setTtsVoice(voice: String) = prefs.edit().putString(KEY_TTS_VOICE, voice).apply()

    fun getTtsSpeed(): Float = prefs.getFloat(KEY_TTS_SPEED_FLOAT, 1.0f)
    fun setTtsSpeed(speed: Float) = prefs.edit().putFloat(KEY_TTS_SPEED_FLOAT, speed).apply()

    fun getLastSyncTime(): String = prefs.getString(KEY_LAST_SYNC_TIME, "Chưa đồng bộ") ?: "Chưa đồng bộ"
    fun setLastSyncTime(time: String) = prefs.edit().putString(KEY_LAST_SYNC_TIME, time).apply()

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAME = "name"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
        private const val KEY_CLASS_NAME = "class_name"
        private const val KEY_CLASSES = "classes"

        private const val KEY_SCHOOL_NAME = "school_name"
        private const val KEY_PENALTY_PER_ERROR = "penalty_per_error"
        private const val KEY_AUTO_ENCOURAGEMENT = "auto_encouragement"
        private const val KEY_AUTO_BOUNDING_BOX = "auto_bounding_box"
        private const val KEY_DEFAULT_ESSAY_TYPE = "default_essay_type"
        private const val KEY_TTS_VOICE = "tts_voice"
        private const val KEY_TTS_SPEED_FLOAT = "tts_speed_float"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
    }
}
