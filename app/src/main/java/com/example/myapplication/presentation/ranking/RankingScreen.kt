package com.example.myapplication.presentation.settings


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.Rank
import com.example.myapplication.presentation.MenuScreen
import com.example.myapplication.presentation.ranking.RankingViewModel
import com.example.myapplication.util.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun TableScreen(rankingList: List<Rank>) {
    // Each cell of a column must have the same weight.
    val column1Weight = .1f // 10%
    val column2Weight = .6f // 60%
    val column3Weight = .3f // 30%
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        // header
        item {
            Row(Modifier.background(MaterialTheme.colors.secondary)) {
                TableCell(text = "", weight = column1Weight)
                TableCell(text = "Athlete", weight = column2Weight)
                TableCell(text = "Time", weight = column3Weight)
            }
        }
        // body
        var position = 1
        items(rankingList) {
            val (id, name, surname, totalDurationTime) = it
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = (position++).toString(), weight = column1Weight)
                TableCell(text = name + " " +  surname, weight = column2Weight)
                val totalSeconds = 86399
                val hours = totalDurationTime!! / 3600
                val minutes = (totalDurationTime!! % 3600) / 60
                val seconds = totalDurationTime!! % 60
                TableCell(text = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}", weight = column3Weight)
            }
        }
    }
}



@SuppressLint("CoroutineCreationDuringComposition")
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun RankingScreen(
    navController: NavController,
    rankingViewModel: RankingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var rankingList by remember { mutableStateOf<List<Rank>?>(null) }
    /* Uncomment this for use Firebase
    val idMarathon = rankingViewModel.getModelState().idMarathon

    GlobalScope.launch {
        try {
            val ranking = rankingViewModel.getRanking(rankingViewModel.getModelState().idMarathon)
            rankingList = ranking.await()
        } catch (e: Exception) {
        }
    }
     */
    rankingList = Constants.rankingList


    MenuScreen(navController = navController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (rankingList != null) {
                Button(onClick = {
                    GlobalScope.launch {
                        try {
                            /* Uncomment this for use Firebase
                            val ranking = rankingViewModel.getRanking(idMarathon)
                            rankingList = ranking.await()
                            */
                        } catch (e: Exception) {
                        }
                    }
                }){
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
                TableScreen(rankingList!!)
            } else {
                Text(text = "Loading...")
            }
        }
    }
}
