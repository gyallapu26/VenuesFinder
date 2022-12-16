package com.adyen.android.assignment.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import com.adyen.android.assignment.R
import com.adyen.android.assignment.databinding.ActivityVenueRecomendationsBinding
import com.adyen.android.assignment.domain.ddd.Venue
import com.adyen.android.assignment.ui.Constants.CAMERA_ZOOM
import com.adyen.android.assignment.ui.Constants.DEFAULT_LAT
import com.adyen.android.assignment.ui.Constants.DEFAULT_LONG
import com.adyen.android.assignment.ui.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.adyen.android.assignment.ui.viewmodel.PlacesViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class VenuesRecommendationsActivity : AppCompatActivity(), OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks {

    private val viewModel: PlacesViewModel by viewModels()
    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var _binding: ActivityVenueRecomendationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVenueRecomendationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (hasLocationPermission()) {
            addLocationListener()
        } else {
            requestLocationPermission(PERMISSION_LOCATION_REQUEST_CODE)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        lifecycle.coroutineScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.venueUiState.collectLatest { uiState ->
                    if (uiState.isLoading) {
                        binding.progressBar.visible()
                    }
                    if (uiState.errorMessage.isNotEmpty()) {
                        binding.progressBar.visible()
                        Snackbar.make(binding.root, uiState.errorMessage, Snackbar.LENGTH_LONG)
                            .show()

                    }
                    if (uiState.venues.isNotEmpty()) {
                        binding.progressBar.gone()
                        uiState.venues.forEach { venue ->
                            map.addMarker(MarkerOptions().apply { position(venue.coordinates) }
                                .title(venue.name))?.apply {
                                showInfoWindow()
                                tag = venue.name
                            }
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    venue.coordinates,
                                    CAMERA_ZOOM
                                )
                            )

                        }
                    }
                }
            }
        }
        map.setOnMarkerClickListener { marker ->
            viewModel.venueUiState.value.venues.find { marker.tag == it.name }?.let {
                goToDetailsScreen(it)
            }
            false
        }
    }

    private fun goToDetailsScreen(venue: Venue) {
        supportFragmentManager.let {
            val dialog =
                (it.findFragmentByTag(VenueDetailsDialog.TAG) as? VenueDetailsDialog)
                    ?: VenueDetailsDialog.getInstance(venue)
            if (dialog.isAdded.not()) {
                dialog.show(it, VenueDetailsDialog.TAG)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addLocationListener() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            fetchVenues(location.latitude, location.longitude)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            addLocationListener()
        }
    }

    /** Since permission is denied, it's not possible to fetch nearby venus, In order to not to make this
     * assignment overcomplicated, proposing a default Coordinates (latitude: Double, longitude: Double) of Netherlands  */

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            fetchVenues(DEFAULT_LAT, DEFAULT_LONG)
        }
    }

    private fun fetchVenues(latitude: Double, longitude: Double) {
        if (this.isNetworkAvailable()) {
            viewModel.fetchVenues(latitude, longitude)
        } else {
            Snackbar.make(binding.root, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}


