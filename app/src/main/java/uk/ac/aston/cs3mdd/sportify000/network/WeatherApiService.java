package uk.ac.aston.cs3mdd.sportify000.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import uk.ac.aston.cs3mdd.sportify000.models.WeatherResponse;

public interface WeatherApiService {
    @GET
    Call<WeatherResponse> getCurrentWeather(@Url String endpoint0); //return Object Call<classDataType> using method getCurrentWeather(endpoint = url to access the API)
}
