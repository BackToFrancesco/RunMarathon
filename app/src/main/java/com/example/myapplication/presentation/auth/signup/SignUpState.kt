package com.example.myapplication.presentation.auth.signup

data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)
