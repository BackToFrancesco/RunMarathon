package com.example.myapplication.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun getNameSurnameEmail(callback: (name: String?, surname: String?, email: String?) -> Unit) = viewModelScope.launch {

        repository.getUserNameSurnameEmail { name, surname, email ->
            callback(name, surname, email)
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String, callback: (String) -> Unit) = viewModelScope.launch {
        repository.updatePassword(currentPassword, newPassword, callback)
    }
}