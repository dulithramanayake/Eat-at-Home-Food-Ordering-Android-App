package com.example.eatathome.Server.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCoordinatesRes {
    @GET("maps/api/geocode/json?key=AIzaSyC283W3rr19iTZWY15rSdCe4nRh4iT4SFI")
    Call<String> getGeoCode(@Query("address") String address);

    @GET("maps/api/directions/json?key=AIzaSyC283W3rr19iTZWY15rSdCe4nRh4iT4SFI")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination);


}
