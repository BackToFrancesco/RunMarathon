package com.example.myapplication.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.myapplication.R
import com.example.myapplication.navigation.ROUTE_HOME
import com.example.myapplication.navigation.ROUTE_SIGNIN
import com.example.myapplication.navigation.ROUTE_SIGNUP
import com.example.myapplication.presentation.auth.signin.SignInViewModel
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalTextApi::class)
@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel = hiltViewModel()
) {
    var email by rememberSaveable { mutableStateOf("admin@admin.com") }
    var password by rememberSaveable { mutableStateOf("password") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = signInViewModel.signInState.collectAsState(initial = null)


    val passwordVisibility = remember { mutableStateOf(false) }

    MyApplicationTheme {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo of the application",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp)
                )
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

                Spacer(modifier = Modifier.padding(30.dp))

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
                    placeholder = { Text(text = "Enter your e-mail") }
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

                Spacer(modifier = Modifier.padding(25.dp))

                Button(onClick = {
                    scope.launch {
                        signInViewModel.loginUser(email, password)
                    }
                }) {
                    Text(text = "SIGN IN", fontSize = 18.sp)
                }

                TextButton(onClick = {
                    navController.navigate(ROUTE_SIGNUP) {
                        popUpTo(ROUTE_SIGNIN) { inclusive = true }
                    }
                }) {
                    Text(text = "Don't have an account yet? Sign up!")
                }

                LaunchedEffect(key1 = state.value?.isSuccess) {
                    scope.launch {
                        if (state.value?.isSuccess?.isNotEmpty() == true) {
                            //val success = state.value?.isSuccess
                            //Toast.makeText(context, "${success}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_HOME) {
                                popUpTo(ROUTE_SIGNIN) { inclusive = true }
                            }
                        }
                    }
                }

                LaunchedEffect(key1 = state.value?.isError) {
                    scope.launch {
                        if (state.value?.isError?.isNotEmpty() == true) {
                            val error = state.value?.isError
                            Toast.makeText(context, "${error}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme() {
        SignInScreen(rememberNavController())
    }
}