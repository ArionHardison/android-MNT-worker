package com.dietmanager.chef.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.chef.R;
import com.dietmanager.chef.adapter.GroceryIngredientAdapter;
import com.dietmanager.chef.api.APIError;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.api.ErrorUtils;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.DataParser;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.Ordertiming;
import com.dietmanager.chef.model.Profile;
import com.dietmanager.chef.model.orderrequest.OrderRequestItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderFlowActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    boolean mapClicked = true;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng srcLatLong;
    private LatLng destLatLong;

    @BindView(R.id.icon_started_towards_restaurant)
    ImageView iconStartedTowardsRestaurant;
    @BindView(R.id.icon_reached_restaurant)
    ImageView iconReachedRestaurant;
    @BindView(R.id.icon_order_picked_up)
    ImageView iconOrderPickedUp;
    @BindView(R.id.icon_order_delivered)
    ImageView iconOrderDelivered;
    @BindView(R.id.icon_payment_received)
    ImageView iconPaymentReceived;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.update_status_btn)
    Button updateStatusBtn;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvFoodName)
    TextView tvFoodName;
    @BindView(R.id.tvIngredients)
    TextView tvIngredients;
    @BindView(R.id.tvDate)
    TextView tvDate;

    FusedLocationProviderClient mFusedLocationClient;
    @BindView(R.id.back)
    ImageView backArrow;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    Context context;
    CustomDialog customDialog;
    private OrderRequestItem userRequestItem = null;

    private Marker sourceMarker;
    private Marker destinationMarker;
    ScheduledExecutorService scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        setContentView(R.layout.activity_order_flow);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userRequestItem = (OrderRequestItem) bundle.getSerializable("userRequestItem");
            tvName.setText(userRequestItem.getUser().getName());
            if(userRequestItem.getCustomerAddress().getMapAddress()!=null)
                tvAddress.setText(userRequestItem.getCustomerAddress().getMapAddress());
            tvFoodName.setText(userRequestItem.getFood().getName());
            StringBuilder sb = new StringBuilder();
            boolean foundOne = false;

            for (int i = 0; i < userRequestItem.getOrderingredient().size(); ++i) {
                if (foundOne) {
                    sb.append(", ");
                }

                foundOne = true;
                sb.append(userRequestItem.getOrderingredient().get(i).getFoodingredient().getIngredient().getName());
            }
            tvIngredients.setText(sb.toString());
            tvDate.setText(userRequestItem.getCreatedAt());
            initFlowIcons();
        }
        context = OrderFlowActivity.this;
        customDialog = new CustomDialog(context);
        //Load animation
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    String nextStatus="";
    private void initFlowIcons() {
        if (userRequestItem != null) {
            nextStatus = GlobalData.getNextOrderStatus(userRequestItem.getStatus());
                if (userRequestItem.getStatus().equalsIgnoreCase("ASSIGNED")) {
                    iconStartedTowardsRestaurant.setBackgroundResource(R.drawable.round_accent);
                    iconStartedTowardsRestaurant.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    iconReachedRestaurant.setBackgroundResource(R.drawable.round_grey);
                    iconReachedRestaurant.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    updateStatusBtn.setText("Started toward location");
                }
                if (userRequestItem.getStatus().equalsIgnoreCase("PICKEDUP")) {
                    iconReachedRestaurant.setBackgroundResource(R.drawable.round_accent);
                    iconReachedRestaurant.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    iconOrderPickedUp.setBackgroundResource(R.drawable.round_grey);
                    iconOrderPickedUp.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    updateStatusBtn.setText("Reached location");
                }
                if (userRequestItem.getStatus().equalsIgnoreCase("ARRIVED")) {
                    iconOrderPickedUp.setBackgroundResource(R.drawable.round_accent);
                    iconOrderPickedUp.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    iconOrderDelivered.setBackgroundResource(R.drawable.round_grey);
                    iconOrderDelivered.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    updateStatusBtn.setText("Food preparation in progress");
                }
                if (userRequestItem.getStatus().equalsIgnoreCase("PROCESSING")) {
                    iconOrderDelivered.setBackgroundResource(R.drawable.round_accent);
                    iconOrderDelivered.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    iconPaymentReceived.setBackgroundResource(R.drawable.round_grey);
                    iconPaymentReceived.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    updateStatusBtn.setText("Food prepared");
                }
                if (userRequestItem.getStatus().equalsIgnoreCase("PREPARED")) {
                    iconPaymentReceived.setBackgroundResource(R.drawable.round_accent);
                    iconPaymentReceived.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    updateStatusBtn.setText("Waiting for user approval");
                    waitingForUserPopup();
                    scheduler.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            getOrderById(userRequestItem.getId());
                        }
                    }, 0, 5, TimeUnit.SECONDS);
                }
                if (userRequestItem.getStatus().equalsIgnoreCase("COMPLETED")) {
                    iconPaymentReceived.setBackgroundResource(R.drawable.round_accent);
                    iconPaymentReceived.setColorFilter(ContextCompat.getColor(OrderFlowActivity.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    updateStatusBtn.setVisibility(View.GONE);
                }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        scheduler.shutdown();
    }

    private void getOrderById(int id) {

            String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this, "access_token");
            System.out.println("getProfile Header " + header);
            Call<OrderRequestItem> call = GlobalData.api.getOrderDetailById(header, id);
            call.enqueue(new Callback<OrderRequestItem>() {
                @Override
                public void onResponse(@NonNull Call<OrderRequestItem> call, @NonNull Response<OrderRequestItem> response) {
                    if (response.isSuccessful()) {
                        GlobalData.selectedOrder = response.body();
                        if(response.body().getStatus().equalsIgnoreCase("COMPLETED"))
                        {
                            userApprovedPopup();
                            scheduler.shutdown();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OrderRequestItem> call, @NonNull Throwable t) {
                    Toast.makeText(OrderFlowActivity.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
                }
            });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            if (!success) {
                Log.i("Map:Style", "Style parsing failed.");
            } else {
                Log.i("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            Log.i("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        if (mMap != null) {
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setOnCameraMoveListener(this);
            mMap.setOnCameraIdleListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    srcLatLong=new LatLng(location.getLatitude(),location.getLongitude());
                                    String url = getUrl(location.getLatitude(), location.getLongitude()
                                            , userRequestItem.getCustomerAddress().getLatitude(), userRequestItem.getCustomerAddress().getLongitude());
                                    FetchUrl fetchUrl = new FetchUrl();
                                    fetchUrl.execute(url);
                                }
                            }
                        });
            } else {
                //Request Location Permission
            }
        } else {
            buildGoogleApiClient();
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                srcLatLong=new LatLng(location.getLatitude(),location.getLongitude());
                                String url = getUrl(location.getLatitude(), location.getLongitude()
                                        , userRequestItem.getCustomerAddress().getLatitude(), userRequestItem.getCustomerAddress().getLongitude());
                                FetchUrl fetchUrl = new FetchUrl();
                                fetchUrl.execute(url);
                            }
                        }
                    });
        }

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
//                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private String getUrl(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude) {
        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    @Override
    public void onLocationChanged(Location location) {
        srcLatLong = new LatLng(location.getLatitude(), location.getLongitude());
       /* String url = getUrl(location.getLatitude(), location.getLongitude()
                , userRequestItem.getCustomerAddress().getLatitude(), userRequestItem.getCustomerAddress().getLongitude());
        FetchUrl fetchUrl = new FetchUrl();
        fetchUrl.execute(url);*/
    }

    @Override
    public void onCameraIdle() {
        try {
            CameraPosition cameraPosition = mMap.getCameraPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {
        mapClicked = true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }


        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            if (result != null) {
                // Traversing through all the routes
                if (result.size() > 0) {
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        }
                        if (userRequestItem.getCustomerAddress().getLatitude() != 0.0) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(srcLatLong).title("Source").draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_marker));
                            sourceMarker = mMap.addMarker(markerOptions);

                            destLatLong = new LatLng(userRequestItem.getCustomerAddress().getLatitude(), userRequestItem.getCustomerAddress().getLongitude());
                            if (destinationMarker != null)
                                destinationMarker.remove();
                            MarkerOptions destMarker = new MarkerOptions()
                                    .position(destLatLong).title("Destination").draggable(true)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker));
                            destinationMarker = mMap.addMarker(destMarker);
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(sourceMarker.getPosition());
                            builder.include(destinationMarker.getPosition());
                            LatLngBounds bounds = builder.build();
                            final int width = getResources().getDisplayMetrics().widthPixels;
                            final int height = getResources().getDisplayMetrics().heightPixels;
                            final int padding = (int) (width * 0.20); // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 120);
                            mMap.moveCamera(cu);
                            // Adding all the points in the route to LineOptions
                            lineOptions.addAll(points);
                            lineOptions.width(5);
                            lineOptions.color(Color.BLACK);
                            Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                        }
                    }
                } else {
                    mMap.clear();
                }
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @OnClick({R.id.back,R.id.update_status_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.update_status_btn:
                if(!userRequestItem.getStatus().equalsIgnoreCase("PROCESSING")) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("_method", "PATCH");
                    map.put("status", nextStatus);
                    updateOrder(userRequestItem.getId(), map);
                }
                else {
                    preparedAlert();
                }
                break;
        }
    }


    ImageView imgPrepared;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0;

    public void preparedAlert() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderFlowActivity.this);
            final FrameLayout frameView = new FrameLayout(OrderFlowActivity.this);
            builder.setView(frameView);
            final AlertDialog purchasedDialog = builder.create();
            purchasedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            purchasedDialog.setCancelable(true);
            LayoutInflater inflater = purchasedDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.food_prepared_popop, frameView);
            Button upload_btn = dialogView.findViewById(R.id.upload_btn);

            imgPrepared=dialogView.findViewById(R.id.imgPrepared);
            dialogView.findViewById(R.id.imgPrepared).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(OrderFlowActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(OrderFlowActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            goToImageIntent();
                        } else {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                        }
                    } else {
                        goToImageIntent();
                    }
                }
            });
            upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imgFile==null){
                        Toast.makeText(OrderFlowActivity.this, getResources().getString(R.string.please_upload_image), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        purchasedDialog.dismiss();
                        HashMap<String, RequestBody> map = new HashMap<>();
                        map.put("_method",RequestBody.create(MediaType.parse("text/plain"),"PATCH"));
                        map.put("status", RequestBody.create(MediaType.parse("text/plain"),nextStatus));
                        updateOrderWithImage(userRequestItem.getId(),map);
                    }
                }
            });
            purchasedDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitingForUserPopup() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderFlowActivity.this);
            final FrameLayout frameView = new FrameLayout(OrderFlowActivity.this);
            builder.setView(frameView);
            final AlertDialog purchasedDialog = builder.create();
            purchasedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            purchasedDialog.setCancelable(false);
            LayoutInflater inflater = purchasedDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.waiting_for_user_approval_popop, frameView);
            purchasedDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userApprovedPopup() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderFlowActivity.this);
            final FrameLayout frameView = new FrameLayout(OrderFlowActivity.this);
            builder.setView(frameView);
            final AlertDialog purchasedDialog = builder.create();
            purchasedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            purchasedDialog.setCancelable(false);
            LayoutInflater inflater = purchasedDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.user_approved_popup, frameView);
            dialogView.findViewById(R.id.done_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(OrderFlowActivity.this,Home.class));
                    finishAffinity();
                }
            });
            purchasedDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateOrderWithImage(int id, HashMap<String, RequestBody> map) {
        customDialog.show();
        MultipartBody.Part filePart = null;

        if (imgFile != null)
            filePart = MultipartBody.Part.createFormData("image", imgFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), imgFile));

        String header = SharedHelper.getKey(OrderFlowActivity.this, "token_type") + " "
                + SharedHelper.getKey(OrderFlowActivity.this, "access_token");
        Call<OrderRequestItem> call = apiInterface.updateOrderWithImage(header,id,map,filePart);
        call.enqueue(new Callback<OrderRequestItem>() {
            @Override
            public void onResponse(@NonNull Call<OrderRequestItem> call, @NonNull Response<OrderRequestItem> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    userRequestItem.setStatus(response.body().getStatus());
                    nextStatus=GlobalData.getNextOrderStatus(userRequestItem.getStatus());
                    initFlowIcons();
                }

            }

            @Override
            public void onFailure(@NonNull Call<OrderRequestItem> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(OrderFlowActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    File imgFile;

    private int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            if(imgPrepared!=null) {
                Glide.with(this)
                        .load(imgDecodableString)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.man)
                                .error(R.drawable.man))
                        .into(imgPrepared);
//            imgFile = new File(imgDecodableString);
            }
            try {
                imgFile = new id.zelory.compressor.Compressor(OrderFlowActivity.this).compressToFile(new File(imgDecodableString));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == Activity.RESULT_CANCELED)
            Toast.makeText(this, getResources().getString(R.string.dont_pick_image),
                    Toast.LENGTH_SHORT).show();
    }
    public void goToImageIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    public void updateOrder(int id,HashMap<String, String> map) {
        customDialog.show();

        String header = SharedHelper.getKey(OrderFlowActivity.this, "token_type") + " "
                + SharedHelper.getKey(OrderFlowActivity.this, "access_token");
        Call<OrderRequestItem> call = apiInterface.updateOrder(header,id,map);
        call.enqueue(new Callback<OrderRequestItem>() {
            @Override
            public void onResponse(@NonNull Call<OrderRequestItem> call, @NonNull Response<OrderRequestItem> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    userRequestItem.setStatus(response.body().getStatus());
                    nextStatus=GlobalData.getNextOrderStatus(userRequestItem.getStatus());
                    initFlowIcons();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderRequestItem> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(OrderFlowActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                if (!jsonObj.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    ParserTask parserTask = new ParserTask();
                    // Invokes the thread for parsing the JSON data
                    parserTask.execute(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                Log.d("downloadUrl", data);
                br.close();
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }
}