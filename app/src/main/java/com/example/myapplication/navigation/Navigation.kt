package com.example.myapplication.navigation

import StopwatchService
import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.arrival.ArrivalScreen
import com.example.myapplication.presentation.auth.SignInScreen
import com.example.myapplication.presentation.auth.signup.SignUpScreen
import com.example.myapplication.presentation.beacon.BeaconViewModel
import com.example.myapplication.presentation.home.HomeScreen
import com.example.myapplication.presentation.settings.RankingScreen
import com.example.myapplication.presentation.settings.SettingsScreen
import com.example.myapplication.presentation.stopwatch.StopwatchScreen
import com.google.accompanist.permissions.*


@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun Navigation(
    beaconViewModel: BeaconViewModel = hiltViewModel(),
) {

    val navController = rememberNavController()

    val list = arrayOf(Pair("Marathon #1",1), Pair("Marathon #2",2))
    val stopWatch = mutableMapOf<String, StopwatchService>()
    for (item in list) {
        stopWatch[item.first] = remember { StopwatchService() }
    }
    var current by remember { mutableStateOf(list[0]) }

    // change ROUTE_HOME with ROUTE_SIGNIN for login features
    NavHost(navController, startDestination = ROUTE_HOME) {
        composable(route = ROUTE_HOME) {
            HomeScreen(
                navController = navController,
                items = list,
                onItemClick = {
                        item -> current = item
                        if(beaconViewModel.beaconState.value.idMarathon != current.second)
                            beaconViewModel.setMarathonID(current.second)
                }
            )
        }
        composable(route = ROUTE_SIGNIN) {
            SignInScreen(navController = navController)
        }
        composable(route = ROUTE_SIGNUP) {
            SignUpScreen(navController = navController)
        }
        composable(route = ROUTE_SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(route = ROUTE_RANKING) {
            RankingScreen(navController = navController)
        }
        composable(route = ROUTE_ARRIVAL) {
            ArrivalScreen(navController = navController, finalTime = beaconViewModel.beaconState.value.beaconPassed.last()!!.durationTime)
        }
        composable(route = ROUTE_STOPWATCH) {
            stopWatch[current.first]?.let { it1 ->
                StopwatchScreen(
                    navController = navController,
                    time = it1.time,
                    timeIntervals = it1.timeIntervals,
                    onStartClick = it1::start,
                    onIntervalClick = it1::addInterval,
                    onResetClick = it1::reset
                )
            }
            //asks permissions
            val permissionsState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.FOREGROUND_SERVICE,
                )
            )

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(
                key1 = lifecycleOwner,
                effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
            )
            //DebugPermissions(permissionsState = permissionsState)
        }
    }
}

// for debug purpose
@ExperimentalPermissionsApi
@Composable
fun DebugPermissions(permissionsState: MultiplePermissionsState){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            permissionsState.launchMultiplePermissionRequest()
        }) {
            Text(text = "Request")
        }
        permissionsState.permissions.forEach { perm ->
            when (perm.permission) {
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    when {
                        perm.status.isGranted -> {
                            Text(text = "ACCESS_FINE_LOCATION accepted")
                        }
                        perm.status.shouldShowRationale -> {
                            Text(text = "ACCESS_FINE_LOCATION is needed")
                        }
                        !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                            Text(text = "ACCESS_FINE_LOCATION has been permanently denied. You can enable it in the app settings.")
                        }
                    }
                }
                Manifest.permission.FOREGROUND_SERVICE -> {
                    when {
                        perm.status.isGranted -> {
                            Text(text = "FOREGROUND_SERVICE accepted")
                        }
                        perm.status.shouldShowRationale -> {
                            Text(text = "FOREGROUND_SERVICE is needed")
                        }
                        !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                            Text(text = "FOREGROUND_SERVICE has been permanently denied. You can enable it in the app settings.")
                            Text(text = "FOREGROUND_SERVICE has been permanently denied. You can enable it in the app settings.")
                        }
                    }
                }
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    when {
                        perm.status.isGranted -> {
                            Text(text = "ACCESS_COARSE_LOCATION accepted")
                        }
                        perm.status.shouldShowRationale -> {
                            Text(text = "ACCESS_COARSE_LOCATION is needed")
                        }
                        !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                            Text(text = "ACCESS_COARSE_LOCATION has been permanently denied. You can enable it in the app settings.")
                        }
                    }
                }
                Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                    when {
                        perm.status.isGranted -> {
                            Text(text = "ACCESS_BACKGROUND_LOCATION accepted")
                        }
                        perm.status.shouldShowRationale -> {
                            Text(text = "ACCESS_BACKGROUND_LOCATION is needed")
                        }
                        !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                            Text(text = "ACCESS_BACKGROUND_LOCATION has been permanently denied. You can enable it in the app settings.")
                        }
                    }
                }
                Manifest.permission.BLUETOOTH_SCAN -> {
                    when {
                        perm.status.isGranted -> {
                            Text(text = "BLUETOOTH_SCAN accepted")
                        }
                        perm.status.shouldShowRationale -> {
                            Text(text = "BLUETOOTH_SCAN is needed")
                        }
                        !perm.status.isGranted && !perm.status.shouldShowRationale -> {
                            Text(text = "BLUETOOTH_SCAN has been permanently denied. You can enable it in the app settings.")
                        }
                    }
                }
            }
        }
    }
}