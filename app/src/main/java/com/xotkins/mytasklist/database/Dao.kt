package com.xotkins.mytasklist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xotkins.mytasklist.entities.DayListItem
import com.xotkins.mytasklist.entities.NameDayItems
import kotlinx.coroutines.flow.Flow


@Dao //интерфейс Dаo (Data access object)
interface Dao { // это интерфейс


    @Query("SELECT * FROM name_day_items")
    fun getAllNameDayItems(): Flow<List<NameDayItems>>

    @Query("SELECT * FROM day_list_item WHERE listId LIKE :listId")
    fun getAllDayListItems(listId: Int): Flow<List<DayListItem>>

    @Query("DELETE FROM day_list_item WHERE listId LIKE :listId")
    suspend fun deleteDayItemsByListId(listId: Int)

    @Query("DELETE FROM day_list_item WHERE id LIKE :id")
    suspend fun deleteItemByListId(id: Int)

    @Insert
    suspend fun insertDayListName(nameItem: NameDayItems)

    @Query("DELETE FROM name_day_items WHERE id IS :id")
    suspend fun deleteDayListName(id: Int)


    @Update
    suspend fun updateListItem(item: DayListItem)

    @Update
    suspend fun updateDayListName(dayListNameItem: NameDayItems)

    @Insert
    suspend fun insertDayItem(dayListItem: DayListItem)
}