package com.example.myapplication.data.dataSource

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.example.myapplication.data.DataBaseRepository
import com.example.myapplication.data.model.Beacon
import com.example.myapplication.presentation.beacon.BeaconsState
import com.example.myapplication.presentation.beacon.MarathonStatus
import com.example.myapplication.util.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.altbeacon.beacon.*
import java.time.LocalTime
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject

class BeaconDataSourceImpl @Inject constructor(
    ctx: Context,
    private val dataBase: DataBaseRepository,
): BeaconDataSource, RangeNotifier {
    private val passedBeacons: ConcurrentLinkedQueue<Beacon> = ConcurrentLinkedQueue()
    private val closestBeaconsMisurations : MutableList<MutableList<Beacon>> = mutableListOf()
    private var firstBeaconID: String? = null
    private var lastBeaconID: String? = null
    private var startingTime: LocalTime? = null
    private var context: Context = ctx
    private var modelState: BeaconsState = BeaconsState()

    private  val beaconManager = BeaconManager.getInstanceForApplication(ctx)
    private val region = Region("all-beacon-region", null, null, null)

    init {
        setupBeaconListener()
    }

    private fun setupBeaconListener(){
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT))
        // for IBEACON beacon
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(IBEACON_LAYOUT))

        beaconManager.foregroundScanPeriod = 1100L
        beaconManager.foregroundBetweenScanPeriod = 0L

        beaconManager.addRangeNotifier(this)
    }

    private fun createNotificationChannel(context: Context) : String{
        val name = "Beacon Scanning"
        val descriptionText = "Notification for Beacon scanning service"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel("BeaconScanning", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        return "BeaconScanning"
    }

    // Required only if I enable the intent
    //@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
    override fun startListeningForBeacons() {
        val channelId = createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Scanning for Beacons")
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        beaconManager.enableForegroundServiceScanning(builder.build(), 456)
        beaconManager.setEnableScheduledScanJobs(false)

        beaconManager.backgroundBetweenScanPeriod = 0
        beaconManager.backgroundScanPeriod = 1100


        beaconManager.startRangingBeacons(region)
        modelState.isRanging = true
    }

    override fun stopListeningForBeacons() {
        beaconManager.stopRangingBeacons(region)
        modelState.isRanging = false
    }

    override fun getPassedBeacon(): Flow<List<Beacon>> = flow {
        while (true) {
            emit(passedBeacons.toList())
            delay(1100)
        }
    }

    override fun getModelState(): BeaconsState {
        return modelState
    }

    override fun setNewIdMarathon(newModelState: BeaconsState) {
        stopListeningForBeacons()
        modelState = newModelState
        modelState.marathonStatus = MarathonStatus.DEFAULT
        modelState.isMarathonEnded = false
        modelState.isMarathonStarted = false
        passedBeacons.clear()
        GlobalScope.launch {
            try {
                /*
                Uncomment this for using with Firebase
                val res = dataBase.getFirstAndLastBeaconAsync(modelState.idMarathon).await()
                firstBeaconID = res["firstBeacon"]
                lastBeaconID = res["lastBeacon"]
                */

                // simulate the request to the server
                delay(2000)

                // set first and last beacon
                firstBeaconID = Constants.listOfBeacons.first()
                lastBeaconID = Constants.listOfBeacons.last()

                modelState.isFetching = false
                startListeningForBeacons()
            }catch (e:Exception){
                modelState.fetchingError = true
                modelState.isFetching = false
                modelState.idMarathon = 0
            }
        }
    }

    override fun setNewMarathonStatus(newMarathonStatus: MarathonStatus) {
        modelState.marathonStatus = newMarathonStatus
    }

    override fun didRangeBeaconsInRegion(
        beacons: MutableCollection<org.altbeacon.beacon.Beacon>?,
        region: Region?
    ) {
        //debug
        //Log.d(TAG, "didRangeBeaconsInRegion started")

        Log.d(TAG, "Ranged: ${beacons?.count()} beacons")
        beacons?.forEach{ beacon ->
            Log.e(TAG, "$beacon about ${beacon.distance} meters away")
        }

        // map beacons of type Android.beacon.library to Beacon
        val beaconToFilter = beacons?.map {
            var time = LocalTime.of(0,0,0)
            var timeStamp = LocalTime.now()

            var isFirst = false
            var isLast = false

            if(it.id1.toString() == firstBeaconID)
                isFirst = true

            if(it.id1.toString() == lastBeaconID) {
                isLast = true
            }

            if(startingTime != null)
                time = LocalTime.ofNanoOfDay(timeStamp.toNanoOfDay() - startingTime!!.toNanoOfDay())

            Beacon(it.id1.toUuid(), it.distance, dayTime = timeStamp, isTheFirstOne = isFirst, isTheLastOne = isLast, durationTime = time)
        }

        // keep beacons with the first 24 digits of UUID equals to first 24 digits of first ID
        val beaconb = filterBeaconsID(beaconToFilter!!, firstBeaconID!!.substring(0,24))

        // keeps only beacons never detected
        val beacon = filterAlreadyPassedBeacons(beaconb, passedBeacons = passedBeacons)


        if(beacon != null)
        {
            if(passedBeacons.isEmpty())
                beacon.forEach {
                    if(it.isTheFirstOne) {
                        passedBeacons.add(it)
                        startingTime = it.dayTime
                    }
                }
            else{
                beacon.forEach {
                    if(checkPresenceOfFirstBeacon(passedBeacons) && it.distance <= MIN_DISTANCE){
                        if (it.isTheLastOne && !checkPresenceOfLastBeacon(passedBeacons)) {
                            passedBeacons.add(it)
                            stopListeningForBeacons()
                        }
                        else {
                            passedBeacons.add(it)
                        }
                    }
                }
            }
        }
    }

    companion object{
        const val TAG = "BeaconDataSourceImpl"
        const val IBEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"
        const val MIN_DISTANCE = 2.5
    }

    private fun addBeaconToClosestBeaconsMisurations(beaconsDetected: List<Beacon>) {
        beaconsDetected.forEach { beacon ->
            if(!beacon.isTheFirstOne && !beacon.isTheLastOne) {
                // Find the group of closest beacons with the same id as the current beacon
                val group = closestBeaconsMisurations.find { it.first().id == beacon.id }
                if (group != null) {
                    // If the group is found, add the beacon to the group
                    group.add(beacon)
                } else {
                    // If the group is not found, create a new group with the current beacon
                    closestBeaconsMisurations.add(mutableListOf(beacon))
                }
            }
        }
    }

    // finds the beacons no more detected
    private fun missingBeacons(beaconsDetected: List<Beacon>?): List<Beacon> {
        val detectedIds = beaconsDetected?.map { it.id } ?: emptyList()
        return closestBeaconsMisurations.filter { it.first().id !in detectedIds }.map { averageBeacon(it) }
    }

    // return a Beacon with timestap of the average of the misurations of the beacon
    private fun averageBeacon(beacons: List<Beacon>): Beacon {
        val totalNano = beacons.sumOf { it.dayTime.toNanoOfDay() }
        val averageNano = totalNano / beacons.size
        val timestamp = LocalTime.ofNanoOfDay(averageNano)

        var time: Long = 0
        if(startingTime != null)
            time = averageNano - startingTime!!.toNanoOfDay()

        return Beacon(beacons.first().id, beacons.first().distance, timestamp,
            isTheFirstOne = false,
            isTheLastOne = false,
            LocalTime.ofNanoOfDay(time)
        )
    }

    // checks if last beacon is already detected and it's distance <= MIN_DISTANCE
    private fun checkPresenceOfLastBeacon(beacons: ConcurrentLinkedQueue<Beacon>): Boolean {
        return beacons.any { it.isTheLastOne }
    }

    private fun checkPresenceOfFirstBeacon(beacons: ConcurrentLinkedQueue<Beacon>): Boolean {
        return beacons.any { it.isTheFirstOne }
    }

    // filter beacons with the given prefix
    private fun filterBeaconsID(beacons: List<Beacon>, prefix: String): List<Beacon> {
        val filteredBeacons = mutableListOf<Beacon>()
        for (beacon in beacons) {
            if (beacon.id.toString().startsWith(prefix)) {
                filteredBeacons.add(beacon)
            }
        }
        return filteredBeacons
    }

    // filter already detected beacons
    fun filterAlreadyPassedBeacons(beacons: List<Beacon>, passedBeacons: ConcurrentLinkedQueue<Beacon>): List<Beacon> {
        val passedIds = passedBeacons.map { it.id }
        return beacons.filter { beacon -> !passedIds.contains(beacon.id) }
    }

}