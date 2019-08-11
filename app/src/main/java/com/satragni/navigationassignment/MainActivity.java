package com.satragni.navigationassignment;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.widget.SweetView;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.satragni.navigationassignment.Model.DirectionResults;
import com.satragni.navigationassignment.Model.DirectionsList;
import com.satragni.navigationassignment.Model.Route;
import com.satragni.navigationassignment.Model.Steps;
import com.satragni.navigationassignment.Network.MyApiRequestInterface;

import com.satragni.navigationassignment.Utils.DatabaseHelper;
import com.satragni.navigationassignment.Utils.Params;

import com.satragni.navigationassignment.Utils.RouteDecode;
import com.satragni.navigationassignment.Utils.Util;


import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.satragni.navigationassignment.Utils.Params.LOG_TAG;
import static com.satragni.navigationassignment.Utils.Params.TO_LOCATION_LATITUDE;
import static com.satragni.navigationassignment.Utils.Params.TO_LOCATION_LONGITUDE;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, PlaceSelectionListener {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 8888;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_SRC = 4444;
    private static final double BOUND_RADIUS = 300000;//300KM
    MapView mMapView;
    public MapFragment mapFragment;
    private static GoogleMap mGoogleMap;
    private static GoogleApiClient googleApiClient;
    private static Location mylocation;
    private LatLng sourceLatlng, destinationLatLng = null, fromPosition, toPosition;
    private LinearLayout searchBox;
    private RelativeLayout DirectionBox, pageLayout, mapRlayout;
    Location location = new Location("My Location");
    TextView sourceTV, destinationTV, listHeading;
    ListView directionListView;
    ProgressBar progressBar;
    static DirectionsList[] directionsListArray;
    private FlowingDrawer mDrawer;
    ImageView menuIcon, locateBTN;

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 30 * 1000;  /* 30 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 sec */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Params.ManualLocationFlag = false;
        Params.googleApiCalled = false;
        if(checkAndRequestPermissions()) {


            getLastLocation();
            startLocationUpdates();


            new CountDownTimer(1500, 1000) {
                @Override
                public void onTick(long l) {
                    Log.d("Flash", "onTick: " + l);
                }

                @Override
                public void onFinish() {
                    // previously invisible view
                    View myView = findViewById(R.id.mapLayout);
                    myView.setVisibility(View.VISIBLE);
                    //-----------------------
                }
            }.start();


            sourceTV = (TextView) findViewById(R.id.search_src_text);
            destinationTV = (TextView) findViewById(R.id.search_dest_text);
            listHeading = (TextView) findViewById(R.id.listHeading);
            directionListView = (ListView) findViewById(R.id.directionList);
            DirectionBox = (RelativeLayout) findViewById(R.id.direction_container);
            pageLayout = (RelativeLayout) findViewById(R.id.pageLayout);
            mapRlayout = (RelativeLayout) findViewById(R.id.mapLayout);
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            menuIcon = (ImageView) findViewById(R.id.menu_icon);
            locateBTN = (ImageView) findViewById(R.id.id_locate);
            progressBar = (ProgressBar)findViewById(R.id.progreassIndicatorM);


            //flowing drawer
            mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
            mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
            mDrawer.setAlpha(0.8f);
            mDrawer.setElevation(4);
            mDrawer.setFadingEdgeLength(8);
            progressBar.setVisibility(View.GONE);


            mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
                @Override
                public void onDrawerStateChange(int oldState, int newState) {
                    if (newState == ElasticDrawer.STATE_CLOSED) {
                        Log.i("MainActivity", "Drawer STATE_CLOSED");
                        pageLayout.setAlpha(1.0f);
                    } else if (newState == ElasticDrawer.STATE_OPEN) {
                        Log.i("MainActivity", "Drawer STATE_OPEN");
                        pageLayout.setBackgroundColor(getResources().getColor(R.color.white));
                        pageLayout.setAlpha(0.4f);
                    } else if (newState == ElasticDrawer.STATE_DRAGGING_OPEN) {
                        float i = 0.2f;
                        pageLayout.setBackgroundColor(getResources().getColor(R.color.white));
                        pageLayout.setAlpha(i++);
                    } else if (newState == ElasticDrawer.STATE_DRAGGING_CLOSE) {
                        float i = 1f;
                        pageLayout.setBackgroundColor(getResources().getColor(R.color.white));
                        pageLayout.setAlpha(i--);
                    }
                }

                @Override
                public void onDrawerSlide(float openRatio, int offsetPixels) {
                    Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
                }
            });
            setUpGClient();
