package com.example.myapplication.presentation.stopwatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.navigation.ROUTE_HOME
import androidx.navigation.NavController
import com.example.myapplication.navigation.ROUTE_ARRIVAL
import com.example.myapplication.presentation.MenuScreen
import com.example.myapplication.presentation.beacon.BeaconViewModel
import com.example.myapplication.presentation.beacon.MarathonStatus
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.time.LocalTime
import kotlin.time.Duration
import com.example.myapplication.util.formatTime

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun StopwatchScreen(
    navController: NavController,
    time: Duration,
    timeIntervals: List<Duration>,
    onStartClick: () -> Unit,
    onIntervalClick: () -> Unit,
    onResetClick: () -> Unit,
    beaconViewModel: BeaconViewModel = hiltViewModel(),
) {
    MenuScreen(navController = navController) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Stopwatch(time)

            Spacer(Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for ((cnt, beacon) in beaconViewModel.beaconState.value.beaconPassed.withIndex()) {
                    item {
                        TimeInterval(cnt+1, beacon!!.durationTime)
                    }
                }
            }

            autoStartMarathon(beaconViewModel, onStartClick)

            autoStopMarathon(beaconViewModel, onResetClick)

            fetchingBeaconsLoadingDialog(beaconViewModel, navController)

            pushBeaconsToServerLoadingDialog(beaconViewModel)

            marathonNotValidDialog(beaconViewModel, navController)

            redirectionToLeaderboard(beaconViewModel, navController)

            endMarathonDialog(beaconViewModel, navController)

            //for debug
            //debugRangingBeacon(beaconViewModel = beaconViewModel, navController)
        }
    }
}

@Composable
private fun redirectionToLeaderboard(
    beaconViewModel: BeaconViewModel,
    navController: NavController
) {
    var isRedirected by remember {
        mutableStateOf(false)
    }
    if (beaconViewModel.beaconState.value.marathonStatus == MarathonStatus.VALID && !isRedirected) {
        isRedirected = true
        navController.navigate(ROUTE_ARRIVAL)
    }
}

@Composable
private fun marathonNotValidDialog(
    beaconViewModel: BeaconViewModel,
    navController: NavController
) {
    if (beaconViewModel.beaconState.value.marathonStatus == MarathonStatus.NOTVALID)
        AlertDialog(
            title = { Text(text = "Marathon not valid")},
            onDismissRequest = {},
            text = {
                Text("The marathon is not valid, not all checkpoints have been passed correctly")
            },
            buttons = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
                {
                    Button(
                        modifier = Modifier
                            .width(250.dp)
                            .height(60.dp)
                            .padding(8.dp),
                        onClick = { navController.navigate(ROUTE_HOME) }) {
                        Text(text = "Back to the home")
                    }
                }
            }
        )
}

@Composable
private fun pushBeaconsToServerLoadingDialog(beaconViewModel: BeaconViewModel) {
    if (beaconViewModel.beaconState.value.marathonStatus == MarathonStatus.INUPLOAD)
        DialogBoxLoading()
}

@Composable
private fun autoStopMarathon(
    beaconViewModel: BeaconViewModel,
    onResetClick: () -> Unit
) {
    if (beaconViewModel.beaconState.value.isMarathonEnded)
        onResetClick()
}

@Composable
private fun autoStartMarathon(
    beaconViewModel: BeaconViewModel,
    onStartClick: () -> Unit
) {
    var isActive by remember {
        mutableStateOf(false)
    }
    if (beaconViewModel.beaconState.value.isMarathonStarted && !isActive) {
        isActive = false
        onStartClick()
    }
}

@Composable
private fun Stopwatch(time: Duration) {
    @Composable
    fun StopwatchBox(text: String) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp
        ) {
            Box(
                Modifier
                    .background(MaterialTheme.colors.primary)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Text(
                    text = text,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
    val (hours, minutes, seconds) = time.toComponents { hours, minutes, seconds, _ ->
        arrayOf(hours, minutes, seconds)
            .map { el -> el.toString().padStart(2, '0') }
    }

    ProvideTextStyle(TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    )) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            StopwatchBox(hours)
            Text(":")
            StopwatchBox(minutes)
            Text(":")
            StopwatchBox(seconds)
        }
    }
}

@Composable
private fun TimeInterval(index: Number, time: LocalTime) {

    var (h, m, s) = formatTime(time)

    val str = "$h : $m : $s"
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color(0xFF0600B4)), shape = RoundedCornerShape(10.dp)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Checkpoint n. $index")
            Text(str)
        }
    }
}

