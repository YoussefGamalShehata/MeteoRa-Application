package com.example.meteora.features.map.view

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.meteora.R
import com.example.meteora.features.detailsBasedLocation.view.WeatherBasedLocationFragment
import com.example.meteora.features.map.viewModel.LocationViewModel
import com.example.meteora.features.map.viewModel.LocationViewModelFactory
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
    private lateinit var searchBar: EditText
    private lateinit var geocoder: Geocoder
    private val locationViewModel: LocationViewModel by lazy {
        LocationViewModelFactory().create(LocationViewModel::class.java)
    }

    private var selectedMarker: Marker? = null
    private lateinit var showWeatherDetailsButton: Button
    private lateinit var addToFavoriteButton: Button
    private lateinit var actionButtonsLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.map_view)
        searchBar = view.findViewById(R.id.search_bar)
        showWeatherDetailsButton = view.findViewById(R.id.showWeatherDetailsButton)
        addToFavoriteButton = view.findViewById(R.id.addToFavoritesButton)
        actionButtonsLayout = view.findViewById(R.id.action_buttons_layout)

        // Initialize Geocoder
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        // Configure map settings
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
        Configuration.getInstance().osmdroidTileCache = File(context?.cacheDir, "osmdroid_tiles")

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(37.7749, -122.4194)) // Default center location

        setupMapTapListener()
        setupSearchListener()

        // Set click listeners for buttons
        showWeatherDetailsButton.setOnClickListener {
            selectedMarker?.let {
                actionButtonsLayout.visibility = View.GONE
                showWeatherDialog(it.position)
            }
        }

        addToFavoriteButton.setOnClickListener {
            // Logic to add to favorites (not implemented yet)
            actionButtonsLayout.visibility = View.GONE
            Toast.makeText(context, "Added to favorites (future work)", Toast.LENGTH_SHORT).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.selectedLocation.collect { location ->
                location?.let {
                    addMarkerAtLocation(it)
                    zoomToLocation(it)
                    showButtons()
                }
            }
        }

        return view
    }

    private fun setupMapTapListener() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    locationViewModel.updateSelectedLocation(it)
                    zoomToLocation(it)
                    Toast.makeText(context, "Selected Lat: ${it.latitude}, Lon: ${it.longitude}", Toast.LENGTH_SHORT).show()
                    addMarkerAtLocation(it)

                    // Show buttons when a location is tapped
                    showButtons()
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)
    }

    private fun setupSearchListener() {
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val locationName = searchBar.text.toString().trim()
                if (locationName.isNotEmpty()) {
                    fetchLocationByName(locationName)
                } else {
                    Toast.makeText(context, "Please enter a location name", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    private fun fetchLocationByName(locationName: String) {
        lifecycleScope.launch {
            try {
                val addressList = geocoder.getFromLocationName(locationName, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
                    val geoPoint = GeoPoint(address.latitude, address.longitude)

                    // Update ViewModel with the new location
                    locationViewModel.updateSelectedLocation(geoPoint)

                    // Add marker and zoom into location
                    addMarkerAtLocation(geoPoint)
                    zoomToLocation(geoPoint)

                    // Show buttons
                    showButtons()

                    Toast.makeText(context, "Found location: $locationName", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error retrieving location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarkerAtLocation(location: GeoPoint) {
        selectedMarker?.let {
            mapView.overlays.remove(it)
        }

        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Selected Location"
        selectedMarker = marker
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    private fun zoomToLocation(location: GeoPoint) {
        mapView.controller.setCenter(location)
        mapView.controller.setZoom(19.5)
    }

    private fun showButtons() {
        actionButtonsLayout.visibility = View.VISIBLE // Show the buttons layout
    }

    private fun showWeatherDialog(location: GeoPoint) {
        val weatherDialog = WeatherBasedLocationFragment()
        val args = Bundle()
        args.putDouble("latitude", location.latitude)
        args.putDouble("longitude", location.longitude)
        weatherDialog.arguments = args
        weatherDialog.show(parentFragmentManager, "weatherDialog")
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
