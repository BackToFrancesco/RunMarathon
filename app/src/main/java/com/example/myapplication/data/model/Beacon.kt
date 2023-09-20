package com.example.myapplication.data.model


import java.time.LocalTime
import java.util.*

data class Beacon(
    val id: UUID,
    val distance: Double,
    val dayTime: LocalTime, //dayTime
    val isTheFirstOne: Boolean = false,
    val isTheLastOne : Boolean = false,
    val durationTime: LocalTime //durationTime
)
