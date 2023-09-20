package com.example.myapplication.presentation.auth.signup

sealed class SignUpEvent{
    data class EnteredEmail(val value: String): SignUpEvent()
    data class EnteredPassword(val value: String): SignUpEvent()
    object RegisterUser : SignUpEvent()
}
