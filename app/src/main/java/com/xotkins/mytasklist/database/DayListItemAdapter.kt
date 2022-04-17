package com.xotkins.mytasklist.database

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xotkins.mytasklist.R
import com.xotkins.mytasklist.databinding.DayListItemBinding
import com.xotkins.mytasklist.entities.DayListItem


//в конструкторе  передаем интерфейс Listener и запускаем через слушатель нажатия
class DayListItemAdapter(private var listener: Listener) :
    ListAdapter<DayListItem, DayListItemAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemHolder { //здесь создаётся разметак ViewHolder
        return ItemHolder.createDayItem(parent)

    }

    override fun onBindViewHolder(
        holder: ItemHolder,
        position: Int,
    ) { //здесь заполняется разметка ViewHolder
        holder.setItemData(getItem(position), listener)
    }

    override fun getItemViewType(position: Int): Int { //функция для получения числа к элементу
        return getItem(position).itemType
    }

    class ItemHolder(val view: View) :
        RecyclerView.ViewHolder(view) { //Создается новый класс ItemHolder, в нём передаётся разметка RecyclerView

        //создаётся функция setItemData
        fun setItemData(dayListItem: DayListItem, listener: Listener){
        val binding = DayListItemBinding.bind(view) //находим разметку для добавления в базу данных
        binding.apply {
            tvName.text = dayListItem.name //Здесь мы добавляем нашему элементу из списка именование
            tvInfo.text = dayListItem.itemInfo // здесь мы заполняем информацию об эелементе из списка
            tvInfo.visibility = infoVisibility(dayListItem) //здесь выполняется проверка и делает видимым или нет
            checkBox.isChecked = dayListItem.itemChecked //здесь будет показывать состояние checkBox
            setPaintFlagAndColor(binding) //тут берутся элементы по индефикаторам
            //создаётся слушатель нажатия checkBox
            checkBox.setOnClickListener{
                listener.onCLickItem(dayListItem.copy(itemChecked = checkBox.isChecked), CHECK_BOX) //здесь сохраняется состояние checkBox выбраным и записывается в базу данных, если нажали на CheckBox
            }
                //создаём слушатель нажатия кнопки редактрирование элемента в списке
            imageEdit.setOnClickListener {
                listener.onCLickItem(dayListItem, EDIT)// если нажали на Edit
            }
            imDeleted.setOnClickListener{
                listener.onCLickItem(dayListItem, DELETE) // здесь добавляем слушатель нажатия (удаления элемента, передаём id элемента)
            }
        }
        }


        private fun setPaintFlagAndColor(binding: DayListItemBinding){ //функция для отметки, что элемент из списка куплен, меняет цвет и перечёркивает, функция запускается в fun setItemData
           binding.apply {
               if(checkBox.isChecked){ //если чекбокс нажат
                   tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG //то элемент из списка перечеркивается
                   tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG //то описание элемента из списка перечеркивается
                   tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray_light)) //то элемент из списка меняет цвет на серый
                   tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray_light)) //то описание элемента из списка меняет цвет на серый
               } else { //впротивном случае
                   tvName.paintFlags = Paint.ANTI_ALIAS_FLAG //то элемент из списка снова без перечеркивания
                   tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG //то описание элемента из списка снова без перечеркивания
                   tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black)) //то элемент из списка меняет цвет на чёрный
                   tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black)) //то описание элемента из списка меняет цвет на чёрный
               }
           }
        }

         private fun infoVisibility(dayListItem: DayListItem): Int{ //создаём функцию для проверки, есть ли информация об элементе и запускаем её в функции SetItemData
           return if(dayListItem.itemInfo.isNullOrEmpty()){ //если в информации об элементе из списка покупок отсутствует
               View.GONE // то описание скрыто
           } else {//в противном случае, если в информации об элементе из списка покупок есть
               View.VISIBLE //то показывает эту информацию
           }
        }



        companion object{ //создаётся статическая функция
            fun createDayItem(parent: ViewGroup): ItemHolder{ //создаётся статическая функция, здесь надувается разметка
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.day_list_item, parent, false)) //возвращается разметка для дальнейшего заполнения
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<DayListItem>() {
        override fun areItemsTheSame(
            oldItem: DayListItem,
            newItem: DayListItem,
        ): Boolean {//функция сравненивает элементы, если они похожи
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DayListItem,
            newItem: DayListItem,
        ): Boolean { //функция сравнивает весь контент
            return oldItem == newItem
        }


    }

    interface Listener {

        fun onCLickItem(dayListItem: DayListItem, state: Int)
        fun deleteItem(id: Int)
    }

    companion object {
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val DELETE = 5



    }

}