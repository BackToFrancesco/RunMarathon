package com.example.myapplication.presentation.arrival

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.navigation.ROUTE_RANKING
import com.example.myapplication.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.time.LocalTime
import com.example.myapplication.util.formatTime

@OptIn(ExperimentalTextApi::class)
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun ArrivalScreen(
    navController: NavController,
    finalTime: LocalTime
) {

    var (h, m, s) = formatTime(finalTime)

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val GradientColors = listOf(Yellow, Blue)
            Text(
                text = "CONGRATULATIONS!",
                fontFamily = anton,
                fontSize = 50.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = GradientColors
                    )
                )
            )
            Text(
                text = "You have successfully completed the marathon in",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(30.dp))
            Text(
                text = "$h:$m:$s",
                fontFamily = orbitron,
                fontSize = 65.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.padding(30.dp))
            Text(
                text = "To check your position in the ranking, press the button below:",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = {
                navController.navigate(ROUTE_RANKING)
            }) {
                Text(text = "GOT TO THE RANKING", fontSize = 18.sp)
            }
        }
    }
}