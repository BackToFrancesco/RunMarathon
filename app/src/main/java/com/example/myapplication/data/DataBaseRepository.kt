package com.example.myapplication.data

import com.example.myapplication.data.model.Beacon
import com.example.myapplication.data.model.Rank
import kotlinx.coroutines.Deferred


interface DataBaseRepository {

    suspend fun getRankingAsync(idMarathon: Int) : Deferred<List<Rank>>
    suspend fun checkAllBeaconsAsync(beacons: List<Beacon?>, idMarathon: Int, idUser: String)
    suspend fun getFirstAndLastBeaconAsync(idMarathon : Int) : Deferred<HashMap<String, String>>
}