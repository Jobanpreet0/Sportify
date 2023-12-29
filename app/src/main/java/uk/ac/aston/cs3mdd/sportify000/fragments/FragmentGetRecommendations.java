package uk.ac.aston.cs3mdd.sportify000.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3mdd.sportify000.R;
import uk.ac.aston.cs3mdd.sportify000.adapters.SportAdapter;
import uk.ac.aston.cs3mdd.sportify000.network.WeatherApiService;
import uk.ac.aston.cs3mdd.sportify000.models.SportItem;
import uk.ac.aston.cs3mdd.sportify000.models.WeatherResponse;

public class FragmentGetRecommendations extends Fragment { //extends Fragment to tell that its a fragment we are dealing with

    private static final String TAG = "FragmentGetRecommendations";  //tag for the log cat
    private FusedLocationProviderClient fusedLocationClient;  //used to retrieve user location
    private boolean requestingLocationUpdates;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location mCurrentLocation;
    private TextView city;
    private TextView longitudeText;
    private TextView timestampText;

    private TextView temperatureTV, conditionTV;
    private ImageView iconIV;
    private static final String BASE_URL = "https://api.weatherapi.com/v1/"; //base https of my weather api provider
    private static final String API_KEY = "c12598fd9aa649328ef173044230411"; //my api key to access the api

    private static final String PREFS_NAME = "UserDataPrefs"; //User's SharedPreferences file

    private RecyclerView mRecyclerView;
    private SportAdapter mAdapter;
    private String weather;

    //Array of Strings that contain all weather conditions we can receive from the api
    private String[] allWeatherConditions = {
            "Sunny", "Clear", "Partly Cloudy", "Cloudy", "Overcast",
            "Mist", "Patchy rain possible", "Patchy snow possible", "Patchy sleet possible",
            "Patchy freezing drizzle possible", "Thundery outbreaks possible", "Blowing snow",
            "Blizzard", "Fog", "Freezing fog", "Patchy light drizzle", "Light drizzle",
            "Freezing drizzle", "Heavy freezing drizzle", "Patchy light rain", "Light rain",
            "Moderate rain at times", "Moderate rain", "Heavy rain at times", "Heavy rain",
            "Light freezing rain", "Moderate or heavy freezing rain", "Light sleet",
            "Moderate or heavy sleet", "Patchy light snow", "Light snow", "Patchy moderate snow",
            "Moderate snow", "Patchy heavy snow", "Heavy snow", "Ice pellets",
            "Light rain shower", "Moderate or heavy rain shower", "Torrential rain shower",
            "Light sleet showers", "Moderate or heavy sleet showers", "Light snow showers",
            "Moderate or heavy snow showers", "Light showers of ice pellets",
            "Moderate or heavy showers of ice pellets", "Patchy light rain with thunder",
            "Moderate or heavy rain with thunder", "Patchy light snow with thunder",
            "Moderate or heavy snow with thunder"
    };
    //------------------------------------------------------------


