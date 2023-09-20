package com.example.myapplication.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.data.model.User
import com.example.myapplication.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    val _signUpState  = Channel<SignUpState>()
    val signUpState  = _signUpState.receiveAsFlow()


    fun registerUser(user: User) = viewModelScope.launch {
        /* Uncomment this line to use Firebase AUTH
        repository.registerUser(user).collect{result ->
            when(result){
                is Resource.Success ->{
                    _signUpState.send(SignUpState(isSuccess = "Successfully signed up."))
                }
                is Resource.Loading ->{
                    _signUpState.send(SignUpState(isLoading = true))
                }
                is Resource.Error ->{
                    _signUpState.send(SignUpState(isError = result.message))
                }
            }
        }*/

        _signUpState.send(SignUpState(isSuccess = "Successfully signed up."))
    }
}