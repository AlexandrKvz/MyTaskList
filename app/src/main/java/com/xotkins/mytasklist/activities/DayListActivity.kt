package com.xotkins.mytasklist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xotkins.mytasklist.databinding.ActivityDayListBinding
import com.xotkins.mytasklist.R
import com.xotkins.mytasklist.database.MainViewModel
import com.xotkins.mytasklist.database.DayListItemAdapter
import com.xotkins.mytasklist.dialogs.EditListItemDialog
import com.xotkins.mytasklist.entities.DayListItem
import com.xotkins.mytasklist.entities.NameDayItems
import com.xotkins.mytasklist.utils.ShareHelper

class DayListActivity : AppCompatActivity(), DayListItemAdapter.Listener {
    private lateinit var binding: ActivityDayListBinding
    private var nameDayItems: NameDayItems? = null //создаётся переменная, чтобы, когда нажимаем на список появлялись все элементы списка
    private lateinit var saveItem: MenuItem // создаём переменную для кнопки Save
    private var edItem: EditText? = null //создаём переменную чтобы присвоить разметку EditText из app:actionLayout="@layout/edit_action_layout"
    private var adapter: DayListItemAdapter? = null /// создаём адаптер, куда записывам

    private lateinit var defPref: SharedPreferences

    private val mainViewModel: MainViewModel by viewModels { //сюда передаётся класс ViewModel, база данных
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayListBinding.inflate(layoutInflater)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme()) //
        setContentView(binding.root)
        initRcView() //запускаем функцию
        init() //запускаем функцию
        listItemObserver() //запускаем функцию
        actionBarSettings() //запускаем функцию

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // создаём функцию для подключения меню
        menuInflater.inflate(R.menu.task_list_menu, menu) //надуваем(вызываем) нашу разметку
        saveItem = menu?.findItem(R.id.save_item)!! //здесь мы находим эту кнопку Save item на экране
        val newItems = menu.findItem(R.id.new_items)!! //здесь мы находим эту кнопку New items на экране
        edItem = newItems.actionView.findViewById(R.id.edNewShopItem) as EditText
        newItems.setOnActionExpandListener(expandActionView()) //добавляем New items слушатель нажатия
        saveItem.isVisible = false // здесь делаем кнопки Save не видимой


        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean { //создаём слушатель нажатия на кнопку
        when (item.itemId) {//проверяем условие,
            R.id.save_item -> {  //если жмём на кнопку Save
                addNewTaskItem(edItem?.text.toString()) //то запускаем эту функцию
            }
            R.id.delete_list -> { //удалить список
                mainViewModel.deleteDayList(nameDayItems?.id!!, true)
                finish()
            }
            R.id.clear_list -> { //очищение списка
                mainViewModel.deleteDayList(nameDayItems?.id!!, true)
            }
            R.id.share_list -> { //передать список
                startActivity(Intent.createChooser( //с помощью createChooser выбираем через какую соц.сеть хотим передать наш список
                    ShareHelper.shareDayList(adapter?.currentList!!, nameDayItems?.name!!),
                "Share by" //поделиться с помощью
                ))
            }
            android.R.id.home ->{ //если нажимаем на стрелку назад, то выходим и обновляем экран с последними изменениями
                saveItemCount()
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewTaskItem(name: String){ //создаём функцию, которая записывает наш элемент в базу данных
        if(name.isEmpty())return //проверяет если какой-нибудь текст, если нет то ничего не происходит
        val item = DayListItem(null, name, "", false, nameDayItems?.id!!, 0) //тут вписываем элемент и сохраняем его в базу данных
        edItem?.setText("") //после того как мы сохранили наш элемент в список, мы строку для ввода элемента очищаем
        mainViewModel.insertDayItem(item) //передаём эти данные в базу данных для записи
    }

    private fun listItemObserver(){ //функция для обновления нашего списка покупок, где обновляются элементы этого списка
        mainViewModel.getAllItemsFromList(nameDayItems?.id!!).observe(this, {
            adapter?.submitList(it) // сюда приходит новый список
            binding.tvEmpty.visibility = if(it.isEmpty()){ //проверяем условие, если список у нас пустой ---
                View.VISIBLE //то показываем тескт Empty
            } else { //в противном случае, если список у нас не пустой ---
                View.GONE //убираем текст Empty
            }
        })

    }



    //функция где инициализируется RecyclerView и adapter
    private  fun initRcView() = with(binding){

        adapter = DayListItemAdapter(this@DayListActivity) //здесь инициализируем адаптер, и передаем listener конкретного фрагмента
        rcView.layoutManager = LinearLayoutManager(this@DayListActivity)  //здесь делаем чтобы RecyclerView шёл списком
        rcView.adapter = adapter //передаём адаптер в rcView
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener{ //создаём функцию, чтобы слушатель Save замечал, что наш ActionView появился для заполнения текста
        return object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean { //ActionView появился для заполнения текста
                saveItem.isVisible = true // здесь делаем кнопку Save видимой
                mainViewModel.getAllItemsFromList(nameDayItems?.id!!).removeObservers(this@DayListActivity)//здесь убираем observer
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean { //ActionView закрылся для заполнения текста
                saveItem.isVisible = false // здесь делаем кнопки Save невидимой
                invalidateOptionsMenu() // возвращаем меню, где видима кнопка только New items
                edItem?.setText("")//делаем пустую строку
                listItemObserver()//запускаем функцию
                return true
            }

        }
    }


    private fun init(){ //функция, чтобы получать данный список, который мы открыли
        nameDayItems = intent.getSerializableExtra(TASK_LIST_NAME) as NameDayItems //здесь мы получаем данные

    }

    companion object{
        const val TASK_LIST_NAME = "task_list_name"
    }

    override fun deleteItem(id: Int) {//функция удаения элемента из списка покупок
        mainViewModel.deleteItemByListId(id)//перезаписывает наш список
    }



    override fun onCLickItem(taskListItem: DayListItem, state: Int) { //функция запускается  в fun setItemData
        when(state){ //проверяем условие, что было нажато, если --
            DayListItemAdapter.DELETE -> deleteItem(taskListItem.id!!)//удаляет элемент из списка покупок
            DayListItemAdapter.CHECK_BOX -> mainViewModel.updateListItem(taskListItem) // запись элементов из списка в базу данных
            DayListItemAdapter.EDIT -> editListItem(taskListItem) // редактирование элементов из списка в базу данных - запускаем функцию


        }
    }
    private fun editListItem(item: DayListItem){ //функция для проверки что нажали, если нажали Edit
        EditListItemDialog.showDialog(this, item, object : EditListItemDialog.Listener{ //запускается диалог для редактирования
            override fun onClick(item: DayListItem) {
                mainViewModel.updateListItem(item) //здесь перезаписывает наш элемент
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCounter = 0 //счётчик отмеченных элементов
        //перебираем элементы в данном списке, который находится в данный момент в адаптере с помощью цикла
        adapter?.currentList?.forEach {
            if(it.itemChecked) checkedItemCounter++ //если есть отмеченные элементы в списке то увеличиваем счётчик
        }
        val tempDayListNameItem = nameDayItems?.copy(
            allItemCounter = adapter?.itemCount!!, //здесь вы записали в allItemCounter сколько сейчас в данный момент находится элементов в нашем списке
            checkedItemsCounter = checkedItemCounter //здесь передаём кол-во отмеченных элементов из списка
        )
        mainViewModel.updateListName(tempDayListNameItem!!) //обновляем список  передавая значение tempShopListNameItem
    }

    private fun actionBarSettings() { //функция для настройки, здесь мы делаем возврат  на предыдущую страницу нажав кнопку стрелка
        val ab = supportActionBar
        saveItemCount()
        ab?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onBackPressed() {
       saveItemCount() //запускаем функцию
        super.onBackPressed()
    }

    private fun getSelectedTheme(): Int{// функция для запуска темы из памяти настроек
        return if(defPref.getString("theme_key", "green") == "green"){ //если в памяти стоит green
            R.style.Theme_MyShoppingListGreen // то остаётся green
        }else{ //противном случае
            R.style.Theme_MyShoppingListBlue //ставится blue
        }
    }
}