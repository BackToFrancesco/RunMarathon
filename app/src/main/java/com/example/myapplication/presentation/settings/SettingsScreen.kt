package com.example.myapplication.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.presentation.MenuScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import androidx.compose.ui.graphics.Color


@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    MenuScreen(navController = navController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var name by rememberSaveable { mutableStateOf("") }
            var surname by rememberSaveable { mutableStateOf("") }
            var email by rememberSaveable { mutableStateOf("") }
            var currentPassword by rememberSaveable { mutableStateOf("") }
            var newPassword by rememberSaveable { mutableStateOf("") }
            val passwordVisibility = remember { mutableStateOf(false) }


            settingsViewModel.getNameSurnameEmail{n, s, e ->
                name = n!!
                surname = s!!
                email = e!!
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(16.dp)
                    .width(100.dp)
                    .weight(weight = 50.0F)
            ) {
                Text(
                    "The firebase authentication is not provided. Look at the app code for implementing it.",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Text("Name: $name")

            Spacer(modifier = Modifier.padding(5.dp))

            Text("Surname: $surname")

            Spacer(modifier = Modifier.padding(5.dp))
            
            Text("Email: $email")

            Spacer(modifier = Modifier.padding(30.dp))


            OutlinedTextField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                },
                visualTransformation = if (passwordVisibility.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                label = { Text(text = "Current password") },
                placeholder = { Text(text = "Enter your current password") },
                trailingIcon = {
                    val image = if (passwordVisibility.value)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description =
                        if (passwordVisibility.value) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                        Icon(imageVector = image, description)
                    }
                }
            )

            Spacer(modifier = Modifier.padding(5.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                },
                visualTransformation = if (passwordVisibility.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                label = { Text(text = "New password") },
                placeholder = { Text(text = "Enter your new password") },
                trailingIcon = {
                    val image = if (passwordVisibility.value)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description =
                        if (passwordVisibility.value) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                        Icon(imageVector = image, description)
                    }
                }
            )

            Spacer(modifier = Modifier.padding(10.dp))

            Button(onClick = {
                settingsViewModel.updatePassword(currentPassword, newPassword){ feedback ->
                    Toast.makeText(context, "$feedback", Toast.LENGTH_LONG).show()
            }}) {
                Text(text = "UPDATE PASSWORD", fontSize = 18.sp)
            }

        }
    }
}