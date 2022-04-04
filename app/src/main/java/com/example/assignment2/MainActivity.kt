package com.example.assignment2

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.assignment2.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.util.*

open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.getLocation.setOnClickListener {
            getLocation()
        }


        binding.sendLocation.setOnClickListener {

        }
    }

    private fun getLocation() {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses:List<Address>

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if(it != null){
                addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                val lat = findViewById<TextView>(R.id.latitude)
                val long = findViewById<TextView>(R.id.longitude)
                val address = findViewById<TextView>(R.id.address)
                lat.text = it.latitude.toString()
                long.text = it.longitude.toString()
                address.text = addresses[0].getAddressLine(0)
            }
        }
    }

}
