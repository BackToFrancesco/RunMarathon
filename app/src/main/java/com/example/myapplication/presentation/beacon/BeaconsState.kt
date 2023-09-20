package com.example.myapplication.presentation.beacon

import com.example.myapplication.data.model.Beacon

data class BeaconsState(
    val beaconPassed: List<Beacon?> = listOf(),
    var isMarathonStarted: Boolean = false,
    var isMarathonEnded: Boolean = false,
    var fetchingError: Boolean = false,
    var idMarathon: Int = 0,
    var isFetching: Boolean = false,
    var isRanging : Boolean = false,
    var marathonStatus: MarathonStatus = MarathonStatus.DEFAULT
)

enum class MarathonStatus {
    VALID, //marathon pushed to the server and it's valid
    NOTVALID, //marathon not valid after push
    INUPLOAD, //trying to push to server
    DEFAULT,
}