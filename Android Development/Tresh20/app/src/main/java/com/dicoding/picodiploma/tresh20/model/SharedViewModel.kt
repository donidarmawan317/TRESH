package com.dicoding.picodiploma.tresh20.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.tresh20.data.Auth
import com.dicoding.picodiploma.tresh20.pref.UserPreference
import kotlinx.coroutines.launch

class SharedViewModel(private val pref: UserPreference) : ViewModel()  {
    fun getUser() : LiveData<Auth> {
        return pref.getUser().asLiveData()
    }

    fun saveUser(user: Auth) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}