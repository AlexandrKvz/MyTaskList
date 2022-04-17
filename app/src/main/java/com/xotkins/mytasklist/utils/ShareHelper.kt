package com.xotkins.mytasklist.utils

import android.content.Intent
import com.xotkins.mytasklist.entities.DayListItem
import java.lang.StringBuilder

//создём класс для передачи нашего списка по соц.сетям
object ShareHelper {
    fun shareDayList(dayList: List<DayListItem>, listName: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/pane"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeShareText(dayList, listName))
        }
        return intent //возваращаем наши данные
    }

    private fun makeShareText(dayList: List<DayListItem>, listName: String): String {
        val stringBuilder =
            StringBuilder()//создаём переменную класса StringBuilder(), переменная будет собирать
        stringBuilder.append("<<$listName>>") //заполняет название списка
        stringBuilder.append("\n") //переводит на следующую строку
        var counter = 0 //счётчик для элементов
        //с помощью этого цикла пробегает все элементы и заполняет наш список
        dayList.forEach {
            stringBuilder.append("${++counter} - ${it.name} (${it.itemInfo})")
            stringBuilder.append("\n")
        }
        return stringBuilder.toString() //возвращает наш текст
    }
}