    //inflate fragment_get_recommendations.xml
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_recommendations, container, false);

        //assign IDs to variables for their dynamic modification
        mRecyclerView = view.findViewById(R.id.recyclerview);
        city = view.findViewById(R.id.city);
        conditionTV = view.findViewById(R.id.idIVcondition);
        iconIV = view.findViewById(R.id.idIVIcon);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity()); //get instance

        //check if location permission has been granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else { // get location if not already present
            getLastLocation();
        }


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //if location is empty exit
                if (locationResult == null) {
                    Log.i(TAG, "Location Update: NO LOCATION");
                    return;
                }

                //else run updateUILocation and fetchWeatherData
                for (Location location : locationResult.getLocations()) {
                    Log.i(TAG, "Location Update: (" + location.getLatitude() +
                            ", " + location.getLongitude() + ")");
                    updateUILocation(location);
                    fetchWeatherData(location.getLatitude(), location.getLongitude());
                }
            }
        };

        return view;
    }


    //get weather data based on current latitude and longitude
    private void fetchWeatherData(double latitude, double longitude) {

        //endpoint URL
        String endpoint = BASE_URL + "current.json?key=" + API_KEY + "&q=" + latitude + "," + longitude + "&aqi=no";

        //object retrofit to access the URL and convert from JSON to Class
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build(); //creates instance

        //create instance of WeatherApiService using the retrofit instance
        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        //store the Call object from apiService.getCurrentWeather(endpoint); to variable call
        Call<WeatherResponse> call = apiService.getCurrentWeather(endpoint);

        //asynchronous API call
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                //if API response is successful
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        WeatherResponse weatherResponse = response.body();

                        //update variables with the data with get from the API
                        weather = weatherResponse.getCurrent().getCondition().getText();
                        conditionTV.setText(weatherResponse.getCurrent().getCondition().getText() + " " + weatherResponse.getCurrent().getTemp_c() + "°C");
                        city.setText("Location: " + weatherResponse.getLocation().getName());
                        String imageUrl = "https:" + weatherResponse.getCurrent().getCondition().getIcon();


                        // Load and display the image using Picasso in the fragment
                        Picasso.get()
                                .load(imageUrl)
                                .into(iconIV);

                        // call the user's SharedPreferences file
                        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

                        // Store true or false based on what the user has in its SharedPreferences file
                        boolean beachVolleyballChecked = prefs.getBoolean("beachVolleyball", false);
                        boolean indoorVolleyballChecked = prefs.getBoolean("indoorVolleyball", false);
                        boolean tennisChecked = prefs.getBoolean("tennis", false);
                        boolean golfingChecked = prefs.getBoolean("golfing", false);
                        boolean swimmingIndoorChecked = prefs.getBoolean("indoorSwimming", false);
                        boolean swimmingOutdoorChecked = prefs.getBoolean("outdoorSwimming", false);
                        boolean footballIndoorChecked = prefs.getBoolean("indoorFootball", false);
                        boolean footballOutdoorChecked = prefs.getBoolean("outdoorFootball", false);
                        boolean cricketIndoorChecked = prefs.getBoolean("indoorCricket", false);
                        boolean cricketOutdoorChecked = prefs.getBoolean("outdoorCricket", false);
                        boolean rockClimbingIndoorsChecked = prefs.getBoolean("rockClimbingOutdoors", false);
                        boolean rockClimbingOutdoorsChecked = prefs.getBoolean("rockClimbingIndoors", false);
                        boolean bowlingChecked = prefs.getBoolean("bowling", false);
                        boolean badmintonChecked = prefs.getBoolean("badminton", false);
                        boolean squashChecked = prefs.getBoolean("squash", false);
                        boolean sleddingChecked = prefs.getBoolean("sledding", false);
                        boolean skiingChecked = prefs.getBoolean("skiing", false);
                        boolean snowboardingChecked = prefs.getBoolean("snowboarding", false);
                        boolean iceSkatingChecked = prefs.getBoolean("iceSkating", false);
                        boolean hikingChecked = prefs.getBoolean("hiking", false);
                        boolean racquetballChecked = prefs.getBoolean("racquetball", false);
                        boolean sailingChecked = prefs.getBoolean("sailing", false);
                        boolean rowingChecked = prefs.getBoolean("rowing", false);
                        boolean fishingChecked = prefs.getBoolean("fishing", false);
                        boolean archeryChecked = prefs.getBoolean("archery", false);
                        boolean paraglidingChecked = prefs.getBoolean("paragliding", false);
                        boolean windsurfingChecked = prefs.getBoolean("windsurfing", false);
                        boolean bungeeJumpingChecked = prefs.getBoolean("bungeeJumping", false);

                        //create a list to store the sports the user likes
                        List<SportItem> filteredSportsList = new ArrayList<>();


                        // Filter sport based on user preferences and weather conditions
                        if (hikingChecked && ("Sunny".equals(weather) ||
                                "Clear".equals(weather) ||
                                "Partly cloudy".equals(weather) ||
                                "Cloudy".equals(weather) ||
                                "Overcast".equals(weather) ||
                                "Mist".equals(weather) ||
                                "Fog".equals(weather) ||
                                "Freezing fog".equals(weather))
                        ) {

                            //if sport can be played, add the sport to the filteredSportList
                            int sportImageResourceId = R.drawable.hiking;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Hiking", sportImageResourceId, latitude, longitude));
                            Log.d(TAG, "Added Hiking to filteredSportsList. Weather: " + weather + ", Hiking Checked: " + hikingChecked);
                        }



                        // Filter sports based on user preferences and weather conditions
                        if (sailingChecked && ("Sunny".equals(weather) ||
                                "Clear".equals(weather) ||
                                "Partly cloudy".equals(weather) ||
                                "Cloudy".equals(weather) ||
                                "Overcast".equals(weather))
                        ) {
                            int sportImageResourceId = R.drawable.sailing;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Sailing", sportImageResourceId, latitude, longitude));
                            Log.d(TAG, "Added Sailing to filteredSportsList. Weather: " + weather + ", Sailing Checked: " + sailingChecked);
                        }



                        // Filter sports based on user preferences and weather conditions
                        if (rowingChecked && ("Sunny".equals(weather) ||
                                "Clear".equals(weather) ||
                                "Partly cloudy".equals(weather) ||
                                "Cloudy".equals(weather) ||
                                "Overcast".equals(weather))
                        ) {
                            int sportImageResourceId = R.drawable.rowing;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Rowing", sportImageResourceId, latitude, longitude));
                            Log.d(TAG, "Added Rowing to filteredSportsList. Weather: " + weather + ", Rowing Checked: " + rowingChecked);
                        }


                        // Filter sports based on user preferences and weather conditions
                        if (fishingChecked && ("Sunny".equals(weather) ||
                                "Clear".equals(weather) ||
                                "Partly cloudy".equals(weather) ||
                                "Cloudy".equals(weather) ||
                                "Overcast".equals(weather) ||
                                "Mist".equals(weather) ||
                                "Fog".equals(weather) ||
                                "Patchy light drizzle".equals(weather) ||
                                "Light drizzle".equals(weather) ||
                                "Freezing drizzle".equals(weather) ||
                                "Heavy freezing drizzle".equals(weather) ||
                                "Patchy light rain".equals(weather) ||
                                "Light rain".equals(weather) ||
                                "Moderate rain at times".equals(weather) ||
                                "Moderate rain".equals(weather) ||
                                "Heavy rain at times".equals(weather) ||
                                "Heavy rain".equals(weather) ||
                                "Light freezing rain".equals(weather) ||
                                "Moderate or heavy freezing rain".equals(weather) ||
                                "Patchy light snow".equals(weather) ||
                                "Light snow".equals(weather) ||
                                "Patchy moderate snow".equals(weather) ||
                                "Moderate snow".equals(weather) ||
                                "Light rain shower".equals(weather) ||
                                "Moderate or heavy rain shower".equals(weather) ||
                                "Torrential rain shower".equals(weather) ||
                                "Light sleet showers".equals(weather) ||
                                "Moderate or heavy sleet showers".equals(weather))
                        ) {
                            int sportImageResourceId = R.drawable.fishing;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Fishing", sportImageResourceId, latitude, longitude));
                            Log.d(TAG, "Added Fishing to filteredSportsList. Weather: " + weather + ", Fishing Checked: " + fishingChecked);
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (beachVolleyballChecked && ("Sunny".equals(weather) || "Clear".equals(weather))) {
                            int sportImageResourceId = R.drawable.beachvolleyball;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Beach VolleyBall", sportImageResourceId, latitude, longitude));
                            Log.d(TAG, "Added Beach VolleyBall to filteredSportsList. Weather: " + weather + ", Beach Volleyball Checked: " + beachVolleyballChecked);
                        }




                        // Filter sports based on user preferences and weather conditions
                        if (indoorVolleyballChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.indoorvolleyball;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Indoor VolleyBall", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (swimmingIndoorChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.indoorswimming;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Indoor Swimming", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (footballIndoorChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.football;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Indoor Football", sportImageResourceId, latitude, longitude));
                            Log.d(TAG, "Added Indoor Football to filteredSportsList. Weather: " + weather);
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (cricketIndoorChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.indoorcricket;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Indoor Cricket", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (rockClimbingIndoorsChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.indoorrockclimbing;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Indoor Rock Climbing", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (bowlingChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.bowling;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Bowling", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (badmintonChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.badminton;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Badminton", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (squashChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.squash;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Squash", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (racquetballChecked && isWeatherSuitable(weather, allWeatherConditions)) {
                            int sportImageResourceId = R.drawable.racquetball;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Racquetball", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (tennisChecked && "Sunny".equals(weather) || "Clear".equals(weather) || "Partly Cloudy".equals(weather)) {
                            int sportImageResourceId = R.drawable.tennis;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Tennis", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (golfingChecked && "Sunny".equals(weather) || "Clear".equals(weather) || "Partly Cloudy".equals(weather)) {
                            int sportImageResourceId = R.drawable.golf;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Golf", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (swimmingOutdoorChecked && "Sunny".equals(weather) || "Clear".equals(weather)) {
                            int sportImageResourceId = R.drawable.outdoorswimming;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Outdoor Swimming", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (footballOutdoorChecked && "Sunny".equals(weather) || "Clear".equals(weather) || "Partly Cloudy".equals(weather)) {
                            int sportImageResourceId = R.drawable.outdoorfootball;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Outdoor Football", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (cricketOutdoorChecked && "Sunny".equals(weather) || "Clear".equals(weather) || "Partly Cloudy".equals(weather)) {
                            int sportImageResourceId = R.drawable.outdoorcricket;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Outdoor Cricket", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (rockClimbingOutdoorsChecked && "Sunny".equals(weather) || "Clear".equals(weather) || "Partly Cloudy".equals(weather)) {
                            int sportImageResourceId = R.drawable.outdoorclimbing;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Outdoor Rockclimbing", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (sleddingChecked && "Blowing snow".equals(weather) ||
                                "Blizzard".equals(weather) ||
                                "Patchy snow possible".equals(weather) ||
                                "Patchy light snow".equals(weather) ||
                                "Light snow".equals(weather) ||
                                "Patchy moderate snow".equals(weather) ||
                                "Moderate snow".equals(weather) ||
                                "Patchy heavy snow".equals(weather) ||
                                "Heavy snow".equals(weather)) {
                            int sportImageResourceId = R.drawable.sledding;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Sledding", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (skiingChecked && "Blowing snow".equals(weather) ||
                                "Blizzard".equals(weather) ||
                                "Patchy snow possible".equals(weather) ||
                                "Patchy light snow".equals(weather) ||
                                "Light snow".equals(weather) ||
                                "Patchy moderate snow".equals(weather) ||
                                "Moderate snow".equals(weather) ||
                                "Patchy heavy snow".equals(weather) ||
                                "Heavy snow".equals(weather) ||
                                "Ice pellets".equals(weather)) {
                            int sportImageResourceId = R.drawable.skiing;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Skiing", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (snowboardingChecked && "Blowing snow".equals(weather) ||
                                "Blizzard".equals(weather) ||
                                "Patchy snow possible".equals(weather) ||
                                "Patchy light snow".equals(weather) ||
                                "Light snow".equals(weather) ||
                                "Patchy moderate snow".equals(weather) ||
                                "Moderate snow".equals(weather) ||
                                "Patchy heavy snow".equals(weather) ||
                                "Heavy snow".equals(weather) ||
                                "Ice pellets".equals(weather)) {
                            int sportImageResourceId = R.drawable.snowboarding;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Snowboarding", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (iceSkatingChecked &&
                                "Patchy light snow".equals(weather) ||
                                "Light snow".equals(weather) ||
                                "Patchy moderate snow".equals(weather) ||
                                "Moderate snow".equals(weather) ||
                                "Patchy heavy snow".equals(weather) ||
                                "Heavy snow".equals(weather) ||
                                "Ice pellets".equals(weather)
                        ) {
                            int sportImageResourceId = R.drawable.iceskating;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Ice Skating", sportImageResourceId, latitude, longitude));
                        }

                        // Filter sports based on user preferences and weather conditions
                        if (archeryChecked && ("Sunny".equals(weather) || "Clear".equals(weather))) {
                            int sportImageResourceId = R.drawable.archery;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Archery", sportImageResourceId, latitude, longitude));
                        }

                        if (paraglidingChecked && ("Sunny".equals(weather) || "Clear".equals(weather))) {
                            int sportImageResourceId = R.drawable.paragliding;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Paragliding", sportImageResourceId, latitude, longitude));
                        }

                        if (windsurfingChecked && ("Sunny".equals(weather) || "Clear".equals(weather))) {
                            int sportImageResourceId = R.drawable.surfing;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Wind Surfing", sportImageResourceId, latitude, longitude));
                        }

                        if (bungeeJumpingChecked && ("Sunny".equals(weather) || "Clear".equals(weather))) {
                            int sportImageResourceId = R.drawable.bungeejumping;
                            double latitude = mCurrentLocation.getLatitude(); // Get the latitude from mCurrentLocation
                            double longitude = mCurrentLocation.getLongitude(); // Get the longitude from mCurrentLocation
                            filteredSportsList.add(new SportItem("Bungee Jumping", sportImageResourceId, latitude, longitude));
                        }

                        // Debugging: Add a log message to see the content of filteredSportsList
                        for (SportItem sportItem : filteredSportsList) {
                            Log.d("check", "Filtered Sport: " + sportItem.getName());
                        }

                        //update the RecyclerView with the list of Filtered List
                        //create instance of SportAdapter to add data to RecyclerView
                        mAdapter = new SportAdapter(getContext(), filteredSportsList);
                        //update variable mRecyclerView to adapt mAdapter
                        mRecyclerView.setAdapter(mAdapter);
                        //update mAdapter
                        mAdapter.updateData(filteredSportsList);
                        //arrange the items in the mRecyclerView
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    } else { // Handle unsuccessful response
                        Log.e(TAG, "Unsuccessful API response");

                    }
                }
            }

            //if API response is failure
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failure: " + t.getMessage());
            }
        });
    }

    // Get Last Location
    private void getLastLocation() {
        //check if permission has been granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //get location
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateUILocation(location);
                            Log.i(TAG, "We got a location: (" + location.getLatitude() +
                                    ", " + location.getLongitude() + ")");
                            // Call fetchWeatherData method here with obtained latitude and longitude
                            fetchWeatherData(location.getLatitude(), location.getLongitude());
                        } else {
                            Log.i(TAG, "We failed to get a last location");
                        }
                    }
                });
    }


    //update mCurrentLocation variable with the current location
    //rest of code in the method is not used
    private void updateUILocation(Location location) {
        mCurrentLocation = location;
        Date date = new Date(location.getTime());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String dateFormatted = formatter.format(date);
    }


    //used to check if current weather condition falls under the list of suitable conditions list I declared before.
    private boolean isWeatherSuitable(String currentWeather, String[] suitableConditions) {
        for (String condition : suitableConditions) {
            if (condition.equalsIgnoreCase(currentWeather)) {
                Log.d(TAG, "Weather is suitable. Current Weather: " + currentWeather);
                return true;
            }
        }
        Log.d(TAG, "Weather is not suitable. Current Weather: " + currentWeather);
        return false;
    }



}
