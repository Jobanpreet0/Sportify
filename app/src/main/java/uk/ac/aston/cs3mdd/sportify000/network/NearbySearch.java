package uk.ac.aston.cs3mdd.sportify000.network;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.RankBy;
import com.google.maps.model.LatLng;
import java.io.IOException;

//class used to search nearby places using Google Places API
public class NearbySearch {

    //give coordinates and keyword to search nearby places
    public PlacesSearchResponse run(LatLng location, String sportName) {
        PlacesSearchResponse request = new PlacesSearchResponse();

        //access Google Places API
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("Your API Key")
                .build();

        try {
            //request search
            request = PlacesApi.nearbySearchQuery(context, location)
                    .radius(5000) //search radius
                    .rankby(RankBy.PROMINENCE)
                    .keyword(sportName)  // Use the sportName as the keyword
                    .language("en")
                    .await();
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return request;
    }
}


