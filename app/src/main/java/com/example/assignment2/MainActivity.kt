package com.example.assignment2

import android.Manifest
import android.R.attr.bitmap
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import java.util.*


//questions:
//other permissions needed? whatsapp, email...
//options to send as something else (not sms)
//what is MIME type
//more types of info from contact
//photo bitmap/drawable/Int?
//review thread coroutines, delay action
//implement search bar

open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var address: String
    private lateinit var contact: String
    private lateinit var phone: String
    private var rows = listOf(
        ContactsContract.Data.PHOTO_ID,                                  //need a photo (bitmap? drawable?)
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,                   //how to specify mobile
        ContactsContract.CommonDataKinds.Email.ADDRESS,                  //not getting an email?
        ContactsContract.CommonDataKinds.Phone._ID                       //what for?
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //don't show views until needed
        binding.sendLocation.visibility = View.INVISIBLE
        binding.loadContacts.visibility = View.INVISIBLE
        binding.search.visibility = View.INVISIBLE

        binding.getLocation.setOnClickListener {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            getLocation()
            binding.loadContacts.visibility = View.VISIBLE          //how to delay till address shows up

        }

        binding.loadContacts.setOnClickListener {
            readContacts()
            binding.search.visibility = View.VISIBLE
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
                address = addresses[0].getAddressLine(0)
                binding.address.text = address
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
        val adapt = CustomAdapter(data)
        binding.recycler.adapter = adapt
        adapt.setOnItemClickedListener(object : CustomAdapter.onItemClickListener {
            override fun onItemClicked(position: Int) {
                contact = data[position].name
                phone = data[position].phone
                binding.contactChosen.text = contact
                binding.sendLocation.visibility = View.VISIBLE
            }
        })

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            rows, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        while (cursor!!.moveToNext()) {
            val image = R.drawable.ic_baseline_person
            val name = cursor.getString(1)
            val phone = cursor.getString(2)
            //val email = cursor.getString(3)

            data.add(ContactInfo(image, name, phone))
        }
        cursor.close()
    }

    private fun sendMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.ru_sure) + " " + contact + "?")
        builder.setIcon(R.drawable.ic_baseline_location_on)
        builder.setPositiveButton(
            getString(R.string.send),
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.SEND_SMS),
                        103
                    )
                }
                try {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse("smsto:")
                    //i.type = "vnd.android-dir/mms-sms"
                    i.putExtra("address", phone)
                    i.putExtra("sms_body", address)
                    startActivity(Intent.createChooser(i, "Send sms via:"))
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_LONG).show()
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
