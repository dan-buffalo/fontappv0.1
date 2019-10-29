package com.davide.fontappbeta;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Mappa pronta", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: La mappa è pronta");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            //TODO Aggiungere un sistema che legga il DB e aggiunga tutti i markers 27/10/19
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Fountain");

            //init();
        }
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private GeoPoint fountainGeoPoint;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private User utente;

    private Fountain mFountain;

    private Button goOn;
    private Button goBack;
    private SwitchCompat mASwitch;
    private ImageView imageView;

    private FirebaseFirestore mDB;

    private DatabaseReference databaseReference;

    private LocationManager locationManager;


    //widgets
    //private EditText mSearchText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        //mSearchText = findViewById(R.id.input_search);
        ImageButton moveToPosition = findViewById(R.id.imageButton);
        moveToPosition.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
        Button fountainButton = findViewById(R.id.fountainButton);

        fountainButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                addNewFountain();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();

        currentUser = mAuth.getCurrentUser();

        utente = (User) LoginActivity.getUser(currentUser);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    /*private void init() {
        Log.d(TAG, "init: Inizializzazione");
        mSearchText = findViewById(R.id.input_search);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    Log.d(TAG, "onEditorAction: Geolocalizzazione");
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: GEOLOCALIZZAZIONE");
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        Address address = null;
        List<Address> list = null;
        try {
            address = (Address) geocoder.getFromLocationName(searchString, 1);
            list.add(address);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: exception threw" + e.getMessage());
        }

        if (list.size() > 0) {
            address = list.get(0);
            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            LatLng addressLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(addressLatLng, DEFAULT_ZOOM));
            moveCamera(addressLatLng, DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }*/

    /**
     * Method that automatically find the fountain next to your position
     * @return fountain LatLng coordinates
     */
    /*@RequiresApi(api = Build.VERSION_CODES.M)
      public GeoPoint fountainPosition() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return fountainGeoPoint;
        }
        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: found location!");
                    Location currentLocation = (Location) location.getResult();
                    if (currentLocation != null) {
                        fountainGeoPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                        Log.d(TAG, "onComplete: FONTANELLA:" + fountainGeoPoint.toString());
                    }
                }
            }
        });

        return fountainGeoPoint;

    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            //GeoPoint currentGeoPoint = (GeoPoint) location.getResult();
                            Location currentLocation = (Location) location.getResult();
                            try {
                                if (currentLocation != null)
                                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                            "La mia posizione");
                            } catch (NullPointerException e) {
                                Log.d(TAG, "onComplete: Impossibile trovare la posizione");
                            }
                        }
                    }
                });
            } else {
                Log.d(TAG, "onSuccess: current location is null");
                Toast.makeText(MapActivity.this, "Impossibile trovare la posizione: controllare il GPS", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapActivity.DEFAULT_ZOOM));

        if (!title.equals("La mia posizione")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
    }

    private void addFountainMarker(Fountain fountain){
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(fountain.getGeo_point().getLatitude(), fountain.getGeo_point().getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(options);
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addNewFountain() { //TODO: GOTO InsertActivity
        setContentView(R.layout.activity_insert);

        goOn = findViewById(R.id.goOn);
        goBack = findViewById(R.id.goBack);
        mASwitch = findViewById(R.id.switchMyPosition);
        imageView = findViewById(R.id.fountainImage);


        //OnClick upload image, then set the uploaded image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //if switch is checked, get position of the fountain
        mASwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mASwitch.isChecked()) {
                    //mFountain = new Fountain(fountainPosition());
                    GeoPoint geoPoint = getGeoPoint();
                    MarkerOptions options = new MarkerOptions().position(
                            new LatLng(geoPoint.getLatitude(),
                                    geoPoint.getLongitude()));
                    mMap.addMarker(options);
                    mFountain = new Fountain(geoPoint);
                }
            }
        });
        //button goOn OnClick
        goOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mASwitch.isChecked()) {
                    mDB.collection("fountain")
                            .document("QjELTHoKdrbFQMIk8PnF")
                            .set(mFountain).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           //controllo per verificare se la fontana esiste già TODO Verifica se la fontana è già presente nel DB
                            //if(){}
                            Log.d(TAG, "onComplete: FONTANA AGGIUNTA: " + mFountain.toString());
                            Toast.makeText(getApplicationContext(), "Fontana aggiunta con successo", Toast.LENGTH_SHORT).show();
                            //aggiungo un marker sulla mappa
                            addFountainMarker(mFountain);
                            startActivity(new Intent(MapActivity.this, MapActivity.class));
                        }
                    });


                    //mDB.collection("fountain")
                    //.add(mFountain)
                            /*.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Log.d(TAG, "onComplete: FONTANA AGGIUNTA: " + mFountain.toString());
                                    Toast.makeText(getApplicationContext(), "Fontana aggiunta con successo", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: ERRORE NELL'AGGIUNGERE LA FONTANAA");
                        }
                    });*/
                } else {
                    mASwitch.setError("Required.");
                    return;
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, MapActivity.class));
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            Toast.makeText(getApplicationContext(), "Hai cliccato Contatti", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.userStats) {
            Toast.makeText(getApplicationContext(), "Nome utente: " + utente.getEmail().toString(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private GeoPoint getGeoPoint() {
        GeoPoint geoPoint = new GeoPoint(90,180);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return geoPoint;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null){
            geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        }
        else {
            Log.d(TAG, "getGeoPoint: error");
        }

        return geoPoint;

    }



    }

