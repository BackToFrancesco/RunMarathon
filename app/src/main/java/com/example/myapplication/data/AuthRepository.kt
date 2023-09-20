package com.example.myapplication.data

import com.example.myapplication.data.model.User
import com.example.myapplication.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(user: User): Flow<Resource<AuthResult>>
    fun logoutUser()
    fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>
    fun getUserNameSurnameEmail(callback: (String?, String?, String?) -> Unit)
    fun updatePassword(currentPassword: String, newPassword: String, callback: (String) -> Unit)

}