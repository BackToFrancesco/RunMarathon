package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.data.model.Beacon
import com.example.myapplication.data.model.Rank
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class DataBaseRepositoryImpl : DataBaseRepository {
    private var fireBaseFunctions: FirebaseFunctions = Firebase.functions("us-central1")

    override suspend fun checkAllBeaconsAsync(beacons: List<Beacon?>, idMarathon: Int, idUser: String) {
        return coroutineScope{
            async{
                val data = beaconListToHashMap(beacons, idMarathon = idMarathon, idUser = idUser)
                val callable = fireBaseFunctions.getHttpsCallable("checkAllBeacons")
                callable.call(data).await()
            }
        }
    }

    override suspend fun getFirstAndLastBeaconAsync(idMarathon : Int): Deferred<HashMap<String, String>> {
        return coroutineScope {
            async {
                val data = hashMapOf(
                    "idMarathon" to idMarathon.toString()
                )

                val callable = fireBaseFunctions.getHttpsCallable("getFirstAndLastBeacon")
                val result = callable.call(data).await()
                val res = result.data as Map<*, *>
                val firstBeacon = res["firstBeacon"].toString()
                val lastBeacon = res["lastBeacon"].toString()

                hashMapOf(
                    "firstBeacon" to firstBeacon,
                    "lastBeacon" to lastBeacon
                )
            }.handleErrors()
        }
    }

    override suspend fun getRankingAsync(idMarathon: Int): Deferred<List<Rank>> {
        return coroutineScope {
            async {
                val data = hashMapOf(
                    "idMarathon" to "$idMarathon"
                )
                val callable = fireBaseFunctions.getHttpsCallable("getRanking")
                val result = callable.call(data).await()

                val res = result.data as Map<*, *>
                val rankingList = (res["ranking"] as List<*>).map { item ->
                    val map = item as Map<*, *>
                    Rank(
                        id = map["id"] as String,
                        name = map["name"] as String,
                        surname = map["surname"] as String,
                        totalDurationTime = map["totalDurationTime"] as Int
                    )
                }
                rankingList
            }
        }
    }

    private fun Deferred<HashMap<String, String>>.handleErrors(): Deferred<HashMap<String, String>> {
        return try {
            this
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            throw e
        }
    }
}

fun beaconListToHashMap(beacons: List<Beacon?>, idMarathon: Int, idUser: String): HashMap<String, Any> {
    return hashMapOf(
        "idUser" to idUser,
        "idMarathon" to idMarathon.toString(),
        "beacons" to beacons.filterNotNull().map { beacon ->
            hashMapOf(
                "id" to beacon.id.toString(),
                "dayTime" to hashMapOf(
                    "hour" to beacon.dayTime.hour,
                    "minute" to beacon.dayTime.minute,
                    "second" to beacon.dayTime.second
                ),
                "durationTime" to hashMapOf(
                    "hour" to beacon.durationTime.hour,
                    "minute" to beacon.durationTime.minute,
                    "second" to beacon.durationTime.second
                ),
                "isTheFirstOne" to beacon.isTheFirstOne,
                "isTheLastOne" to beacon.isTheLastOne
            )
        }
    )
}