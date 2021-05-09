package com.saravana.containmentzonealertapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.saravana.containmentzonealertapp.models.LocationResponse;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationUpdateService extends Service implements  ILocationService{
    private static final int ONGOING_NOTIFICATION_ID = 1234 ;
    private static final int ALERT_NOTIFICATION_ID = 1235;
    private static final String ACTION_STOP_SERVICE ="stop_location_service" ;
    public static String SAFEZONE_FLAG = "SAFEZONE";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static String jwtToken;
    private LocationRequest mLocationRequest;

    private static Integer ONE_MIN = 1000 * 60;
    private long UPDATE_INTERVAL = ONE_MIN*2;  /* 15 secs */
    private long FASTEST_INTERVAL = ONE_MIN; /* 5 secs */
    private static final int REQUEST_CHECK_SETTINGS = 200;
    private LocationCallback locationUpdateCallback;
    private NotificationManager notifManager;
    private ObservableEmitter<LocationResponse> locChangeEmitter;
    private Observable<LocationResponse> locChangeObservable;

    @Override
    public Observable<LocationResponse> observeLocation() {
        if(locChangeObservable == null) {
            locChangeObservable = Observable.create(emitter -> locChangeEmitter = emitter);
            locChangeObservable = locChangeObservable.share();
        }
        return locChangeObservable;
    }

    public class MyLocationBinder extends Binder{
       public LocationUpdateService getService(){
           return LocationUpdateService.this;
       }
    }
    private IBinder binder = new MyLocationBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TESTING", "Service created");
        fusedLocationProviderClient = getFusedLocationProviderClient(this);
        jwtToken = AuthUtils.getAuthorizationToken(this);
        notifManager= getSystemService(NotificationManager.class);
        locationUpdateCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        startLocationUpdates();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TESTING", "Service started");
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d("TESTING","called to cancel service");
            notifManager.cancel(ONGOING_NOTIFICATION_ID);
            stopForeground(true);
            stopSelf();
        }
        else {

            Intent stopSelf = new Intent(this, LocationUpdateService.class);
            stopSelf.setAction(this.ACTION_STOP_SERVICE);
            PendingIntent pStopSelf = PendingIntent.getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification notification = new NotificationCompat.Builder(this, App.LOC_CHANNEL_ID)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_message))
                    .setSmallIcon(R.drawable.ic_location_update)
                    .addAction(R.drawable.ic_stop, "Stop", pStopSelf)
                    .build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }

        return START_NOT_STICKY;
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        mLocationRequest = LocationRequest.create();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(1000f);


        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationUpdateCallback,
            Looper.myLooper());


    }
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("TESTING", "onLocationChanged: "+msg);

        updateDB(latLng.latitude,latLng.longitude);
    }
    private void updateDB(double lat,double lng){
        Log.d("TESTING", "updateDB: Auth header"+jwtToken);
        Call<Map<String, String>> locresponse = RetrofitClient.getInstance().getAPI().updateAndCheckIfInsideCZone(
                jwtToken,
                lat,lng);
        locresponse.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if(!response.isSuccessful()){
                    Log.d("TESTING", "onResponse: onResponse Failed");
                }
                String address = response.body().get("response");
                Log.d("TESTING", "onResponse: Success"+ address);
                LocationResponse cZoneArea = new LocationResponse();
                cZoneArea.setLat(lat);
                cZoneArea.setLng(lng);
                cZoneArea.setName(address);
                if(!address.equals(LocationUpdateService.SAFEZONE_FLAG)){
                    notifyAtDanger();
                }
                locChangeEmitter.onNext(cZoneArea);


            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.d("TESTING", "onResponse: onFailure "+t.getLocalizedMessage());

            }
        });
    }
    private void notifyAtDanger(){
        Notification alertNotification = new NotificationCompat.Builder(this,App.LOC_CHANNEL_ID)
                                     .setContentTitle("Danger Zone")
                                      .setContentText("You are at a Containmnet Zone")
                                     .setSmallIcon(R.drawable.ic_launcher_background)
                                      .build();

        notifManager.notify(ALERT_NOTIFICATION_ID,alertNotification);

    }
    @Override
    public void onDestroy() {
        Log.d("TESTING", "onDestroy: service destroyed");
        if(fusedLocationProviderClient!=null){
            fusedLocationProviderClient.removeLocationUpdates(locationUpdateCallback);
        }
    }
}
