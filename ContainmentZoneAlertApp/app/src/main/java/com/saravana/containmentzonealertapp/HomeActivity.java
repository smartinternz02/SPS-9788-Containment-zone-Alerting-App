package com.saravana.containmentzonealertapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.saravana.containmentzonealertapp.models.LocationResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 200;
    private static final String TAG = "TESTING";

    private static String jwtToken;
    private LocationRequest mLocationRequest;
    private static Integer ONE_MIN = 1000 * 60;
    private long UPDATE_INTERVAL = ONE_MIN*2;  /* 15 secs */
    private long FASTEST_INTERVAL = ONE_MIN; /* 5 secs */

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private FusedLocationProviderClient fusedLocationProviderClient;


    private ServiceConnection serviceConnection;
    private Disposable disposable;
    private LocationUpdateService myLocationUpdateService;
    private Button grantPermBtn;
    private TextView alertTextView;
    private TextView locTextView;
    SupportMapFragment mapFragment;
    private GoogleMap gMap;
    private Marker currentLocMarker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        grantPermBtn = findViewById(R.id.grantPermBtn);
        alertTextView = findViewById(R.id.alertTextView);
        locTextView = findViewById(R.id.locTextView);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = getFusedLocationProviderClient(this);
        setAtDangerZone(null,false);
        askPermissions();
        jwtToken = AuthUtils.getAuthorizationToken(HomeActivity.this);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("TESTING", "onServiceConnected: Connected");
                myLocationUpdateService= ((LocationUpdateService.MyLocationBinder)service).getService();
                disposable=myLocationUpdateService.observeLocation()
                        .observeOn(AndroidSchedulers.mainThread()) //downstream
                        .subscribe(
                                location -> {
                                    if(currentLocMarker!=null)
                                         currentLocMarker.remove();
                                    if(gMap!=null){
                                        LatLng myLatLng = new LatLng(location.getLat(),location.getLng());
                                        if(currentLocMarker==null)
                                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,10f));
                                        currentLocMarker = gMap.addMarker(new MarkerOptions().title("My Position")
                                                       .position(myLatLng).draggable(false));
                                    }
                                    String currentCZoneLoc = location.getName();
                                    Log.d("TESTING", "onServiceConnected: Add"+currentCZoneLoc);
                                    if(!currentCZoneLoc.equals(LocationUpdateService.SAFEZONE_FLAG)){
                                        setAtDangerZone(location.getName(),true);
                                    }else{
                                        setAtDangerZone(null,false);
                                    }
                                }

                        );
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("TESTING", "onServiceDisconnected: DisConnected");

            }
        };
        startLocationUpdates();
    }

    private void setAtDangerZone(String address,boolean isDanger){
        if(isDanger) {
            alertTextView.setText(getString(R.string.dangerzone_msg));
            alertTextView.setBackgroundColor(getColor(android.R.color.holo_red_light));
            locTextView.setVisibility(View.VISIBLE);
            locTextView.setText(address);
        }else{
            alertTextView.setText(getString(R.string.safezone_msg));
            alertTextView.setBackgroundColor(getColor(android.R.color.holo_green_light));
            locTextView.setVisibility(View.GONE);

        }
    }
    private void askPermissions() {
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                grantPermBtn.setVisibility(View.VISIBLE);

                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                return;
            }
        }
        grantPermBtn.setVisibility(View.GONE);

    }

    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: here ");
        grantPermBtn.setVisibility(View.GONE);
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> result =
                settingsClient.checkLocationSettings(locationSettingsRequest);
        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askPermissions();
                    return;
                }
                Log.d(TAG, "startLocationUpdates: goonna start ");
                Intent locServiceIntent = new Intent(
                        this, LocationUpdateService.class);
                ContextCompat.startForegroundService(this,locServiceIntent);
                bindService(locServiceIntent,
                        serviceConnection, BIND_AUTO_CREATE);
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;

                            resolvable.startResolutionForResult(
                                    HomeActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(HomeActivity.this,"you don't have the required features to run this app",Toast.LENGTH_SHORT)
                                 .show();
                        break;
                }
            }
        });

    }



    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        askPermissions();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;

    }




    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    (dialog, which) -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    });
                            return;
                        }
                    }

                }else{
                    startLocationUpdates();
                }

                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(HomeActivity.this,"The app needs all permissions to function correctly",Toast.LENGTH_LONG)
                                .show();
                        break;
                    default:
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        if(disposable != null)
            disposable.dispose();
        unbindService(serviceConnection);
        super.onDestroy();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(11.1271,78.6569),10f));
        Call<List<LocationResponse>> response =RetrofitClient.getInstance().getAPI().getAllCZones(jwtToken);

        response.enqueue(new Callback<List<LocationResponse>>() {
            @Override
            public void onResponse(Call<List<LocationResponse>> call, Response<List<LocationResponse>> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: Failed "+response.errorBody()+" :"+response.message()+" "+response.code());
                    return;
                }
                Log.d(TAG, "onResponse: Location Fetched");
                List<LocationResponse> cZoneAreasList = response.body();
                for(LocationResponse cZoneArea : cZoneAreasList){
                    LatLng latLng = new LatLng(cZoneArea.getLat(),cZoneArea.getLng());
                    gMap.addMarker(new MarkerOptions().position(latLng).title(cZoneArea.getName()).draggable(false));
                }
            }

            @Override
            public void onFailure(Call<List<LocationResponse>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                    if(AuthUtils.deleteAuthorizationToken(this)){
                        finish();
                        Toast.makeText(this,"Logged out successfully",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"Failed to logout",Toast.LENGTH_SHORT).show();
                    }
        }

        return super.onOptionsItemSelected(item);
    }
}

