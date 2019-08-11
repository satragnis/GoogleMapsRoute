package com.satragni.navigationassignment.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dell on 21/12/17.
 */

public class Route {
    @SerializedName("overview_polyline")
    private OverviewPolyLine overviewPolyLine;

    private List<Legs> legs;

    public OverviewPolyLine getOverviewPolyLine() {
        return overviewPolyLine;
    }

    public List<Legs> getLegs() {
        return legs;
    }
}
