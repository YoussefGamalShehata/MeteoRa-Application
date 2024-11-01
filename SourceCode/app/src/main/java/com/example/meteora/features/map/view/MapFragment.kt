package com.example.meteora.features.map.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.meteora.R
import com.example.meteora.db.local.LocalDataSourceImpl
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.features.map.viewModel.LocationViewModel
import com.example.meteora.features.map.viewModel.LocationViewModelFactory
import com.example.meteora.model.Forcast
import com.example.meteora.network.ApiClient
import com.example.meteora.network.ApiState
import com.example.meteora.network.RemoteDataSourceImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import java.io.File
import java.util.Locale

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var geocoder: Geocoder
    private lateinit var progressBar: ProgressBar
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var searchBar: EditText
    private lateinit var repository: RepositoryImpl
    private lateinit var forecast: Forcast

    private var selectedMarker: Marker? = null
    private lateinit var showWeatherDetailsButton: Button
    private lateinit var addToFavoriteButton: Button
    private lateinit var actionButtonsLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize views
        repository = RepositoryImpl(RemoteDataSourceImpl.getInstance(ApiClient.retrofit), LocalDataSourceImpl(requireContext()))
        locationViewModel = LocationViewModelFactory(repository).create(LocationViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapView = view.findViewById(R.id.map_view)
        progressBar = view.findViewById(R.id.progress_bar)
        searchBar = view.findViewById(R.id.search_bar) // Initialize searchBar
        showWeatherDetailsButton = view.findViewById(R.id.showWeatherDetailsButton)
        addToFavoriteButton = view.findViewById(R.id.addToFavoritesButton)
        actionButtonsLayout = view.findViewById(R.id.action_buttons_layout)

        geocoder = Geocoder(requireContext(), Locale.getDefault())
        setupMap()
        setupMapTapListener()
        setupSearchListener()

        showWeatherDetailsButton.setOnClickListener {
            selectedMarker?.position?.let { showWeatherDialog(it) }
            toggleActionButtons(false)
        }

        addToFavoriteButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                repository.insertForecast(forecast)
                toggleActionButtons(false)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.selectedLocation.collect { location ->
                location?.let {
                    updateMarkerAndView(it)
                    toggleActionButtons(true)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.forecastData.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is ApiState.Success -> {
                        progressBar.visibility = View.GONE
                        handleForecastData(state.data) // Handle the successful data
                    }
                    is ApiState.Failure -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }

    private fun setupMap() {
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
        Configuration.getInstance().osmdroidTileCache = File(context?.cacheDir, "osmdroid_tiles")

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentGeoPoint = GeoPoint(it.latitude, it.longitude)
                    mapView.controller.setCenter(currentGeoPoint)
                    mapView.controller.setZoom(15.0)
                }
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun setupMapTapListener() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    locationViewModel.updateSelectedLocation(it)
                    updateMarkerAndView(it)
                    toggleActionButtons(true)
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?) = false
        }

        mapView.overlays.add(MapEventsOverlay(mapEventsReceiver))
    }

    private fun setupSearchListener() {
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBar.text.toString().trim().let { locationName ->
                    if (locationName.isNotEmpty()) fetchLocationByName(locationName)
                    else Toast.makeText(context, "Enter a location name", Toast.LENGTH_SHORT).show()
                }
                true
            } else false
        }
    }

    private fun fetchLocationByName(locationName: String) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val addressList = geocoder.getFromLocationName(locationName, 1)
                if (!addressList.isNullOrEmpty()) {
                    val geoPoint = GeoPoint(addressList[0].latitude, addressList[0].longitude)
                    locationViewModel.updateSelectedLocation(geoPoint)
                    updateMarkerAndView(geoPoint)
                    toggleActionButtons(true)
                } else {
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error retrieving location", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateMarkerAndView(location: GeoPoint) {
        selectedMarker?.let { mapView.overlays.remove(it) }

        selectedMarker = Marker(mapView).apply {
            position = location
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Selected Location"
            mapView.overlays.add(this)
        }
        mapView.controller.apply {
            setCenter(location)
            setZoom(19.5)
        }
        mapView.invalidate()
    }

    private fun toggleActionButtons(show: Boolean) {
        actionButtonsLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showWeatherDialog(location: GeoPoint) {
        WeatherBasedLocationFragment().apply {
            arguments = Bundle().apply {
                putDouble("latitude", location.latitude)
                putDouble("longitude", location.longitude)
            }
        }.show(parentFragmentManager, "weatherDialog")
    }

    private fun handleForecastData(data: Forcast) {
        forecast = data
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMap()
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
