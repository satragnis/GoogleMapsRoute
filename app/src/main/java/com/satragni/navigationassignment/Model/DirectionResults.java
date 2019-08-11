package com.satragni.navigationassignment.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//import okhttp3.Route;

/**
 * Created by dell on 21/12/17.
 */

public class DirectionResults {
        @SerializedName("routes")
        private List<Route> routes;

        public List<Route> getRoutes() {
            return routes;
        }
}

