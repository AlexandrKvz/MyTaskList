package com.xotkins.mytasklist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.xotkins.mytasklist.activities.MainApp
import com.xotkins.mytasklist.activities.DayListActivity
import com.xotkins.mytasklist.database.MainViewModel
import com.xotkins.mytasklist.database.NameDayItemsAdapter
import com.xotkins.mytasklist.databinding.FragmentDayListNamesBinding


import com.xotkins.mytasklist.dialogs.DeleteDialog
import com.xotkins.mytasklist.dialogs.NewListDialog
import com.xotkins.mytasklist.entities.NameDayItems
import com.xotkins.mytasklist.utils.TimeManager


class DayListNamesFragment : BaseFragment(), NameDayItemsAdapter.Listener {
    private lateinit var binding: FragmentDayListNamesBinding //создаём разметку
    private lateinit var adapter: NameDayItemsAdapter // создаём адаптер, куда записывам

    private val mainViewModel: MainViewModel by activityViewModels { //сюда передаётся класс ViewModel, база данных
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }


    override fun onClickNew() { //запускаем абстрактную функцию

        NewListDialog.showDialog(activity as AppCompatActivity, object: NewListDialog.Listener{
            override fun onClick(name: String) { //возвращаем имя списка которое выбрал пользователь
                val nameDayItems = NameDayItems(null, name, TimeManager.getCurrentTime(), 0, 0, "")
                mainViewModel.insertDayListName(nameDayItems) //вводим и заполняем данными и сохраняем это в базу данных
            }
        }, "") // при создании нового списка, значение пустое используется
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayListNamesBinding.inflate(inflater, container, false)
        return binding.root //показываем наш фрагмент с разметкой

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //эта функция запускается, когда уже созданы все View
        super.onViewCreated(view, savedInstanceState)
        initRcView() // запускается функция
        observer() //запускается функция
    }


    //функция где инициализируется RecyclerView и adapter
    private fun initRcView() = with(binding) {

        rcViewDayListName.layoutManager = LinearLayoutManager(activity)
        adapter = NameDayItemsAdapter(this@DayListNamesFragment)
        rcViewDayListName.adapter = adapter
    }

    private fun observer() { //функция которая следить за изменениями названий
        mainViewModel.allNameDayItems.observe(viewLifecycleOwner, {
            adapter.submitList(it) //сюда приходит обновленный список, который обновляет адаптер
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = DayListNamesFragment()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                mainViewModel.deleteDayList(id, true)
            }
        })
    }


    override fun editItem(nameDayItems: NameDayItems) {
        NewListDialog.showDialog(activity as AppCompatActivity, object : NewListDialog.Listener {
            override fun onClick(name: String) { //возвращаем имя списка которое выбрал пользователь
                mainViewModel.updateListName(nameDayItems.copy(name = name)) //вводим и заполняем данными и сохраняем новое название в базу данных
            }
        }, nameDayItems.name) //показывает старое название
    }

    override fun onCLickItem(nameDayItems: NameDayItems) {
        val i = Intent(activity, DayListActivity::class.java).apply {
            putExtra(DayListActivity.TASK_LIST_NAME, nameDayItems) // здесь мы передаём данные, какой список мы выбрали и отправляем в функцию fun init() в ShopListActivity
        }
        startActivity(i)
    }
}