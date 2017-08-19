package gps;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.src.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class Maps extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private TextView tvCoordinate;
    private Location location;
    private LocationRequest locationRequest;
    private Button btnShowLocation;
    private Button btnGetLocation;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps);

        tvCoordinate = (TextView) findViewById(R.id.TvCoordinate);
        btnShowLocation = (Button) findViewById(R.id.ButtonShowLocation);
        btnGetLocation = (Button) findViewById(R.id.ButtonGetLocation);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                boolean isOn = manager.isProviderEnabled( LocationManager.GPS_PROVIDER);
                if(isOn) {
                    callConnection();
                }else{
                    Toast.makeText(Maps.this, "Ative a Localizacao!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
			}
		});
        callConnection();
    }

    private synchronized void callConnection(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate(){
        initLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest,Maps.this);
    }
    private void stopLocationUpdate(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,Maps.this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

       location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(location != null){
            Log.i("LOG", "latitude: "+ location.getLatitude());
            Log.i("LOG", "longitude: "+ location.getLongitude());
            tvCoordinate.setText(location.getLatitude()+","+ location.getLongitude());
        }
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed("+connectionResult+")");
    }

    @Override
    public void onBackPressed(){
        Intent data = new Intent();
        data.putExtra("gps", tvCoordinate.getText());
        setResult(2,data);
        finish();
        stopLocationUpdate();
    }

    @Override
    public void onLocationChanged(Location location) {
        tvCoordinate.setText(location.getLatitude()+","+ location.getLongitude());
    }
}