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

    fun loginAsGuest(role: String = "teacher", name: String = "Giáo viên Tiểu học") {
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

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAME = "name"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
        private const val KEY_CLASS_NAME = "class_name"
        private const val KEY_CLASSES = "classes"
    }
}
