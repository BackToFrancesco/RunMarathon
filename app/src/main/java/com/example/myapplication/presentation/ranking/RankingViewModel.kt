package com.example.myapplication.presentation.ranking

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.BeaconRepository
import com.example.myapplication.data.DataBaseRepository
import com.example.myapplication.presentation.beacon.BeaconsState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RankingViewModel @Inject constructor(
    private val dbRepository: DataBaseRepository,
    private val beaconRepository: BeaconRepository
) : ViewModel() {

    suspend fun getRanking(idMarathon: Int) = dbRepository.getRankingAsync(idMarathon)

    fun getModelState(): BeaconsState {
        return beaconRepository.getModelState()
    }

}