package com.xotkins.mytasklist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.xotkins.mytasklist.databinding.EditListItemDialogBinding
import com.xotkins.mytasklist.entities.DayListItem


object EditListItemDialog {   //создаём класс NewListDialog
    fun showDialog(context: Context, item: DayListItem, listener: Listener) { //создаём функцию showDialog, передаёт сюда context
        var dialog: AlertDialog? = null //создаём переменную dialog от класса AlertDialog, который может быть null
        val builder = AlertDialog.Builder(context) // создаём переменную builder и инициализируем его
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context)) // здесь мы делаем разметку, где все наши элементы
        builder.setView(binding.root)


        //здесь присваиваем слушатель нажатий
        binding.apply {
            edName.setText(item.name) // для редактирования передаёт старое название, чтобы изменить
            edInfo.setText(item.itemInfo) // для редактирования передаёт старое описание, чтобы изменить
           if(item.itemType == 1) edInfo.visibility = View.GONE
            buttonUpdate.setOnClickListener{
                if(edName.text.toString().isNotEmpty()){ //если наш edName не пустой, то мы можем записывать
                    listener.onClick(item.copy(name = edName.text.toString(), itemInfo = edInfo.text.toString())) //сюда мы передаём наше старое название и описание
                }
                dialog?.dismiss() //то просто закрываем диалог
            }

        }

        dialog = builder.create() //создаётся диалог(экран)
        dialog.window?.setBackgroundDrawable(null) //убираем стандартный фон вокруг нашего диалога
        dialog.show() //здесь мы показываем диалог
    }

    interface Listener{ //Создаём интерфейс Listener
        fun onClick(item: DayListItem) //функция которая возвращает name обьект типа String
    }
}