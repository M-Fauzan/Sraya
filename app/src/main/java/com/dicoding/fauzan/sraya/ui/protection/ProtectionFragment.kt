package com.dicoding.fauzan.sraya.ui.protection

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.*
import com.dicoding.fauzan.sraya.MyWorker
import com.dicoding.fauzan.sraya.R
import com.dicoding.fauzan.sraya.databinding.FragmentProtectionBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class ProtectionFragment : Fragment(), OnMapReadyCallback {

    private lateinit var workManger: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isReady = false
    private var _binding: FragmentProtectionBinding? = null
    private lateinit var database: FirebaseFirestore
    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        when {
            it[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getInitialLocation()
            }
            it[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getInitialLocation()
            }
            else -> {

            }
        }
    }

    private var resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()) {
        when (it.resultCode) {
            RESULT_OK -> {
                Log.i(TAG, "Izin lokasi telah terpenuhi")
            }
            RESULT_CANCELED -> {
                Toast.makeText(this.context, "Butuh izin lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProtectionBinding.inflate(inflater, container, false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        workManger = WorkManager.getInstance(requireActivity())

        val data = Data.Builder()

            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(MyWorker::class.java, 15L, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManger.enqueue(periodicWorkRequest)
        workManger.getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(
            requireActivity()
        ) {
            if (it.state == WorkInfo.State.RUNNING) {

            }
        }
        binding.btnProtectionProtect.setOnClickListener {
            // TODO: Replace value with the corresponding data type from Firestore



            val location = hashMapOf(
                "GPS" to "0",
                "Location" to "0",
                "Time" to "0")
            database.collection("SrayaData")
                .document("location")
                .set(location)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully added a document!")
                }
                .addOnFailureListener {
                    Log.w(TAG, "An error occured", it)
                }
        }


        return binding.root
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */


        initLocationRequest()

        initLocationCallback()

        startLocationUpdates()
        isReady = true
    }

    override fun onResume() {
        super.onResume()
        if (isReady) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }
    private fun getInitialLocation() {
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

        } else {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }
    private fun initLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(locationSettingsRequest).addOnSuccessListener {

            }
            .addOnFailureListener {
                try {
                    if (it is ResolvableApiException) {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(it.resolution).build()
                        )
                    }
                } catch (exception: IntentSender.SendIntentException) {
                    Toast.makeText(this.context, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun initLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation
                val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)

                Log.d(TAG, latLng.latitude.toString())
                Log.d(TAG, latLng.longitude.toString())

                mMap.addMarker(MarkerOptions()
                    .position(latLng)
                    .title("You're here"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    private fun startLocationUpdates() {
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION))
        }

    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val TAG = "ProtectionFragment"
    }
}