//        getMyLocation();
            if (Util.checkNetStatus(MainActivity.this)) {
                sourceTV.setOnClickListener(sourceLocationListener);
                destinationTV.setOnClickListener(destLocationListener);
            }
            if (sourceTV.getText().toString().isEmpty()
                    || destinationTV.getText().toString().isEmpty()) {
                DirectionBox.setVisibility(View.GONE);
            }
            mapFragment.getMapAsync(onMapReadyCallback);


            locateBTN.setOnClickListener(locateBTNListener);
            menuIcon.setOnClickListener(menuIconListener);

        }else{
            checkAndRequestPermissions();
        }
        //==-================


    }


    View.OnClickListener menuIconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawer.openMenu(true);
        }
    };

    View.OnClickListener locateBTNListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };


    View.OnClickListener sourceLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Util.checkNetStatus(MainActivity.this);
            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(MainActivity.this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_SRC);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
                showToast(getString(R.string.general_error_msg));
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
                showToast(getString(R.string.general_error_msg));
            }
        }
    };


    View.OnClickListener destLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (!sourceTV.getText().toString().isEmpty()) {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setBoundsBias(Util.toBounds(new LatLng(
                                            Double.parseDouble(DatabaseHelper.getAccountDetails(MainActivity.this, Params.FROM_LOCATION_LATITUDE)),
                                            Double.parseDouble(DatabaseHelper.getAccountDetails(MainActivity.this, Params.FROM_LOCATION_LONGITUDE))
                                    ), BOUND_RADIUS))
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                } else {
                    showToast("Please select the source address first.");
                }
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
                showToast(getString(R.string.general_error_msg));
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
                showToast(getString(R.string.general_error_msg));
            }
        }
    };



