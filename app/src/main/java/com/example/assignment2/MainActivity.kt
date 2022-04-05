package com.example.assignment2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment2.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.util.*

open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var rows = listOf(                                           //more info?
        ContactsContract.Data.PHOTO_ID,                                  //need a photo (bitmap?)
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Email.ADDRESS,                  //not an email?
        ContactsContract.CommonDataKinds.Phone._ID
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getLocation.setOnClickListener {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            getLocation()
        }

        binding.loadContacts.setOnClickListener {
            readContacts()
        }

        binding.sendLocation.setOnClickListener {
            sendMessage()
        }
    }

    private fun getLocation() {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>

        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                binding.latitude.text = it.latitude.toString()
                binding.longitude.text = it.longitude.toString()
                binding.address.text = addresses[0].getAddressLine(0)
            }
        }
    }

    @SuppressLint("Range")
    private fun readContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 102)
        }
        binding.recycler.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<ContactInfo>()
        binding.recycler.adapter = CustomAdapter(data)

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            rows, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        while (cursor!!.moveToNext()) {
            val image = R.drawable.ic_baseline_person
            val name = cursor.getString(1)
            val phone = cursor.getString(2)
            val email = cursor.getString(3)
            data.add(ContactInfo(image, name, phone))
        }
        cursor.close()
    }

    private fun sendMessage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 103)
        }

        val intent = Intent(Intent.ACTION_SEND).apply {                 //not sure which action
            data = Uri.parse(binding.address.text.toString())           //should be address from getLocation
        }
        startActivity(intent)

        //other permissions needed? whatsapp, email...
        //send location (address) to the contact that was clicked on
        //options to send as text, whatsapp, email... which action?
    }
}
