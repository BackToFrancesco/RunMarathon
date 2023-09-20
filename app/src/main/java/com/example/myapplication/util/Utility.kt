package com.example.myapplication.util

import java.time.LocalTime

fun formatTime(time: LocalTime): Triple<String, String, String> {
    var h = "${time.hour}"
    if (time.hour < 9)
        h = "0${time.hour}"
    var m = "${time.minute}"
    if (time.minute < 9)
        m = "0${time.minute}"
    var s = "${time.second}"
    if (time.second < 9)
        s = "0${time.second}"
    return Triple(h, m, s)
}