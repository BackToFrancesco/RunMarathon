package com.example.myapplication.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.navigation.ROUTE_STOPWATCH
import com.example.myapplication.presentation.MenuScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    navController: NavController,
    items: Array<Pair<String, Int>>,
    onItemClick: (Pair<String, Int>) -> Unit
) {
    MenuScreen(navController = navController) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.map { item ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemClick(item)
                                navController.navigate(ROUTE_STOPWATCH)
                            },
                        elevation = 2.dp
                    ) {
                        Box(Modifier.padding(16.dp)) {
                            Text(item.first)
                        }
                    }
                }
            }
        }
    }
}