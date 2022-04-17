package com.xotkins.mytasklist.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object TimeManager {
    private const val DEF_TIME_FORMAT = "hh:mm:ss - dd/MM/yyyy"
     fun getCurrentTime(): String { //функция которая берёт настоящее время
        val formatter = SimpleDateFormat(
            DEF_TIME_FORMAT,
            Locale.getDefault()
        ) //получаем в каком формате получаем время
        return formatter.format(Calendar.getInstance().time) //возвращает уже формат времени
    }
}