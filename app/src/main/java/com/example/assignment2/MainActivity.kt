package com.example.assignment2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.assignment2.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.util.*

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var permissionID = 1000

    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private lateinit var lat: String
    private lateinit var long: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getLocation.setOnClickListener {
            //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            getLocation()
        }

        binding.sendLocation.setOnClickListener {

        }
    }


    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionID)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }
    override fun onLocationChanged(location: Location) {
        lat = location.latitude.toString()
        long = location.longitude.toString()
        val txt = findViewById<TextView>(R.id.address)
        txt.text = "$lat ; $long"
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


/*

    private fun getLocation() : String {
        val task : Task<Location> = fusedLocationProviderClient.lastLocation
        var answer = ""
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), permissionID)
        }

        //var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //return locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER)


        if (task.isSuccessful && task.result != null) {
            answer = task.result.latitude.toString() + " ; " + task.result.longitude.toString()
            return  answer
        } else {
            return "No Location Detected"
        }
    }

    private fun getAddress(lat: Double, long: Double) : String {
        var city = ""
        var State = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Address : (Mutable)List<Address!>! = geoCoder.getFromLocation(lat, long, 1)

        city = Address.get(0).locality
        return city
    }
*/

}