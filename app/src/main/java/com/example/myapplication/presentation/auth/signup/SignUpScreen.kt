package com.example.myapplication.presentation.auth.signup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.model.User
import com.example.myapplication.navigation.ROUTE_SIGNIN
import com.example.myapplication.navigation.ROUTE_SIGNUP
import com.example.myapplication.ui.theme.Blue
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.Yellow
import com.example.myapplication.ui.theme.fasterOne
import kotlinx.coroutines.launch


@OptIn(ExperimentalTextApi::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = signUpViewModel.signUpState.collectAsState(initial = null)
    val passwordVisibility = remember { mutableStateOf(false) }

    MyApplicationTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val GradientColors = listOf(Yellow, Blue)
                Text(
                    text = "RunMarathon",
                    fontFamily = fasterOne,
                    fontSize = 43.sp,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = GradientColors
                        )
                    )
                )
                Spacer(modifier = Modifier.padding(20.dp))
                OutlinedTextField(
                    value = name,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "nameIcon"
                        )
                    },
                    onValueChange = {
                        name = it
                    },
                    label = { Text(text = "Name") },
                    placeholder = { Text(text = "Enter your name") },
                )
                OutlinedTextField(
                    value = surname,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "nameIcon"
                        )
                    },
                    onValueChange = {
                        surname = it
                    },
                    label = { Text(text = "Surname") },
                    placeholder = { Text(text = "Enter your surname") },
                )
                OutlinedTextField(
                    value = email,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "emailIcon"
                        )
                    },
                    onValueChange = {
                        email = it
                    },
                    label = { Text(text = "Email address") },
                    placeholder = { Text(text = "Enter your email") },
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    visualTransformation = if (passwordVisibility.value) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    label = { Text(text = "Password") },
                    placeholder = { Text(text = "Enter your password") },
                    trailingIcon = {
                        val image = if (passwordVisibility.value)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description =
                            if (passwordVisibility.value) "Hide password" else "Show password"
                        IconButton(onClick = {
                            passwordVisibility.value = !passwordVisibility.value
                        }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )

                Spacer(modifier = Modifier.padding(10.dp))
                Button(onClick = {
                    if (name == "" || surname == "" || email == "" || password == "") {
                        Toast.makeText(context, "All fields are mandatory", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        signUpViewModel.registerUser(User(name, surname, email, password))
                    }
                }) {
                    Text(text = "SIGN UP", fontSize = 18.sp)
                }

                TextButton(onClick = {
                    navController.navigate(ROUTE_SIGNIN) {
                        popUpTo(ROUTE_SIGNUP) { inclusive = true }
                    }
                }) {
                    Text(text = "Have an account already? Sign in!")
                }

                LaunchedEffect(key1 = state.value?.isSuccess) {
                    scope.launch {
                        if (state.value?.isSuccess?.isNotEmpty() == true) {
                            val success = state.value?.isSuccess
                            Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_SIGNIN) {
                                popUpTo(ROUTE_SIGNUP) { inclusive = true }
                            }
                        }
                    }
                }
                LaunchedEffect(key1 = state.value?.isError) {
                    scope.launch {
                        if (state.value?.isError?.isNotBlank() == true) {
                            val error = state.value?.isError
                            Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(rememberNavController())
}