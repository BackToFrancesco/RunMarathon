package com.example.myapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun getNameSurname(callback: (name: String?, surname: String?) -> Unit) = viewModelScope.launch {
        repository.getUserNameSurnameEmail { name, surname, email ->
            callback(name, surname)
        }
    }

    fun logoutUser(){
        repository.logoutUser()
    }


}