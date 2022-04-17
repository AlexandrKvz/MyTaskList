package com.xotkins.mytasklist.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.xotkins.mytasklist.R

//Создаём класс SettingsFragment наследуемый от PreferenceFragmentCompat() -- этот класс отвечает за разметку
class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey) //выбираем из какого ресурса будет запущен экран настроек
    }
}
