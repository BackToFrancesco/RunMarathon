package com.example.myapplication.presentation.beacon

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.BeaconRepository
import com.example.myapplication.data.DataBaseRepository
import com.example.myapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeaconViewModel @Inject constructor(
    private val repository: BeaconRepository,
    private val dataBase: DataBaseRepository,
    private val firebaseAuth: FirebaseAuth
) :ViewModel(){
    private val _beaconState = mutableStateOf(BeaconsState())
    val beaconState: State<BeaconsState> = _beaconState

    fun startListeningForBeacon(){
        repository.startListeningForBeacons()
    }
    fun stopListeningForBeacon(){
        repository.stopListeningForBeacons()
    }

    fun getModelState(): BeaconsState {
        return repository.getModelState()
    }

    fun setMarathonID(idMarathon: Int){
        var modelState = repository.getModelState()
        modelState.idMarathon = idMarathon
        modelState.isFetching = true
        modelState.fetchingError = false
        repository.setNewIdMarathon(modelState)
    }

    fun pushBeaconsToServer(){
       val beacons = _beaconState.value.beaconPassed
        repository.setNewMarathonStatus(MarathonStatus.INUPLOAD)
        GlobalScope.launch{
            try {
                /*
                 uncomment this to use Firebase
                dataBase.checkAllBeaconsAsync(beacons, _beaconState.value.idMarathon, firebaseAuth.currentUser!!.uid)
                */

                // simulate server request
                delay(2000)

                val allContained = Constants.listOfBeacons.all { targetId ->
                    beacons.any { beacon -> beacon?.id.toString() == targetId }
                }

                if (!allContained) {
                    throw Exception("Invalid marathon, not all checkpoints have been registered")
                }

                repository.setNewMarathonStatus(MarathonStatus.VALID)
            } catch (e:Exception){
                Log.e("BeaconViewModel", "${e.message}")
                repository.setNewMarathonStatus(MarathonStatus.NOTVALID)
            }
        }
    }

    init {
        repository.getPassedBeacons().onEach {
            _beaconState.value = _beaconState.value.copy(
                beaconPassed = it,
                isMarathonStarted = it.isNotEmpty(),
                isMarathonEnded = if(it.isNotEmpty()) it.last().isTheLastOne else false,
                fetchingError = repository.getModelState().fetchingError,
                isFetching = repository.getModelState().isFetching,
                idMarathon = repository.getModelState().idMarathon,
                marathonStatus = repository.getModelState().marathonStatus
            )
        }.launchIn(viewModelScope)
    }
}