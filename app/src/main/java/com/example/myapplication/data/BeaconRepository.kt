package com.example.myapplication.data

import com.example.myapplication.data.model.Beacon
import com.example.myapplication.presentation.beacon.BeaconsState
import com.example.myapplication.presentation.beacon.MarathonStatus
import kotlinx.coroutines.flow.Flow

interface BeaconRepository {
    fun startListeningForBeacons()
    fun stopListeningForBeacons()
    fun getPassedBeacons(): Flow<List<Beacon>>
    fun getModelState(): BeaconsState
    fun setNewIdMarathon(newModelState: BeaconsState)
    fun setNewMarathonStatus(newMarathonStatus: MarathonStatus)
}