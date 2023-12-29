package uk.ac.aston.cs3mdd.sportify000.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import uk.ac.aston.cs3mdd.sportify000.fragments.FragmentGetRecommendations;
import uk.ac.aston.cs3mdd.sportify000.fragments.MapUserLocationFragment;
import uk.ac.aston.cs3mdd.sportify000.R;
import uk.ac.aston.cs3mdd.sportify000.fragments.UserInformationFragment;
import uk.ac.aston.cs3mdd.sportify000.fragments.WeatherFragment;
import uk.ac.aston.cs3mdd.sportify000.databinding.ActivityMainBinding;

//extends AppCompatActivity to handle fragments and the main activity
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding; //used to call views in the project
    public static final String TAG = "MYTAG"; //tag for log statements
    private FusedLocationProviderClient fusedLocationClient; //class to obtain the user's location

    @Override
    protected void onCreate(Bundle savedInstanceState) {     //create the activity
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); //store all the IDs from all the xml files
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); //inizitalize fusedLocationClient

        replaceFragment(new UserInformationFragment());    //run class UserInformationFragment when app launches


        //handle the navigation bar when clicked
        //IDs from menu/bottom_nav_menu.xml
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new FragmentGetRecommendations());
            } else if (item.getItemId() == R.id.person) {
                replaceFragment(new UserInformationFragment());
            } else if (item.getItemId() == R.id.map) {
                replaceFragment(new MapUserLocationFragment());
            } else if (item.getItemId() == R.id.weather) {
                replaceFragment(new WeatherFragment());
            }
            return true;
        });


        //check if the permission for fine and coarse location have been granted, if not proceed to ask handle the location request
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // create ActivityResultLauncher to handle the permission request
            ActivityResultLauncher<String[]> locationPermissionRequest =
                    registerForActivityResult(new ActivityResultContracts
                                    .RequestMultiplePermissions(), result -> {
                                Boolean fineLocationGranted = result.getOrDefault(
                                        android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                                Boolean coarseLocationGranted = result.getOrDefault(
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION,false);
                                if (fineLocationGranted != null && fineLocationGranted) {
                                    Log.i(TAG, "Precise location access granted.");
                                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                    Log.i(TAG, "Only approximate location access granted.");
                                } else {
                                    Log.i(TAG, "No location access granted.");
                                }
                            }
                    );

            //launch permission request
            locationPermissionRequest.launch(new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION, //request
                    android.Manifest.permission.ACCESS_COARSE_LOCATION //request
            });
        }
    }

    //get user's last location
    public void getLastLocation() {
        //check if location permission has been granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // obtain the user's last location:
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                    //run onSuccess if location is retrieved, the parameter will be filled automatically
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location
                        if (location != null) {
                            // Logic to handle location object
                            Log.i(TAG, "We got a location: (" + location.getLatitude() +
                                    ", " + location.getLongitude() + ")");
                        } else {
                            Log.i(TAG, "We failed to get a last location");
                        }
                    }
                });
    }


    //method to replace the current fragment with another specified one
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }


    //update the navbar when an item is clicked on the navbar
    public void updateSelectedItem(int itemId) {
        binding.bottomNavigationView.setSelectedItemId(itemId);
    }


    //update the navbar without updating triggering the button clicked
    public void updateBottomNavigationView(int itemId) {
        // Programmatically set the selected item in BottomNavigationView without triggering a click event
        binding.bottomNavigationView.getMenu().findItem(itemId).setChecked(true);
    }



}