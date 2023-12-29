package uk.ac.aston.cs3mdd.sportify000.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.ac.aston.cs3mdd.sportify000.R;

//class used to control map_user_location.xml to show the user's location on a map
public class MapUserLocationFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapUserLocation";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;


    //inflate with map_user_location.xml
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_user_location, container, false);
        SupportMapFragment mapFragment = new SupportMapFragment();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.mapUserLocationContainer, mapFragment)
                .addToBackStack(null)
                .commit();

        //get user location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }


    //display the map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get the user's last known location and update the map
        getLastLocation();
    }


    //get user's last location if not already known
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle permissions if not granted
            return;
        }
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateMapWithLocation(location);
                            Log.i(TAG, "We got a location: (" + location.getLatitude() +
                                    ", " + location.getLongitude() + ")");
                        } else {
                            Log.i(TAG, "We failed to get a last location");
                        }
                    }
                });
    }

    //add marker to display the user's location on the map
    private void updateMapWithLocation(Location location) {
        // Add a marker for the user's location with a different color
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("Your Location"));
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))); // Set the color here
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
