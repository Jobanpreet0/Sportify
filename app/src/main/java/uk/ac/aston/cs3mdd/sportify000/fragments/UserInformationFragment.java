package uk.ac.aston.cs3mdd.sportify000.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import uk.ac.aston.cs3mdd.sportify000.R;
import uk.ac.aston.cs3mdd.sportify000.activity.MainActivity;
import uk.ac.aston.cs3mdd.sportify000.fragments.FragmentGetRecommendations;

//extends Fragment to tell that this is an Android Application
public class UserInformationFragment extends Fragment {

    private GridLayout sportsGridLayout; //to display the checkboxes
    private SearchView searchView; //to search from the checkboxes
    private static final String PREFS_NAME = "UserDataPrefs"; //name of the SharedPreferences file we use to store the data

    public UserInformationFragment() {
        // Required empty public constructor
    }


    //run onCreateView to inflate xml file: fragment_user_information
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_information, container, false);

        // Load user data from SharedPreferences if available
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        String name = prefs.getString("name", "");
        String location = prefs.getString("location", "");

        // Retrieve values for each sport from the SharedPreference file is already present
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

        // Label each ID from xml file to a variable so it can be updated
        EditText nameEditText = view.findViewById(R.id.nameEditText);
        CheckBox beachVolleyballCheckBox = view.findViewById(R.id.beachVolleyballCheckBox);
        CheckBox indoorVolleyballCheckBox = view.findViewById(R.id.indoorVolleyballCheckBox);
        CheckBox tennisCheckBox = view.findViewById(R.id.tennisCheckBox);
        CheckBox golfingCheckBox = view.findViewById(R.id.golfingCheckBox);
        CheckBox swimmingIndoorCheckBox = view.findViewById(R.id.swimmingIndoorCheckBox);
        CheckBox swimmingOutdoorCheckBox = view.findViewById(R.id.swimmingOutdoorCheckBox);
        CheckBox footballIndoorCheckBox = view.findViewById(R.id.footballIndoorCheckBox);
        CheckBox footballOutdoorCheckBox = view.findViewById(R.id.footballOutdoorCheckBox);
        CheckBox cricketIndoorCheckBox = view.findViewById(R.id.cricketIndoorCheckBox);
        CheckBox cricketOutdoorCheckBox = view.findViewById(R.id.cricketOutdoorCheckBox);
        CheckBox rockClimbingIndoorsCheckBox = view.findViewById(R.id.rockClimbingIndoorsCheckBox);
        CheckBox rockClimbingOutdoorsCheckBox = view.findViewById(R.id.rockClimbingOutdoorsCheckBox);
        CheckBox bowlingCheckBox = view.findViewById(R.id.bowlingCheckBox);
        CheckBox badmintonCheckBox = view.findViewById(R.id.badmintonCheckBox);
        CheckBox squashCheckBox = view.findViewById(R.id.squashCheckBox);
        CheckBox sleddingCheckBox = view.findViewById(R.id.sleddingCheckBox);
        CheckBox skiingCheckBox = view.findViewById(R.id.skiingCheckBox);
        CheckBox snowboardingCheckBox = view.findViewById(R.id.snowboardingCheckBox);
        CheckBox iceSkatingCheckBox = view.findViewById(R.id.iceSkatingCheckBox);
        CheckBox hikingCheckBox = view.findViewById(R.id.hikingCheckBox);
        CheckBox racquetballCheckBox = view.findViewById(R.id.racquetballCheckBox);
        CheckBox sailingCheckBox = view.findViewById(R.id.sailingCheckBox);
        CheckBox rowingCheckBox = view.findViewById(R.id.rowingCheckBox);
        CheckBox fishingCheckBox = view.findViewById(R.id.fishingCheckBox);
        CheckBox archeryCheckBox = view.findViewById(R.id.archeryCheckBox);
        CheckBox paraglidingCheckBox = view.findViewById(R.id.paraglidingCheckBox);
        CheckBox windsurfingCheckBox = view.findViewById(R.id.windsurfingCheckBox);
        CheckBox bungeeJumpingCheckBox = view.findViewById(R.id.bungeeJumpingCheckBox);


        sportsGridLayout = view.findViewById(R.id.sportsGridLayout);
        searchView = view.findViewById(R.id.searchView);


        // Populate UI elements with loaded data
        // ID.value
        nameEditText.setText(name);
        // ID.setTrue or False
        beachVolleyballCheckBox.setChecked(beachVolleyballChecked);
        indoorVolleyballCheckBox.setChecked(indoorVolleyballChecked);
        tennisCheckBox.setChecked(tennisChecked);
        golfingCheckBox.setChecked(golfingChecked);
        swimmingIndoorCheckBox.setChecked(swimmingIndoorChecked);
        swimmingOutdoorCheckBox.setChecked(swimmingOutdoorChecked);
        footballIndoorCheckBox.setChecked(footballIndoorChecked);
        footballOutdoorCheckBox.setChecked(footballOutdoorChecked);
        cricketIndoorCheckBox.setChecked(cricketIndoorChecked);
        cricketOutdoorCheckBox.setChecked(cricketOutdoorChecked);
        rockClimbingIndoorsCheckBox.setChecked(rockClimbingIndoorsChecked);
        rockClimbingOutdoorsCheckBox.setChecked(rockClimbingOutdoorsChecked);
        bowlingCheckBox.setChecked(bowlingChecked);
        badmintonCheckBox.setChecked(badmintonChecked);
        squashCheckBox.setChecked(squashChecked);
        sleddingCheckBox.setChecked(sleddingChecked);
        skiingCheckBox.setChecked(skiingChecked);
        snowboardingCheckBox.setChecked(snowboardingChecked);
        iceSkatingCheckBox.setChecked(iceSkatingChecked);
        hikingCheckBox.setChecked(hikingChecked);
        racquetballCheckBox.setChecked(racquetballChecked);
        sailingCheckBox.setChecked(sailingChecked);
        rowingCheckBox.setChecked(rowingChecked);
        fishingCheckBox.setChecked(fishingChecked);
        archeryCheckBox.setChecked(archeryChecked);
        paraglidingCheckBox.setChecked(paraglidingChecked);
        windsurfingCheckBox.setChecked(windsurfingChecked);
        bungeeJumpingCheckBox.setChecked(bungeeJumpingChecked);


        // Set a listener for the search view to filter the checkboxes
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter checkboxes based on the search query
                filterCheckboxes(newText);
                return true;
            }
        });

        // Set a click listener for the "Get Recommendation" button to update the SharedPreferences file when clicked on the button
        Button getRecommendationButton = view.findViewById(R.id.getRecommendationButton);
        getRecommendationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update variables on click
                String name = nameEditText.getText().toString();
                boolean beachVolleyball = beachVolleyballCheckBox.isChecked();
                boolean indoorVolleyball = indoorVolleyballCheckBox.isChecked();
                boolean tennis = tennisCheckBox.isChecked();
                boolean golfing = golfingCheckBox.isChecked();
                boolean swimmingIndoor = swimmingIndoorCheckBox.isChecked();
                boolean swimmingOutdoor = swimmingOutdoorCheckBox.isChecked();
                boolean footballIndoor = footballIndoorCheckBox.isChecked();
                boolean footballOutdoor = footballOutdoorCheckBox.isChecked();
                boolean cricketIndoor = cricketIndoorCheckBox.isChecked();
                boolean cricketOutdoor = cricketOutdoorCheckBox.isChecked();
                boolean rockClimbingIndoors = rockClimbingIndoorsCheckBox.isChecked();
                boolean rockClimbingOutdoors = rockClimbingOutdoorsCheckBox.isChecked();
                boolean bowling = bowlingCheckBox.isChecked();
                boolean badminton = badmintonCheckBox.isChecked();
                boolean squash = squashCheckBox.isChecked();
                boolean sledding = sleddingCheckBox.isChecked();
                boolean skiing = skiingCheckBox.isChecked();
                boolean snowboarding = snowboardingCheckBox.isChecked();
                boolean iceSkating = iceSkatingCheckBox.isChecked();
                boolean hiking = hikingCheckBox.isChecked();
                boolean racquetball = racquetballCheckBox.isChecked();
                boolean sailing = sailingCheckBox.isChecked();
                boolean rowing = rowingCheckBox.isChecked();
                boolean fishing = fishingCheckBox.isChecked();
                boolean archery = archeryCheckBox.isChecked();
                boolean paragliding = paraglidingCheckBox.isChecked();
                boolean windsurfing = windsurfingCheckBox.isChecked();
                boolean bungeeJumping = bungeeJumpingCheckBox.isChecked();


                // Save the updated variables/data to SharedPreferences
                saveUserData(name, beachVolleyball, indoorVolleyball, tennis,
                        golfing, swimmingIndoor, swimmingOutdoor, footballIndoor, footballOutdoor,
                        cricketIndoor, cricketOutdoor, rockClimbingIndoors, rockClimbingOutdoors, bowling, badminton,
                        squash, sledding, skiing, snowboarding, iceSkating,
                        hiking, racquetball, sailing, rowing, fishing, archery, paragliding,
                        windsurfing, bungeeJumping);

                // Navigate to the next fragment after the click

                FragmentGetRecommendations fragmentGetRecommendations = new FragmentGetRecommendations();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                //replace frame_layout from activity_main.xml to fragmentGetRecommendations.
                transaction.replace(R.id.frame_layout, fragmentGetRecommendations);
                transaction.addToBackStack(null);
                transaction.commit();

                // Update the selected item in the bottom navigation bar
                ((MainActivity) requireActivity()).updateSelectedItem(R.id.home); // Assuming R.id.home is the ID for the "Home" item

                //debug method (display log information) to see which checkbox is selected
                logCheckedBoxes();

            }
        });

        return view;
    }

    //method used that takes in new data and replaces it with the old one in the SharedPreferences file
    private void saveUserData(String name,
                              boolean beachVolleyball, boolean indoorVolleyball,
                              boolean tennis,
                              boolean golfing,
                              boolean indoorSwimming, boolean outdoorSwimming,
                              boolean indoorFootball, boolean outdoorFootball,
                              boolean indoorCricket, boolean outdoorCricket,
                              boolean rockClimbingOutdoors, boolean rockClimbingIndoors,
                              boolean Bowling, boolean badminton,
                              boolean squash,
                              boolean sledding, boolean skiing,
                              boolean snowboarding, boolean IceSkating,
                              boolean hiking,
                              boolean racquetball, boolean sailing,
                              boolean rowing, boolean fishing,
                              boolean archery, boolean paragliding,
                              boolean windsurfing, boolean bungeeJumping) {

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //replace new data with old one by using keys as reference
        editor.putString("name", name);
        editor.putBoolean("beachVolleyball", beachVolleyball);
        editor.putBoolean("indoorVolleyball", indoorVolleyball);
        editor.putBoolean("tennis", tennis);
        editor.putBoolean("golfing", golfing);
        editor.putBoolean("outdoorSwimming", outdoorSwimming);
        editor.putBoolean("indoorSwimming", indoorSwimming);
        editor.putBoolean("outdoorFootball", outdoorFootball);
        editor.putBoolean("indoorFootball", indoorFootball);
        editor.putBoolean("outdoorCricket", outdoorCricket);
        editor.putBoolean("indoorCricket", indoorCricket);
        editor.putBoolean("rockClimbingOutdoors", rockClimbingOutdoors);
        editor.putBoolean("rockClimbingIndoors", rockClimbingIndoors);
        editor.putBoolean("bowling", Bowling);
        editor.putBoolean("badminton", badminton);
        editor.putBoolean("squash", squash);
        editor.putBoolean("sledding", sledding);
        editor.putBoolean("skiing", skiing);
        editor.putBoolean("snowboarding", snowboarding);
        editor.putBoolean("iceSkating", IceSkating);
        editor.putBoolean("hiking", hiking);
        editor.putBoolean("racquetball", racquetball);
        editor.putBoolean("sailing", sailing);
        editor.putBoolean("rowing", rowing);
        editor.putBoolean("fishing", fishing);
        editor.putBoolean("archery", archery);
        editor.putBoolean("paragliding", paragliding);
        editor.putBoolean("windsurfing", windsurfing);
        editor.putBoolean("bungeeJumping", bungeeJumping);

        editor.apply();
    }


    //filter checkboxes for the search field
    private void filterCheckboxes(String query) {           //take in input text from user
        for (int i = 0; i < sportsGridLayout.getChildCount(); i++) {   //iterate through child views (UI elements) in gridlayout
            View child = sportsGridLayout.getChildAt(i);
            if (child instanceof CheckBox) {  //check if child is a checkbox
                CheckBox checkBox = (CheckBox) child;
                String checkBoxText = checkBox.getText().toString().toLowerCase(); //retrieve checkbox text in lowercase

                //handle visibility based on input
                if (TextUtils.isEmpty(query) || checkBoxText.contains(query.toLowerCase())) {
                    checkBox.setVisibility(View.VISIBLE);
                } else {
                    checkBox.setVisibility(View.GONE);
                }
            }
        }
    }

    //Method to debug and see if all checkboxes are working properly
    private void logCheckedBoxes() {
        for (int i = 0; i < sportsGridLayout.getChildCount(); i++) {
            View child = sportsGridLayout.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) child;
                if (checkBox.isChecked()) {
                    // Log the checked box
                    String checkBoxText = checkBox.getText().toString();
                    Log.d("CheckedBoxes", "Checked: " + checkBoxText);
                }
            }
        }
    }

}
