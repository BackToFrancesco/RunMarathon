package com.example.myapplication.data

import com.example.myapplication.data.dataSource.BeaconDataSource
import com.example.myapplication.data.model.Beacon
import com.example.myapplication.presentation.beacon.BeaconsState
import com.example.myapplication.presentation.beacon.MarathonStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BeaconRepositoryImpl @Inject constructor(
    private val beaconDataSource: BeaconDataSource
) :BeaconRepository {
    override fun startListeningForBeacons() {
        beaconDataSource.startListeningForBeacons()
    }

    override fun stopListeningForBeacons() {
        beaconDataSource.stopListeningForBeacons()
    }

    override fun getPassedBeacons(): Flow<List<Beacon>> = beaconDataSource.getPassedBeacon()

    override fun getModelState(): BeaconsState{
        return beaconDataSource.getModelState()
    }

    override fun setNewIdMarathon(newModelState: BeaconsState){
        beaconDataSource.setNewIdMarathon(newModelState)
    }

    override fun setNewMarathonStatus(newMarathonStatus: MarathonStatus) {
        beaconDataSource.setNewMarathonStatus(newMarathonStatus)
    }
}