@Composable
fun debugRangingBeacon(beaconViewModel: BeaconViewModel, navController: NavController){

    // for debug

    var isRanging by remember {
        mutableStateOf(false)
    }



    // ranging button

    Button(
        onClick = {
            if(!isRanging)
                beaconViewModel.startListeningForBeacon()
            else
                beaconViewModel.stopListeningForBeacon()

            isRanging = !isRanging
        }) {
        Text(text = if(!isRanging) "Start ranging" else "Stop ranging")
    }

    // output passed beacons
    val beaconState = beaconViewModel.beaconState.value
    Text(text = "ErrorInFetching ${beaconState.fetchingError}")
    beaconState.beaconPassed.forEach{
        Text(text = "----------------------------------------------------")
        Text(text = "ID:${it?.id}", color = MaterialTheme.colors.secondary)
        Text(text = "TIME:${it?.durationTime}", color = MaterialTheme.colors.secondary)
        Text(text = "TIMESTAMP:${it?.dayTime}", color = MaterialTheme.colors.secondary)
        Text(text = "DISTANCE:${it?.distance}", color = MaterialTheme.colors.secondary)
        if(it!= null) {
            if (it.isTheFirstOne)
                Text(text = "isTheFirstOne", color = MaterialTheme.colors.secondary)
            if (it.isTheLastOne)
                Text(text = "isTheLastOne", color = MaterialTheme.colors.secondary)
        }
    }

    Button(onClick = { beaconViewModel.setMarathonID(10) }) {
        Text(text = "Set new ID Marathon")
    }
    Text(text = "${beaconViewModel.beaconState.value.idMarathon}")
}

@Composable
private fun endMarathonDialog(
    beaconViewModel: BeaconViewModel,
    navController: NavController
) {
    var endMarathonDialogIsOpen by remember {
        mutableStateOf(true)
    }

    if (!beaconViewModel.beaconState.value.isMarathonEnded)
        endMarathonDialogIsOpen = true

    // dialog end marathon

    if (endMarathonDialogIsOpen && beaconViewModel.beaconState.value.isMarathonEnded && beaconViewModel.beaconState.value.marathonStatus != MarathonStatus.VALID) {
        AlertDialog(
            title= {Text("Do you want to send your data?")},
            onDismissRequest = { endMarathonDialogIsOpen = false },
            text = {
                Text("Congratulations, you have completed the marathon!\nTap on 'Send' to send the stats of your marathon to compete in the leaderboard or tap on 'Keep' to not share data.")
            },
            buttons = {
                // Use a row to arrange the buttons horizontally
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    // Swap the order of confirm and dismiss buttons
                    Button(
                        modifier = Modifier
                            .width(150.dp)
                            .height(60.dp)
                            .padding(8.dp),
                        onClick = {
                        endMarathonDialogIsOpen = false;
                        beaconViewModel.pushBeaconsToServer();
                    },
                    ) {
                        Text("Send")
                    }
                    Button(
                        modifier = Modifier
                            .width(150.dp)
                            .height(60.dp)
                            .padding(8.dp)
                        ,
                        onClick = {
                            endMarathonDialogIsOpen = false
                            navController.navigate(ROUTE_ARRIVAL)
                        }) {
                        Text("Keep")
                    }}
            },
        )
    }
}

@Composable
private fun fetchingBeaconsLoadingDialog(
    beaconViewModel: BeaconViewModel,
    navController: NavController
) {
    // try fetching beacons from server
    if (beaconViewModel.beaconState.value.isFetching) {
        DialogBoxLoading()
    }

    if (beaconViewModel.beaconState.value.fetchingError) {
        AlertDialog(
            title = { Text(text = "Error connection")},
            onDismissRequest = {},
            text = {
                Text("Error connection with server. Impossible start the marathon")
            },
            buttons = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
                {
                    Button(
                        modifier = Modifier
                            .width(250.dp)
                            .height(60.dp)
                            .padding(8.dp),
                        onClick = { navController.navigate(ROUTE_HOME) }) {
                        Text(text = "Back to the home")
                    }
                }
            }
        )
    }
}

@Composable
fun DialogBoxLoading(
    cornerRadius: Dp = 16.dp,
    paddingStart: Dp = 56.dp,
    paddingEnd: Dp = 56.dp,
    paddingTop: Dp = 32.dp,
    paddingBottom: Dp = 32.dp,
    progressIndicatorColor: Color = Color(0xFF35898f),
    progressIndicatorSize: Dp = 80.dp
) {

    Dialog(
        onDismissRequest = {
        }
    ) {
        Surface(
            elevation = 4.dp,
            shape = RoundedCornerShape(cornerRadius)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = paddingStart, end = paddingEnd, top = paddingTop),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProgressIndicatorLoading(
                    progressIndicatorSize = progressIndicatorSize,
                    progressIndicatorColor = progressIndicatorColor
                )

                // Gap between progress indicator and text
                Spacer(modifier = Modifier.height(32.dp))

                // Please wait text
                Text(
                    modifier = Modifier
                        .padding(bottom = paddingBottom),
                    text = "Please wait...",
                    style = TextStyle(
                        //color = Color.White,
                        fontSize = 16.sp,
                    )
                )
            }
        }
    }
}

@Composable
fun ProgressIndicatorLoading(progressIndicatorSize: Dp, progressIndicatorColor: Color) {

    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
            }
        )
    )

    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                12.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White, // add background color first
                        progressIndicatorColor.copy(alpha = 0.1f),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = Color.White // Set background color
    )
}
