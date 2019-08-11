package com.satragni.navigationassignment.Network;

import com.satragni.navigationassignment.Model.DirectionResults;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;


/**
 * Created by dell on 21/12/17.
 */

public interface MyApiRequestInterface {
    @GET("/maps/api/directions/json")
    public void
    getJson(@Query("origin") String origin,
            @Query("destination") String destination,
            @Query("sensor") String sensor,
            @Query("units") String units,
            @Query("mode") String mode,
            @Query("alternatives") String alternatives,
            @Query("key") String key,

            Callback<DirectionResults> callback);
}
