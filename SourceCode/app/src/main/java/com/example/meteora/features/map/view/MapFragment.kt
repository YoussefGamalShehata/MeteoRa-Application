package com.example.meteora.features.map.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.meteora.R
import com.example.meteora.features.map.viewModel.LocationViewModel
import com.example.meteora.features.map.viewModel.LocationViewModelFactory
import com.example.meteora.features.detailsBasedLocation.view.WeatherBasedLocationFragment // Import your WeatherDialogFragment
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import java.io.File

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private val locationViewModel: LocationViewModel by lazy {
        LocationViewModelFactory().create(LocationViewModel::class.java)
    }

    private var selectedMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.map_view)

        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(Context.MODE_PRIVATE))
        Configuration.getInstance().osmdroidTileCache = File(context?.cacheDir, "osmdroid_tiles")

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(37.7749, -122.4194))

        setupMapTapListener()

        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.selectedLocation.collect { location ->
                location?.let {
                    addMarkerAtLocation(it)
                    zoomToLocation(it)
                    showWeatherDialog(it)  // Show the dialog when a location is selected
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

                    // Show a toast message with the selected latitude and longitude
                    Toast.makeText(context, "Selected Lat: ${it.latitude}, Lon: ${it.longitude}", Toast.LENGTH_SHORT).show()

                    // Show the weather dialog for the selected location
                    showWeatherDialog(it)
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

    private fun showWeatherDialog(location: GeoPoint) {
        // Create an instance of your WeatherDialogFragment
        val weatherDialog = WeatherBasedLocationFragment()

        // Pass location data to the dialog if needed
        val args = Bundle()
        args.putDouble("latitude", location.latitude)
        args.putDouble("longitude", location.longitude)
        weatherDialog.arguments = args

        // Show the dialog fragment
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
