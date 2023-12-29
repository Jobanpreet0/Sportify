package uk.ac.aston.cs3mdd.sportify000.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.ac.aston.cs3mdd.sportify000.R;
import uk.ac.aston.cs3mdd.sportify000.network.WeatherApiService;
import uk.ac.aston.cs3mdd.sportify000.models.WeatherResponse;

//class to control weather.xml and display information about the weather allowing users to input a city of their choice
public class WeatherFragment extends Fragment {

    private TextView temperatureTextView, weatherConditionTextView, cityNameTextView, windMetricTextView, humidityMetricTextView, precipitationTextTextView;
    private ImageView weatherIconImageView, searchImageView, windIconImageView, precipitationIconImageView, humidityIconImageView;
    private TextInputEditText cityEditText;

    private static final String BASE_URL = "https://api.weatherapi.com/v1/";
    private static final String API_KEY = "c12598fd9aa649328ef173044230411";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public WeatherFragment() {
        // Required empty public constructor
    }

    //inflate weather.xml file
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather, container, false);


        //set IDs to variables for dynamic changes
        temperatureTextView = view.findViewById(R.id.temperatureTextView);
        cityNameTextView = view.findViewById(R.id.cityNameTextView);
        weatherConditionTextView = view.findViewById(R.id.weatherConditionTextView);
        weatherIconImageView = view.findViewById(R.id.weatherIconImageView);

        windMetricTextView = view.findViewById(R.id.windMetricTextView);
        humidityMetricTextView = view.findViewById(R.id.humidityMetricTextView);
        precipitationTextTextView = view.findViewById(R.id.precipitationTextTextView);

        cityEditText = view.findViewById(R.id.cityEditText);
        searchImageView = view.findViewById(R.id.searchImageView);

        windIconImageView = view.findViewById(R.id.windIconImageView);
        precipitationIconImageView = view.findViewById(R.id.precipitationIconImageView);
        humidityIconImageView = view.findViewById(R.id.humidityIconImageView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Set up location request
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set up location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    fetchWeatherData(location.getLatitude(), location.getLongitude());
                    // Stop location updates once we have the location
                    stopLocationUpdates();
                }
            }
        };

        // Request location updates
        requestLocationUpdates();

        // Set click listener for the search button
        searchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityEditText.getText().toString();
                if (!cityName.isEmpty()) {
                    // Fetch weather data for the entered city
                    fetchWeatherDataByCity(cityName);
                    // Hide the keyboard
                    hideKeyboard();
                }
            }
        });

        return view;
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    // Stop location updates once we have the location
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    //call the api and fetch data from it
    private void fetchWeatherData(double latitude, double longitude) {
        String endpoint = BASE_URL + "current.json?key=" + API_KEY + "&q=" + latitude + "," + longitude + "&aqi=no";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        Call<WeatherResponse> call = apiService.getCurrentWeather(endpoint);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    updateWeatherUI(weatherResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                // Handle network errors or API request failure
            }
        });
    }

    //method to fetch data based on the city the user inputs
    private void fetchWeatherDataByCity(String cityName) {
        String endpoint = BASE_URL + "current.json?key=" + API_KEY + "&q=" + cityName + "&aqi=no";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        Call<WeatherResponse> call = apiService.getCurrentWeather(endpoint);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    updateWeatherUI(weatherResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                // Handle network errors or API request failure
            }
        });
    }

    //Update the UI with the retrieved information
    private void updateWeatherUI(WeatherResponse weatherResponse) {
        temperatureTextView.setText(weatherResponse.getCurrent().getCondition().getText() + " " + weatherResponse.getCurrent().getTemp_c() + "°C");
        weatherConditionTextView.setText(weatherResponse.getCurrent().getCondition().getText());

        String cityName = weatherResponse.getLocation().getName();
        cityNameTextView.setText(cityName);  // Set the city name

        Log.d("WeatherFragment", "Temperature: " + weatherResponse.getCurrent().getTemp_c());
        Log.d("WeatherFragment", "Condition: " + weatherResponse.getCurrent().getCondition().getText());

        // Additional fields
        String windMetric = weatherResponse.getCurrent().getWind_mph() + " mph";
        String humidityMetric = weatherResponse.getCurrent().getHumidity() + "%";
        String precipitationMetric = weatherResponse.getCurrent().getPrecip_mm() + " mm";

        // Set the values in corresponding TextViews
        windMetricTextView.setText(windMetric);
        humidityMetricTextView.setText(humidityMetric);
        precipitationTextTextView.setText(precipitationMetric);

        // Load images from the drawable folder
        //weatherIconImageView.setImageResource(R.drawable.weather_icon);  // Replace with your actual weather icon drawable
        searchImageView.setImageResource(R.drawable.search);  // Replace with your actual search icon drawable

        windIconImageView.setImageResource(R.drawable.wind);  // Replace with your actual wind icon drawable
        precipitationIconImageView.setImageResource(R.drawable.precipitation);  // Replace with your actual precipitation icon drawable
        humidityIconImageView.setImageResource(R.drawable.humidity);  // Replace with your actual humidity icon drawable

        // Load weather icon image using Picasso
        String imageUrl = "https:" + weatherResponse.getCurrent().getCondition().getIcon();
        Picasso.get()
                .load(imageUrl)
                .into(weatherIconImageView);
    }

    //method to hide the keyboard once the user had clicked on the search icon
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);
    }

}
