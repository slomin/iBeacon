package com.kotlinblog.ibeacon

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import org.altbeacon.beacon.Region

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkBluetoothPermissions()
        setCurrentUuidView()

        btnUpdateUuidA.setOnClickListener { updateUuidA(editText.text.toString()) }
        btnUpdateUuidB.setOnClickListener { updateUuidB(editText.text.toString()) }
        btnPurgeLogs.setOnClickListener { purgeLogs() }
        setUiListener()
        updateUi()
    }

    private fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Ask another time here (the same ask dialog for now)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION)
            }
        } else {
            Log.d("TAG", "Permission already granted")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("TAG", "Permission GRANTED!!!")
                } else {
                    Toast.makeText(this, "Please grant LOCATION permission to use this app", Toast.LENGTH_LONG).show()
                    finish()
                }
                return
            }
            else -> {

            }
        }
    }

    private fun updateUuidA(uuid: String) {
        if (uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex())) {
            Paper.book().write(Constants.PAPER_KEY_UUID_A, uuid)
            setCurrentUuidView()
        } else {
            Toast.makeText(this, "Invalid UUID A formatting...", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUuidB(uuid: String) {
        if (uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex())) {
            Paper.book().write(Constants.PAPER_KEY_UUID_B, uuid)
            setCurrentUuidView()
        } else {
            Toast.makeText(this, "Invalid UUID B formatting...", Toast.LENGTH_LONG).show()
        }
    }

    private fun setCurrentUuidView() {
        val uuidA: String = Paper.book().read(Constants.PAPER_KEY_UUID_A, "UUID A is empty")
        val uuidB: String = Paper.book().read(Constants.PAPER_KEY_UUID_B, "UUID B is empty")
        tvUuidA.text = uuidA
        tvUuidB.text = uuidB
    }

    private fun updateUi() {
        val events = Paper.book().read(Constants.PAPER_KEY_EVENTS, "")
        tvUuidList.text = events
    }

    private fun setUiListener() {
        val sampleApp = applicationContext as MyApplication

        sampleApp.setRegionListener(object : MyApplication.RegionListener {
            override fun onRegionChanged(region: Region?) {
                updateUi()
            }

        })
    }

    private fun purgeLogs() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Purge logs")
        dialogBuilder.setMessage("Do you want to purge your logs")
        dialogBuilder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            Paper.book().delete(Constants.PAPER_KEY_EVENTS)
            updateUi()
        }
        dialogBuilder.create().show()
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0
    }
}
