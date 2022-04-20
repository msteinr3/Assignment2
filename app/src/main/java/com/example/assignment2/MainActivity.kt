package com.example.assignment2

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment2.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*

open class MainActivity : AppCompatActivity() {

    //things to do:
    //coroutines for get location and read contacts
    //explain need for permissions, provide button to settings

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var address: String
    private lateinit var contact: String
    private lateinit var phone: String
    private var rows = listOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //don't show views until needed
        binding.loadContacts.visibility = View.INVISIBLE
        binding.text.visibility = View.INVISIBLE

        binding.getLocation.setOnClickListener {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            CoroutineScope(IO).launch {
                getLocation()
            }
        }

        binding.loadContacts.setOnClickListener {
            //CoroutineScope(IO).launch {
                readContacts()
            //}
        }
    }

    private fun setAddress(input: String) {
        binding.address.text = input
    }

    private suspend fun getLocation() {
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
                binding.text.visibility = View.VISIBLE
                binding.loadContacts.visibility = View.VISIBLE

                addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                address = addresses[0].getAddressLine(0)

                setAddress(address)
            }
        }
    }

    private fun readContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 102)
        }
        binding.recycler.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<Contact>()
        val adapt = ContactAdapter(data)
        binding.recycler.adapter = adapt
        adapt.setOnItemClickedListener(object : ContactAdapter.ItemClickListener {
            override fun onItemClicked(position: Int) {
                contact = data[position].name
                phone = data[position].phone
                sendMessage()
            }
        })

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            rows, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        while (cursor!!.moveToNext()) {
            val name = cursor.getString(0)
            val phone = cursor.getString(1)
            data.add(Contact(name, phone))
        }
        cursor.close()
    }

    private fun sendMessage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                103
            )
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.ru_sure) + " " + contact + "?")
        builder.setIcon(R.drawable.ic_baseline_location_on)
        builder.setPositiveButton(
            getString(R.string.send),
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
                try {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse("smsto:")
                    i.putExtra("address", phone)
                    i.putExtra("sms_body", address)
                    startActivity(Intent.createChooser(i, "Send sms via:"))
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.fail), Toast.LENGTH_LONG).show()
                }
            })
        builder.setNegativeButton(
            getString(R.string.cancel),
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert = builder.create()
        alert.setTitle(getString(R.string.send_location))
        alert.show()
    }
}
