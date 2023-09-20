package com.example.myapplication.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.navigation.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun MenuScreen(
    navController: NavController,
    menuViewModel: MenuViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }

    menuViewModel.getNameSurname{n, s ->
        name = n!!
        surname = s!!
    }

    MyApplicationTheme {
        val scaffoldState = rememberScaffoldState(
            rememberDrawerState(DrawerValue.Closed)
        )
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                MyAppBar(
                    navController,
                    coroutineScope,
                    scaffoldState
                )
            },
            drawerContent = {
                MyDrawer(navController, menuViewModel, name, surname)
            }
        ) {
            Box(Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun MyAppBar(
    navController: NavController,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val title = navController.currentBackStackEntry?.destination?.route.toString()

    TopAppBar(
        title = { Text(title) },
        elevation = 8.dp,
        navigationIcon = {
            if (title === ROUTE_HOME)
                IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                    Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Open menu")
                }
            else
                IconButton(onClick = { navController.navigate(ROUTE_HOME) }) {
                    Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Go back")
                }
        },
    )
}

@ExperimentalMaterialApi
@Composable
private fun MyDrawer(navController: NavController, menuViewModel: MenuViewModel, name: String, surname: String) {
    Column {
        Surface(onClick = { navController.navigate(ROUTE_SETTINGS) }) {
            Row(
                Modifier
                    .background(MaterialTheme.colors.primary)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "$name $surname's profile image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = "$name $surname",
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.subtitle1
                )
            }
        }
        LazyColumn {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            menuViewModel.logoutUser()
                            navController.navigate(ROUTE_SIGNIN)
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Logout")
                    Icon(imageVector = Icons.Filled.Logout, contentDescription = null)
                }
            }
        }
    }
}