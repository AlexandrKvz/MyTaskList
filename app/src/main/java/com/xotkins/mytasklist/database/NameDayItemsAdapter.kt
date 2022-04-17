package com.xotkins.mytasklist.database

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xotkins.mytasklist.R
import com.xotkins.mytasklist.databinding.ListNameItemBinding
import com.xotkins.mytasklist.entities.NameDayItems



class NameDayItemsAdapter(private var listener: Listener) :
    ListAdapter<NameDayItems, NameDayItemsAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemHolder { //здесь создаётся разметак ViewHolder
        return ItemHolder.create(parent) //здесь возвращается ItemHolder
    }

    override fun onBindViewHolder(
        holder: ItemHolder,
        position: Int,
    ) { //здесь заполняется разметка ViewHolder
        holder.setData(getItem(position), listener)
    }

    class ItemHolder(view: View) :
        RecyclerView.ViewHolder(view) { //Создается новый класс ItemHolder, в нём передаётся разметка RecyclerView
        private val binding = ListNameItemBinding.bind(view)


        fun setData(nameDayItems: NameDayItems, listener: Listener) = with(binding) {
            tvListName.text = nameDayItems.name //записывается заголовок
            tvTime.text = nameDayItems.time // записывается время создания
            progressBar.max =
                nameDayItems.allItemCounter //здесь мы указываем максимальный размер progressBar - это кол-во элементов в списке
            progressBar.progress =
                nameDayItems.checkedItemsCounter //здесь мы указываем продвижения  progressBar - это кол-во выбранных элементов в списке
            val colorState = ColorStateList.valueOf(getProgressColorState(nameDayItems,
                binding.root.context))//создаём переменную colorState и передаём в неё функцию getProgressColorState(передаём item, context)
            progressBar.progressTintList = colorState //код который меняет в итоге цвет полоски
            counterCard.backgroundTintList = colorState //код который в итоге меняет цвет счётчика
            val counterText = "${nameDayItems.checkedItemsCounter} / ${nameDayItems.allItemCounter}" //создаём отдельную переменную, где мы составляем текст из отдельных частей
            tvCounter.text = counterText //тут мы показываем кол-во элементов в списке
            itemView.setOnClickListener {
                listener.onCLickItem(nameDayItems)
            }
            imDelete.setOnClickListener{
                listener.deleteItem(nameDayItems.id!!)
            }
            imEdit.setOnClickListener{
                listener.editItem(nameDayItems)
              }
        }

        private fun getProgressColorState(item: NameDayItems, context: Context): Int{ //функция для изменения текста ProgressBar
            return if(item.checkedItemsCounter == item.allItemCounter){ //возвращает если кол-во выбранных элементов равно кол-ву всех элементов то--
                ContextCompat.getColor(context, R.color.green_main) //цвет меняется на зелёный
            }else{ //в противном случае
                ContextCompat.getColor(context, R.color.red_main) //цвет меняется на красный
            }
        }

        companion object { //создаётся статическая функция
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_name_item,
                        parent,
                        false)) //возвращается разметка для дальнейшего заполнения
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<NameDayItems>() {
        override fun areItemsTheSame(
            oldItem: NameDayItems,
            newItem: NameDayItems,
        ): Boolean {//функция сравненивает элементы, если они похожи
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NameDayItems,
            newItem: NameDayItems,
        ): Boolean { //функция сравнивает весь контент
            return oldItem == newItem
        }

    }

    interface Listener {
        fun deleteItem(id: Int)
        fun editItem(dayListNameItem: NameDayItems)
        fun onCLickItem(dayListNameItem: NameDayItems)
    }

}