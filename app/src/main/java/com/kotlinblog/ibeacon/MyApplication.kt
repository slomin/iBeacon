package com.kotlinblog.ibeacon

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import io.paperdb.Paper
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap
import java.text.SimpleDateFormat
import java.util.*


class MyApplication : Application(), BootstrapNotifier {
    private var regionBootstrap: RegionBootstrap? = null
    private var regionListener: RegionListener? = null

    interface RegionListener {
        fun onRegionChanged(region: Region?)
    }

    override fun onCreate() {
        super.onCreate()

        Paper.init(applicationContext) // Initialising PaperDB

        val beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this)

        beaconManager.beaconParsers.clear()
        beaconManager.beaconParsers.add(BeaconParser().
                setBeaconLayout(Constants.IBEACON_LAYOUT_BY_GEOFF))


        val storedUuidA = Paper.book().read(Constants.PAPER_KEY_UUID_A, Constants.DEFAULT_UUID_A)
        val storedUuidB = Paper.book().read(Constants.PAPER_KEY_UUID_B, Constants.DEFAULT_UUID_B)

        if (storedUuidA.isEmpty() && storedUuidB.isEmpty()) {
            Toast.makeText(this, "Please add UUIDs and kill the app before using it", Toast.LENGTH_LONG).show()
        } else {
            val regionA = Region("Region A",
                    Identifier.parse(storedUuidA), null, null)

            val regionB = Region("Region B",
                    Identifier.parse(storedUuidB), null, null)
            regionBootstrap = RegionBootstrap(this, regionA) // initialising regionBootstrap, adding first region
            if (regionB != null) {
                regionBootstrap?.addRegion(regionB) // adding second region
            }
        }


    }

    fun setRegionListener(listener: MyApplication.RegionListener) {
        this.regionListener = listener
    }

    override fun didEnterRegion(region: Region) {
        Log.d(Constants.TAG, "did enter region")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        this.startActivity(intent)
        storeDetectedEvent(region, "Entered: ")
    }

    override fun didDetermineStateForRegion(p0: Int, region: Region?) {
        if (p0 == MonitorNotifier.INSIDE) {
            Log.d(Constants.TAG, "didDetermineState: ${region.toString()} INSIDE: ")
            storeDetectedEvent(region, "determinedState: INSIDE")
        } else if (p0 == MonitorNotifier.OUTSIDE) {
            Log.d(Constants.TAG, "didDetermineStateForRegion ${region.toString()} OUTSIDE: ")
            storeDetectedEvent(region, "determinedState: OUTSIDE")
        }

    }

    override fun didExitRegion(region: Region?) {
        Log.d(Constants.TAG, "did exit region ${region.toString()}")
        storeDetectedEvent(region, "Exit: ")

    }

    private fun storeDetectedEvent(region: Region?, reason: String) {
        if (region != null) {
            var storedEvents1: String = Paper.book().read(Constants.PAPER_KEY_EVENTS, "")
            val c = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss a")
            val time = simpleDateFormat.format(c.time)
            Log.d(Constants.TAG, "formatted date: $time")
            val eventToAdd = reason + region.uniqueId + "\n detected at: " + time + "\n\n"
            storedEvents1 += eventToAdd

            Paper.book().write(Constants.PAPER_KEY_EVENTS, storedEvents1)
            regionListener?.onRegionChanged(region)
            Log.d(Constants.TAG, "region $: $region")
        }

    }

}