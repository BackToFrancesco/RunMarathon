package com.example.myapplication.util

import com.example.myapplication.data.model.Rank

object Constants {
    // beacon UUIDs of marathon 1
    val listOfBeacons = listOf(
        "f839a5b4-1d79-4db1-a66d-89917cef6300",
        "f839a5b4-1d79-4db1-a66d-89917cef6305",
        "f839a5b4-1d79-4db1-a66d-89917cef6309"
    )
    // To prevent detections of other beacons that are not part of the marathon,
    // it is necessary for the beacons that are part of the marathon to have the same first 24 digits of the UUID.

    val rankingList = listOf(
        Rank(id = "1", name = "John", surname = "Doe", totalDurationTime = 100),
        Rank(id = "2", name = "Alice", surname = "Smith", totalDurationTime = 85),
        Rank(id = "3", name = "Bob", surname = "Johnson", totalDurationTime = 120),
        Rank(id = "4", name = "Eva", surname = "Brown", totalDurationTime = 95),
        // Add more Rank objects as needed
    )
}
