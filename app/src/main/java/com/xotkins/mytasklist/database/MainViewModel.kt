package com.xotkins.mytasklist.database

import androidx.lifecycle.*
import com.xotkins.mytasklist.entities.DayListItem
import com.xotkins.mytasklist.entities.NameDayItems

import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
//Это всё архитектура MVVM
class MainViewModel(dataBase: MainDataBase): ViewModel() { //создается MVVM класс, в этот класс передается база данных(DataBase)
    val dao = dataBase.getDao()

    val allNameDayItems: LiveData<List<NameDayItems>> = dao.getAllNameDayItems().asLiveData()

    fun getAllItemsFromList(listId: Int): LiveData<List<DayListItem>>{
        return dao.getAllDayListItems(listId).asLiveData()
    }


    fun insertDayItem(dayListItem: DayListItem) = viewModelScope.launch {
        dao.insertDayItem(dayListItem)
    }


    fun insertDayListName(listNameItem: NameDayItems) = viewModelScope.launch {
        dao.insertDayListName(listNameItem)
    }


    fun updateListItem(item: DayListItem) = viewModelScope.launch {
        dao.updateListItem(item)
    }


    fun updateListName(nameDayItems: NameDayItems) = viewModelScope.launch {
        dao.updateDayListName(nameDayItems)
    }


    fun deleteItemByListId(id: Int ) = viewModelScope.launch{
        dao.deleteItemByListId(id)
    }


    fun deleteDayList(id: Int, deleteList: Boolean) = viewModelScope.launch {
        if (deleteList) dao.deleteDayListName(id)
        dao.deleteDayItemsByListId(id)
    }


    class MainViewModelFactory(val dataBase: MainDataBase) : ViewModelProvider.Factory{ //Создается класс для инициализации класса MainViewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST") //анотация
                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }

    }
}