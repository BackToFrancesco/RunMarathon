package com.example.myapplication.data.dataSource

import com.example.myapplication.data.model.Beacon
import com.example.myapplication.presentation.beacon.BeaconsState
import com.example.myapplication.presentation.beacon.MarathonStatus
import kotlinx.coroutines.flow.Flow

interface BeaconDataSource {
    fun startListeningForBeacons()
    fun stopListeningForBeacons()
    fun getPassedBeacon(): Flow<List<Beacon>>
    fun getModelState(): BeaconsState
    fun setNewIdMarathon(newModelState: BeaconsState)
    fun setNewMarathonStatus(newMarathonStatus: MarathonStatus)
}