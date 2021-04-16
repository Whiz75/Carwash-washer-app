package com.example.carwasher.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.example.carwasher.R;
import com.example.carwasher.models.CompletedReqModel;
import com.example.carwasher.models.InterestModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private android.widget.Toolbar toolbar;

    /*----location textInput layout----*/
    TextInputLayout fromLocation, toLocation;

    private MaterialButton completedButton;

    private CardView cv;
    private GoogleMap mGoogleMap;
    private DatabaseReference completedRef;
    private DatabaseReference interested;

    private FirebaseFirestore firebaseFirestore;

    private final int LOCATION_PERMISSION_CODE = 1;
    public static final int REQUEST_CHECK_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapActivity.this, "You have already granted this permission!", Toast.LENGTH_SHORT).show();

            SupportMapFragment supportMapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
            assert supportMapFragment != null;
            supportMapFragment.getMapAsync(this);
        } else {
            requestStoragePermission();
        }

        /*---assign firebase objects---*/
        completedRef = FirebaseDatabase.getInstance().getReference("CarWash").child("Completed");
        /*-------call method-------*/
        init();
        setToolbar();
        getCurrentLocation();
        GPSControl();
        animateBottomSheet();
        placeBid();
    }

    private void init() {
        toolbar = findViewById(R.id.mapToolbar);
        toLocation = findViewById(R.id.toDestination);
        fromLocation = findViewById(R.id.fromDestination);
        cv = findViewById(R.id.bottomsheet_cv);

        completedButton = findViewById(R.id.completed_button);
    }

    private void setToolbar() {
        toolbar.setTitle("DESTINATION");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ad = new AlertDialog.Builder(MapActivity.this);
                ad.setMessage("Are you sure you want to cancel? Going back will cancel this confirmation.");
                ad.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try
                        {
                            Intent intent = new Intent(getApplicationContext(), RequestsActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });
    }

    private void animateBottomSheet()
    {
        float v = 0;
        int y = 600;

        cv.setTranslationY(y);
        cv.setAlpha(v);

        cv.animate().translationY(0).alpha(1).setDuration(3000).setStartDelay(800).start();
        //cv.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(2000).start();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MapActivity.this,
                                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private  void getCurrentLocation()
    {
        try
        {
            /*--------cooordinates for current location--------*/
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,15, (LocationListener) MapActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onMyLocationChange(Location location)
            {
                LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latlng);

                markerOptions.title("My Location");
                mGoogleMap.clear();

                CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latlng, 17);
                mGoogleMap.animateCamera(cameraUpdate);
                mGoogleMap.addMarker(markerOptions);

                try
                {
                    /*--------------geocoder to get the current address--------------*/
                    Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    final String address = addresses.get(0).getAddressLine(0);
                    /*-------------- set the map location and zoom --------------*/
                    mGoogleMap.setMyLocationEnabled(true);
                    mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

                    /*--------------display the current location address on the TextInputLayout--------------*/
                    fromLocation.getEditText().setText(address);

                    Intent intent = getIntent();
                    String passed_address = intent.getStringExtra("addressKey");
                    String passed_lat = intent.getStringExtra("requestLat");
                    String passed_lon = intent.getStringExtra("requestLon");
                    Objects.requireNonNull(toLocation.getEditText()).setText(passed_address);

                    /*--- get the current location's coordinates and display them ---*/
                    //String coordinates = "Lat:" + location.getLatitude() + ", Lon:" + location.getLongitude();
                    double lat = Double.parseDouble(passed_lat);
                    double lon = Double.parseDouble(passed_lon);

                    /*--- call method to calculate the distance ---*/
                    calculateDistance(lat, lon, location.getLatitude(), location.getLongitude());
                    //GetDistanceFromCurrentPosition(lat, lon,location.getLatitude(),location.getLongitude());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void GPSControl()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> results = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        results.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try
                {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(getApplicationContext(),"GPS location is turned On",Toast.LENGTH_LONG).show();
                    //setLocationDialog();

                } catch (ApiException e) {
                    switch (e.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :
                            try
                            {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(MapActivity.this, REQUEST_CHECK_CODE);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE :
                            break;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void placeBid()
    {
        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setCompleteRequest();
                setupdialog();
            }
        });
    }

    private void  setCompleteRequest(String price)
    {
        /*---success dialog----*/
        final SweetAlertDialog dialog =  new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        dialog.setTitleText("SUCCESSFUL")
        .setContentText("Request was completed. Thank you!")
        .show();

        final SweetAlertDialog failedDialog =  new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);

        Intent intent = getIntent();
        String key = intent.getStringExtra("requesterKey");
        String profile = intent.getStringExtra("requestProfile");
        //String address = intent.getStringExtra("requestsAddress");
        @SuppressLint("SimpleDateFormat")
        String datenow = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CompletedReqModel model1 = new CompletedReqModel();
        model1.setRequester(key);
        model1.setDate(datenow);
        model1.setProfile(profile);
        model1.setPrice(price);

        /*---declare thread variable---*/
        try
        {
            completedRef.child(uid).push().setValue(model1)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            /*---display the success dialog---*/
                            dialog.show();
                            /*---start to run thread for 3 seconds---*/
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try
                                    {
                                        Thread.sleep(3000);
                                        dialog.dismiss();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    /*---show error dialog if failed to load to database---*/
                    failedDialog.setTitleText("Failed")
                            .setContentText(e.getMessage())
                            .show();
                    /*---dismiss the dialog after 3 secs---*/
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try
                            {
                                Thread.sleep(3000);
                                failedDialog.dismiss();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupdialog()
    {
        /*--------test test test test dialog------*/
        final Dialog dialog = new Dialog(MapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bid_view);
        dialog.setCancelable(true);

        /*---------------set the dialog height and width---------------*/
        WindowManager.LayoutParams layout = new WindowManager.LayoutParams();
        layout.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layout.width = WindowManager.LayoutParams.MATCH_PARENT;
        layout.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layout.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(layout);

        /*-------bind the dialog components---------*/
        MaterialButton bidButton = dialog.findViewById(R.id.place_bid_button);
        final TextInputLayout placeBidTextLayout = dialog.findViewById(R.id.place_bid_tl);

        interested = FirebaseDatabase.getInstance().getReference("CarWash").child("Interested");
        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String bidInput = placeBidTextLayout.getEditText().getText().toString();

                //setCompleteRequest(bidInput);
                sendBid(bidInput);
                dialog.dismiss();
                //Toast.makeText(getApplicationContext(),bidInput, Toast.LENGTH_LONG).show();
                Snackbar.make(v,"you have placed a bid successfully...", Snackbar.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

    private void setLocationDialog()
    {
        /*--------------open the enter location dialog--------------*/
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_location_dialog);

        /*---------------set the dialog height and width---------------*/
        WindowManager.LayoutParams layout = new WindowManager.LayoutParams();
        layout.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layout.width = WindowManager.LayoutParams.MATCH_PARENT;
        layout.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layout.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(layout);

        /*--------------dialog components--------------*/
        Button setAutomaticalButton = dialog.findViewById(R.id.set_automatically_button);
        Button setManualButton = dialog.findViewById(R.id.set_manually_button);

        setAutomaticalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSControl();
                dialog.dismiss();
            }
        });

        setManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_CODE)
        {
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    Toast.makeText(getApplicationContext(),"GPS is ON already", Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(), "GPS is required for this service!", Toast.LENGTH_LONG).show();
                    setLocationDialog();
                    break;
            }
        }
    }

    private void calculateDistance(double destination_lat, double destination_lon, double origin_lat, double origin_lon)
    {
        double distance;
        Location locationA = new Location("");
        locationA.setLatitude(origin_lat);
        locationA.setLongitude(origin_lon);
        Location locationB = new Location("");
        locationB.setLatitude(destination_lat);
        locationB.setLongitude(destination_lon);
        distance = locationA.distanceTo(locationB)/1000;
        //kmeter.setText(String.valueOf(distance));
        Toast.makeText(getApplicationContext(), String.valueOf(distance), Toast.LENGTH_SHORT).show();
    }

    //to be tested
    private void GetDistanceFromCurrentPosition(double lat1,double lng1, double lat2, double lng2)
    {
        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        int meterConversion = 1609;

        //Float(dist * meterConversion).floatValue();
        double distance = dist*meterConversion;

        Toast.makeText(getApplicationContext(),String.valueOf(distance),Toast.LENGTH_LONG ).show();

    }





    /*--- MIGRATE TO FIRE STORE1 ---*/
    private void sendBid(final String txt_price)
    {
        /*--- initialize firestore ---*/
        firebaseFirestore = FirebaseFirestore.getInstance();

        /*Intent i = getIntent();
        String reqKey = i.getStringExtra("requestkey");*/

        final HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("price", txt_price);
        // Set "Price" field of the request
        FirebaseFirestore
                .getInstance()
                .collection("Requests")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {

                        HashMap<String, Object> data = new HashMap<String, Object>();
                        data.put("id", documentReference.getId());

                        FirebaseFirestore.getInstance()
                                .collection("Requests")
                                .document(documentReference.getId())
                                .update(data);
                    }
                });

    }
}
