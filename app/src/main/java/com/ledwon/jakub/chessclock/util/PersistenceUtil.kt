package com.ledwon.jakub.chessclock.util

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BooleanPreferencesDelegate(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }
}

class EnumPreferencesDelegate<T : Enum<*>>(
    private val preferences: SharedPreferences,
    private val key: String,
    private val enumClass: Class<T>,
    private val defaultValue: T
) : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val value = preferences.getString(key, defaultValue.name)
        return enumClass.enumConstants?.find { it.name == value } ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        preferences.edit().putString(key, value.name).apply()
    }
}

class StringPreferencesDelegate(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }
}

class FloatPreferencesDelegate(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Float
) : ReadWriteProperty<Any, Float> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Float {
        return preferences.getFloat(key, defaultValue)
    }
}

class NullableStringSetPreferencesDelegate(
    private val preferences: SharedPreferences,
    private val key: String
) : ReadWriteProperty<Any, Set<String>?> {
    override fun setValue(thisRef: Any, property: KProperty<*>, value: Set<String>?) {
        preferences.edit().putStringSet(key, value).apply()
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): Set<String>? {
        return preferences.getStringSet(key, null)
    }
}