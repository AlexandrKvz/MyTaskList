package com.xotkins.mytasklist.activities

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem

import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.xotkins.mytasklist.R
import com.xotkins.mytasklist.databinding.ActivityMainBinding
import com.xotkins.mytasklist.dialogs.DialogConst
import com.xotkins.mytasklist.dialogs.DialogHelper

import com.xotkins.mytasklist.dialogs.NewListDialog
import com.xotkins.mytasklist.fragments.DayListNamesFragment
import com.xotkins.mytasklist.fragments.FragmentManager
import com.xotkins.mytasklist.notification.PushService
import com.xotkins.mytasklist.settings.SettingsActivity

class MainActivity : AppCompatActivity(), NewListDialog.Listener, NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding // включение разметки  binding для активити
    private var currentMenuItemId = R.id.task_list //создаём переменную, чтобы обновлять фрагменты, после изменений настроек, поумолчанию она day_list
    private lateinit var defPref: SharedPreferences
    private  var currentTheme = "" // создаём переменную для обновления нашей темы, после изменения
    val mAuth = FirebaseAuth.getInstance()
    private val dialogHelper = DialogHelper(this)
    private lateinit var pushBroadcastReceiver: BroadcastReceiver
    private lateinit var tvAccount: TextView





    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)//инициализируем нашу переменную
        currentTheme = defPref.getString("theme_key", "green").toString()//выбор темы изначально
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(DayListNamesFragment.newInstance(), this) //когда запускаем приложение, будет открываться фрагмент DayListNameFragments
        setBottomNavListener()
        init()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(!task.isSuccessful){
                return@addOnCompleteListener
            }
            val token = task.result
            Log.e("TAG", "Token -> $token")
            val msg = getString(R.string.msg_token_fmt, token)
            Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
        }


    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun init() {

        var toggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    override fun onDestroy() {
        unregisterReceiver(pushBroadcastReceiver)
        super.onDestroy()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ac_sign_up -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }
            R.id.ac_sign_in -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }
            R.id.ac_sign_out -> {
                uiUpdate(null)
                mAuth.signOut()
            }
        }
        return true
    }
    fun uiUpdate(user: FirebaseUser?){ //ф-ция для обновления nav_header
        tvAccount.text = if(user == null){
            resources.getString(R.string.note_reg)
        }else{
            user.email
        }
    }

    private fun setBottomNavListener(){
        binding.mainContent.bNavMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings ->{
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                }
                R.id.task_list ->{
                    currentMenuItemId = R.id.task_list
                    FragmentManager.setFragment(DayListNamesFragment.newInstance(), this)
                }
                R.id.new_item ->{
                    FragmentManager.currentFrag?.onClickNew()

                }
            }
            true
        }
    }

    override fun onResume() {//вызываем функцию onResume(), это когда мы возвращаемся с наших настроек
        super.onResume()
        binding.mainContent.bNavMenu.selectedItemId = currentMenuItemId //указываем какая кнопка должна быть нажата
        if(defPref.getString("theme_key", "green") != currentTheme) recreate() //проверяем, если тема неравна нашей теме значит меняй тему, которую выбрали , в противном случае ничего не делаем
    }
    private fun getSelectedTheme(): Int{// функция для запуска темы из памяти настроек
        return if(defPref.getString("theme_key", "green") == "green"){ //если в памяти стоит green
            R.style.Theme_MyShoppingListGreen// то остаётся green
        }else{ //противном случае
            R.style.Theme_MyShoppingListBlue //ставится blue
        }
    }
    override fun onClick(name: String) {
    }
}