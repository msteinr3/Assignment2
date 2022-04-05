package com.example.assignment2

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
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


    private var columns = listOf<String>(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
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

        binding.sendLocation.setOnClickListener {
            readContacts()
        }
    }

    @SuppressLint("Range")
    private fun readContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 102)
        }
        binding.recycler.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<ItemFields>()
        binding.recycler.adapter = CustomAdapter(data)

        val cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,
            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID, null, null)

        while (cursor!!.moveToNext()) {

            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            val phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            //data.add(ItemFields(R.drawable.ic_baseline_person, name, phone))
        }
        cursor.close()

        // Test data
        for (i in 1..20) { data.add(ItemFields(R.drawable.ic_baseline_person, "Item " + i, "8474630156")) }
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
                binding.latitude.text = it.latitude.toString()
                binding.longitude.text = it.longitude.toString()
                binding.address.text = addresses[0].getAddressLine(0)
            }
        }
    }
}
