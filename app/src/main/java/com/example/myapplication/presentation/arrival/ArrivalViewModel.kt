package com.example.myapplication.presentation.arrival

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.BeaconRepository
import com.example.myapplication.presentation.beacon.BeaconsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ArrivalViewModel @Inject constructor(
    private val beaconRepository: BeaconRepository
) : ViewModel() {

    fun getModelState(): BeaconsState {
        return beaconRepository.getModelState()
    }

}