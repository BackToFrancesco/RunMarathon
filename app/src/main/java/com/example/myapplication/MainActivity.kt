package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import com.example.myapplication.navigation.Navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        //functions = Firebase.functions("us-central1")
        /// for debug
        //functions.useEmulator("127.0.0.1", 5001);
        super.onCreate(savedInstanceState)
        setContent {
            Navigation()
        }

    }
}