//  setting up google client
    private synchronized void setUpGClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                    .addConnectionCallbacks(MainActivity.this)
                    .addOnConnectionFailedListener(MainActivity.this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            googleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            if (mGoogleMap != null) {
                //menu
//                getMyLocation();
                setupMenu(mGoogleMap);
                MapsInitializer.initialize(MainActivity.this);
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mGoogleMap.setBuildingsEnabled(true);
                        mGoogleMap.setTrafficEnabled(false);
                        mGoogleMap.setMyLocationEnabled(true);
                        if (sourceLatlng == null) {
                            getLastLocation();
                        } else {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(sourceLatlng)      // Sets the center of the map to location user
                                    .zoom(15)                   // Sets the zoom
                                    .bearing(120)                // Sets the orientation of the camera to east
                                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            mGoogleMap.moveCamera((CameraUpdateFactory.newCameraPosition(cameraPosition)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //to reposition the mylocation button by google
                    //https://gist.github.com/Manabu-GT/10962858
                    try{
                        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                        Log.d(LOG_TAG, "onMapReady: inside try>>>> "+locationButton);
                        // and next place it, on bottom right (as Google Maps app)
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                                locationButton.getLayoutParams();
                        // position on right bottom
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
                        layoutParams.setMargins(0, 0, 30, 30);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                } else {
                    checkAndRequestPermissions();
                }
            }
        }
    };


    private boolean checkAndRequestPermissions() {
        int permissionFineLocation = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarseLocation = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Params.REQUEST_PERMISSIONS_PICKUP);
            return false;
        }
        return true;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(checkAndRequestPermissions() && !Params.ManualLocationFlag){
            getLastLocation();
            startLocationUpdates();
        }if (sourceTV.getText().toString().isEmpty()
                || destinationTV.getText().toString().isEmpty()) {
            DirectionBox.setVisibility(View.GONE);
        }if (mGoogleMap != null) {
            setupMenu(mGoogleMap);
        }setUpGClient();
//        getMyLocation();
        mapFragment.getMapAsync(onMapReadyCallback);
    }


    LocationListener locationlistener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(LOG_TAG, "onLocationChanged: >>>>> " + location);
        }
    };

    private synchronized void zoomSource() {
        try {
            if (mGoogleMap != null) {
                mGoogleMap.clear();
                Double sourceLat = sourceLatlng.latitude;
                Double sourceLong = sourceLatlng.longitude;
                location.setLatitude(sourceLat);
                location.setLongitude(sourceLong);
//                String address = Util.getAddressFromLocation(MainActivity.this, location);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(sourceLatlng)      // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .bearing(120)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(sourceLatlng)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.sourcemarker))
                        .title("Source")

                );
                Log.d("++CAMERA++", "onCameraMoveStarted:4 ");

            } else {
                Log.d("++CAMERA++", "null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Log.d(LOG_TAG, "onConnected: " + bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceSelected(Place place) {

    }

    @Override
    public void onError(Status status) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ReqCode", String.valueOf(requestCode));
        Log.d("ResultCode", String.valueOf(resultCode));
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST:
                if (resultCode == RESULT_OK) {
                    if (mGoogleMap != null) {
                        mGoogleMap.clear();
                    }
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.d(LOG_TAG, "Place: " + place.getName());
                    if (place != null) {
                        destinationTV.setText(place.getAddress().toString());
                        LatLng maual_loc = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                        Double latitude = maual_loc.latitude;
                        Double longitude = maual_loc.longitude;
                        destinationLatLng = new LatLng(latitude, longitude);
                        DatabaseHelper.setAccountDetails(MainActivity.this, Params.TO_LOCATION_LATITUDE, String.valueOf(latitude));
                        DatabaseHelper.setAccountDetails(MainActivity.this, Params.TO_LOCATION_LONGITUDE, String.valueOf(longitude));
                        DatabaseHelper.setAccountDetails(MainActivity.this, Params.TO_LOCATION_STR, place.getAddress().toString());
                        //Or Do whatever you want with your location
                        callGoogleAPI();
                    }
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(LOG_TAG, status.getStatusMessage());
                    showToast(getString(R.string.general_error_msg));
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;

            case PLACE_AUTOCOMPLETE_REQUEST_CODE_SRC:
                if (resultCode == RESULT_OK) {
                    Params.ManualLocationFlag = true;
                    if (mGoogleMap != null) {
                        mGoogleMap.clear();
                    }
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    Log.d(LOG_TAG, "Place: " + place.getName());
                    if (place != null) {
                        sourceTV.setText(place.getAddress().toString());
                        LatLng maual_loc = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                        Double latitude = maual_loc.latitude;
                        Double longitude = maual_loc.longitude;
                        sourceLatlng = new LatLng(latitude, longitude);
                        DatabaseHelper.setAccountDetails(MainActivity.this, Params.FROM_LOCATION_LATITUDE, String.valueOf(latitude));
                        DatabaseHelper.setAccountDetails(MainActivity.this, Params.FROM_LOCATION_LONGITUDE, String.valueOf(longitude));
                        DatabaseHelper.setAccountDetails(MainActivity.this, Params.FROM_LOCATION_STR, place.getAddress().toString());
                        zoomSource();
                        //Or Do whatever you want with your location
                        callGoogleAPI();
                    }
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
                    Log.i(LOG_TAG, status.getStatusMessage());
                    showToast(getString(R.string.general_error_msg));
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;


            default:
                Log.d("ReqCode", "onActivityResult: def");
                break;
        }
    }

    private void callGoogleAPI() {
        if (!sourceTV.getText().toString().isEmpty()
                && !destinationTV.getText().toString().isEmpty()) {
            try {
                Params.googleApiCalled = true;
                GetRouteTask getRouteTask = new GetRouteTask();
                getRouteTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showSnackbar(String msg, Context context) {
        View view = findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    private class GetRouteTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            String response = "false";
            String base_url = "https://maps.googleapis.com/";

//Retrofit 1.9.0 for network call reference
// * https://stackoverflow.com/questions/30163699/google-map-direction-api-using-retrofit

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(base_url)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            MyApiRequestInterface reqinterface = restAdapter.create(MyApiRequestInterface.class);

            try {
                reqinterface.getJson(
                        DatabaseHelper.getAccountDetails(MainActivity.this, Params.FROM_LOCATION_STR),
                        DatabaseHelper.getAccountDetails(MainActivity.this, Params.TO_LOCATION_STR),
                        "false",//sensor
                        "metric",//unit
                        "driving",//mode
                        "true",//for multiple routes
                        "AIzaSyBaOLU1XybZwa59q3bw2uNL8b0CZTzd-SE",//key
                        new Callback<DirectionResults>() {
                            @Override
                            public void success(DirectionResults directionResults, Response response) {
                                ArrayList<LatLng> routelist;
                                ArrayList<String> directionslist;
                                int routeCount = 0;
                                Log.d(LOG_TAG, "inside on success routes :" + directionResults.getRoutes().size());
                                if (directionResults.getRoutes().size() > 0) {
                                    ArrayList<LatLng> decodelist;
                                    directionsListArray = new DirectionsList[directionResults.getRoutes().size()];
                                    for (int j = 0; j < directionResults.getRoutes().size(); j++) {
                                        routeCount = j;
                                        routelist = new ArrayList<LatLng>();
                                        directionslist = new ArrayList<String>();
                                        directionsListArray[j] = new DirectionsList();
                                        Route routeA = directionResults.getRoutes().get(j);
                                        Log.i(LOG_TAG, "-----------------------------------------------------");
                                        Log.i(LOG_TAG, "Legs length : " + routeA.getLegs().size());
                                        if (routeA.getLegs().size() > 0) {
                                            List<Steps> steps = routeA.getLegs().get(0).getSteps();
                                            Log.i(LOG_TAG, "Steps size :" + steps.size());
                                            Steps step;
                                            com.satragni.navigationassignment.Model.Location location;
                                            String polyline;
                                            for (int i = 0; i < steps.size(); i++) {
                                                step = steps.get(i);
                                                location = step.getStart_location();
                                                String html_instructions = String.valueOf(Html.fromHtml(String.valueOf(Html.fromHtml(step.getHtml_instructions()))));
                                                Log.d(LOG_TAG, "success: " + html_instructions);
                                                directionslist.add(html_instructions);
                                                routelist.add(new LatLng(location.getLat(), location.getLng()));
                                                Log.d(LOG_TAG, "Start Location :" + location.getLat() + ", " + location.getLng());
                                                polyline = step.getPolyline().getPoints();
                                                decodelist = RouteDecode.decodePoly(polyline);
                                                routelist.addAll(decodelist);
                                                location = step.getEnd_location();
                                                routelist.add(new LatLng(location.getLat(), location.getLng()));
                                                Log.d(LOG_TAG, "End Location :" + location.getLat() + ", " + location.getLng());
                                            }
                                        }
                                        Log.d(LOG_TAG, "routelist size : " + routelist.size());
                                        if (routelist.size() > 0) {
                                            Log.d(LOG_TAG, "success: index " + routeCount);
                                            directionsListArray[j].setDirectionslist(directionslist);
                                            drawPoly(routelist, "success", routeCount, directionsListArray);
                                        } else {
                                            Log.d(LOG_TAG, "doInBackground: " + response
                                            );
                                        }
                                    }
                                    displayDirection(directionsListArray, routeCount);
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                showToast(error.toString());
                                Log.d(LOG_TAG, "failure: " + error.toString());
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
        }
    }


    //displays the route
    private void displayDirection(DirectionsList[] directionslistArray, int index) {
//        DirectionBox.setVisibility(View.VISIBLE);
        SweetSheet sweetSheet = new SweetSheet(mapRlayout);// define sweetsheet object
        new SweetView(MainActivity.this).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        listHeading.setText("Directions for route " + index);
        Log.d(LOG_TAG, "displayDirection: " + index);

        final ArrayList<MenuEntity> MenuEntityList = new ArrayList<>();
        MenuEntity[] menuEntities = new MenuEntity[directionslistArray[index].getDirectionslist().size() + 1];
//        MenuEntityList.clear();
        menuEntities[0] = new MenuEntity();
        menuEntities[0].iconId = R.mipmap.ic_route2;
        menuEntities[0].title = "Directions for route " + index;
        Log.d(LOG_TAG, "displayDirection: " + index);
        menuEntities[0].titleSize = 20;
        menuEntities[0].titleColor = getResources().getColor(R.color.colorPrimaryDark);
        MenuEntityList.add(0, menuEntities[0]);
        for (int i = 1; i < directionslistArray[index].getDirectionslist().size(); i++) {
            menuEntities[i] = new MenuEntity();
            menuEntities[i].iconId = R.mipmap.ic_directions;
            menuEntities[i].titleColor = getResources().getColor(R.color.colorblack);
            menuEntities[i].titleSize = 12;
            menuEntities[i].title = directionslistArray[index].getDirectionslist().get(i);
            MenuEntityList.add(i, menuEntities[i]);
        }
        sweetSheet.setMenuList(MenuEntityList);
        sweetSheet.setDelegate(new RecyclerViewDelegate(true));
        sweetSheet.setBackgroundEffect(new BlurEffect(8));
        ((RecyclerViewDelegate)sweetSheet.getDelegate()).notifyDataSetChanged();
        sweetSheet.show();

        final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_list_item_1, directionslistArray[index].getDirectionslist());
        directionListView.setAdapter(adapter);
    }


    //draw the polyline
    public void drawPoly(ArrayList<LatLng> routelists, String result, int index, final DirectionsList[] directionslistArray) {
        if (result.equalsIgnoreCase("success") && mGoogleMap != null) {
            PolylineOptions rectLine = new PolylineOptions().width(10).color(getRandColor());
            for (int i = 0; i < routelists.size(); i++) {
                rectLine.add(routelists.get(i));
            }
            try {
                if (sourceLatlng != null && destinationLatLng != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(sourceLatlng);
                    builder.include(destinationLatLng);

                    LatLngBounds bounds = builder.build();

                    int padding = 150; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                    mGoogleMap.animateCamera(cu);
                    Log.d("++CAMERA++", "onCameraMoveStarted:3 ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("++CAMERA++", "onCameraMoveStarted: exception ");
            }


            // Adding route on the map
            mGoogleMap.addPolyline(rectLine);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(
                    Double.parseDouble(DatabaseHelper.getAccountDetails(MainActivity.this, TO_LOCATION_LATITUDE)),
                    Double.parseDouble(DatabaseHelper.getAccountDetails(MainActivity.this, TO_LOCATION_LONGITUDE))))
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.destinationmarker))

            ;

            Random r = new Random();
            int rand = r.nextInt(routelists.size() - 1) + 1;

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(routelists.get(rand).latitude, routelists.get(rand).longitude))
                    .title("Route " + String.valueOf(index))
                    .snippet("Tap marker for directions")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_route2))
            );

            //for the route click
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.d(LOG_TAG, "onMarkerClick: " + marker.getTitle());
                    if (marker != null) {
                        if (!marker.getTitle().equalsIgnoreCase("Destination")
                                &&
                                !marker.getTitle().equalsIgnoreCase("Source")) {
                            //to identify which route
                            String Title = marker.getTitle();
                            Title = Title.replace("Route ", "");
                            int i = Integer.parseInt(Title);
                            displayDirection(directionslistArray, i);
                        }
                    }
                    return false;
                }
            });
            mGoogleMap.addMarker(markerOptions);
//            rectLine.clickable(true);
        } else {
            Log.d(LOG_TAG, "onPostExecute: " + mGoogleMap + " " + routelists);
        }


    }


    //generate random colors
    private int getRandColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }


    private void setupMenu(GoogleMap googleMap) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MenuListFragment menuListFragment = (MenuListFragment) fragmentManager.findFragmentById(R.id.id_container_menu);
        if (menuListFragment == null) {
            menuListFragment = new MenuListFragment();
            menuListFragment.setGoogleMap(googleMap);
            fragmentManager.beginTransaction().add(R.id.id_container_menu, menuListFragment).commit();
        }
    }




//https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API

    protected void startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // do work here
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());
        }
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        mylocation=location;
        if(!Params.ManualLocationFlag) {
            sourceTV.setText(Util.getAddressFromLocation(MainActivity.this, location).replace("null",""));

            DatabaseHelper.setAccountDetails(MainActivity.this, Params.FROM_LOCATION_LATITUDE, String.valueOf(location.getLatitude()));
            DatabaseHelper.setAccountDetails(MainActivity.this, Params.FROM_LOCATION_LONGITUDE, String.valueOf(location.getLongitude()));
            DatabaseHelper.setAccountDetails(MainActivity.this, Params.FROM_LOCATION_STR, Util.getAddressFromLocation(MainActivity.this, location).replace("null", ""));
            if(Params.googleApiCalled==false) {
                zoomSource();
            }
            // You can now create a LatLng Object for use with maps
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            sourceLatlng = latLng;
        }



    }


    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(MainActivity.this);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MapDemoActivity", "Error trying to get last GPS location");
                            e.printStackTrace();
                        }
                    });
        }else{
            Log.d(LOG_TAG, "getLastLocation: NO PERM");
        }
    }
}
