package com.example.eatathome.Rider.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCoordinatesRider {
    @GET("maps/api/geocode/json?key=AIzaSyA0hYfid7VtWnBdQg1LVxwSOzI-5v9zLwc&sensor=true&language=en")
    Call<String> getGeoCode(@Query("address") String address);

    @GET("maps/api/directions/json?key=AIzaSyA0hYfid7VtWnBdQg1LVxwSOzI-5v9zLwc&sensor=true&language=en&mode=driving")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination);
}
