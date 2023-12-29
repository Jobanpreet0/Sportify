package uk.ac.aston.cs3mdd.sportify000.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

import uk.ac.aston.cs3mdd.sportify000.network.NearbySearch;
import uk.ac.aston.cs3mdd.sportify000.R;
import uk.ac.aston.cs3mdd.sportify000.models.SportItem;

//class to handle the map fragment we use to display the nearest sport centers where a specific sport can be played
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_SPORT_ITEM = "sportItem";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    private SportItem sportItem;
    private double latitude;
    private double longitude;
    private GoogleMap mMap;

    //constructor takes the sportItem, the user's latitude and longitude
    public static MapFragment newInstance(SportItem sportItem, double latitude, double longitude) {
        MapFragment fragment = new MapFragment();

        //use bundle to transfer data from between fragments
        Bundle args = new Bundle();
        //key-value pairs of data:
        args.putParcelable(ARG_SPORT_ITEM, sportItem);
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);

        //store args in the MapFragment so it can be used in other fragments when called
        fragment.setArguments(args);
        return fragment;
    }


    //inflate UI with map.xml
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map, container, false);
        SupportMapFragment mapFragment = new SupportMapFragment();

        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, mapFragment)
                .addToBackStack(null)
                .commit();

        if (getArguments() != null) {
            sportItem = getArguments().getParcelable(ARG_SPORT_ITEM);
            latitude = getArguments().getDouble(ARG_LATITUDE);
            longitude = getArguments().getDouble(ARG_LONGITUDE);
        }

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //get data from bundle/fragment
        if (getArguments() != null) {
            sportItem = getArguments().getParcelable(ARG_SPORT_ITEM);
            latitude = getArguments().getDouble(ARG_LATITUDE);
            longitude = getArguments().getDouble(ARG_LONGITUDE);

            // Add a marker for the user's location with blue color
            com.google.android.gms.maps.model.LatLng userLocation = new com.google.android.gms.maps.model.LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(userLocation)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            //scale of map
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));
            //enable zoom
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Execute NearbySearchTask to fetch nearby places and display them
            new NearbySearchTask().execute(userLocation, sportItem.getName());

        }
    }

    //inner class which extends AsyncTask to perform asynchronous nearby places search without blocking the main thread.
    private class NearbySearchTask extends AsyncTask<Object, Void, PlacesSearchResponse> {
        private String sportName;

        @Override
        protected PlacesSearchResponse doInBackground(Object... params) {
            try {
                //check if both parameters are there
                if (params.length > 1 && params[0] instanceof com.google.android.gms.maps.model.LatLng && params[1] instanceof String) {

                    //assign values from the parameters
                    com.google.android.gms.maps.model.LatLng location = (com.google.android.gms.maps.model.LatLng) params[0];
                    String sportName = (String) params[1];

                    //create instance of NearbySearch
                    NearbySearch nearbySearch = new NearbySearch();

                    //return statement
                    return nearbySearch.run(new com.google.maps.model.LatLng(location.latitude, location.longitude), sportName);
                }
            } catch (Exception e) { //handle any errors
                Log.e("AsyncTaskError", "Exception in doInBackground", e);
            }
            return null;
        }




        //obtain the PlacesSearchResponse we get from NearbySearch.java
        //add markers to the map if PlacesSearchResponse is not null
        @Override
        protected void onPostExecute(PlacesSearchResponse placesSearchResponse) {
            if (placesSearchResponse != null && placesSearchResponse.results != null) {
                // Handle the response, e.g., add markers on the map
                PlacesSearchResult[] results = placesSearchResponse.results;
                Log.d("NearbySearchTask", "Response: " + placesSearchResponse.toString());


                //add markers to display the places we got from PlacesSearchResponse
                for (PlacesSearchResult result : results) {
                    com.google.android.gms.maps.model.LatLng placeLocation =
                            new com.google.android.gms.maps.model.LatLng(
                                    result.geometry.location.lat,
                                    result.geometry.location.lng
                            );

                    mMap.addMarker(new MarkerOptions().position(placeLocation).title(result.name));
                }
            } else {
                Log.e("NearbySearchTask", "Error fetching nearby places");
            }
        }


    }
}
