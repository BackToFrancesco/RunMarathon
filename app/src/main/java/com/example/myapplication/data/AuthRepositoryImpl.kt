package com.example.myapplication.data


import android.util.Log
import com.example.myapplication.data.model.User
import com.example.myapplication.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun registerUser(user: User): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.createUserWithEmailAndPassword(
                user.email!!,
                user.password!!
            ).await()

            val id = firebaseAuth.currentUser!!.uid
            val dataMap = mapOf(
                "name" to user.name,
                "surname" to user.surname,
                "email" to user.email,
            )

            val database = Firebase.database.reference
            database.child("user").child(id).setValue(dataMap)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun logoutUser() {
        firebaseAuth.signOut()
    }

    override fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun getUserNameSurnameEmail(callback: (String?, String?, String?) -> Unit) {
        if(firebaseAuth.currentUser != null) {
            val id = firebaseAuth.currentUser!!.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("user/")
            databaseRef.child(id).get().addOnSuccessListener { task ->
                val name = task.child("name").getValue(String::class.java)
                val surname = task.child("surname").getValue(String::class.java)
                val email = task.child("email").getValue(String::class.java)
                callback(name, surname, email)
            }.addOnFailureListener {
                Log.e("Get user's data", "Error getting data.", it)
            }
        } else {
            Log.d("Get user's data", "No user signed in.")
        }
    }

    override fun updatePassword(currentPassword: String, newPassword: String, callback: (String) -> Unit) {
        if(firebaseAuth.currentUser != null) {
            val credential =
                EmailAuthProvider.getCredential(firebaseAuth.currentUser!!.email!!, currentPassword)
            firebaseAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (newPassword.length < 6) { //otherwise FirebaseAuthWeakPasswordException
                        callback("Password too short.")
                    } else {
                        firebaseAuth.currentUser!!.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    callback("Password updated.")
                                } else {
                                    callback("Error updating password.")
                                }
                            }
                    }
                } else {
                    callback("Current password is incorrect.")
                }
            }
        } else {
            Log.d("Get user's data", "No user signed in.")
        }
    }

}