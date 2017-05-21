package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import FindCellTowerApiOperation.CellTowerLocManager;

public class Main extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {
    private CellTowerInfoManager ctiMan ;
    private Geocoder geoCoder ;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    Button bRefresh ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bRefresh = (Button) findViewById(R.id.refresh);
        bRefresh.setOnClickListener(this) ;
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Google Services are Available!", Toast.LENGTH_LONG).show();
            ctiMan = new CellTowerInfoManager((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE));
            geoCoder= new Geocoder(this);
            initMap();
        } else {
            //No Google Maps Layout
        }


        /*
        if (CellTowerLocManager.getInstance().getCellTowerLocation().isStatusOK())
        {
            double lat = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLat()) ;
            double lon = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLon()) ;

            Toast.makeText(this, "lat:"+lat, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "lon:"+lon, Toast.LENGTH_LONG).show();
        }
        */
    }

    private boolean refreshIsBusy  = false;
    private void refresh()
    {
        if (!refreshIsBusy) {
            refreshIsBusy = true ;
            ctiMan.reload();
            CellTowerLocManager.getInstance().loadCellTowerLocation(ctiMan.getMcc(),ctiMan.getMnc(),ctiMan.getCellLac(),ctiMan.getCellId());
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    double lat = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLat());
                    double lon = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLon());
                    //Toast.makeText(this, "Lat : "+lat + " Lon : " +lon, Toast.LENGTH_LONG).show();

                    String locality = "";
                    try {
                        locality = geoCoder.getFromLocation(lat, lon, 1).get(0).getLocality();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    setMarker(locality, lat, lon);
                    refreshIsBusy = false ;
                }
            }, 3-+000);

        }
        else
            Toast.makeText(this, "Refresh operation is busy", Toast.LENGTH_LONG).show();
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }


    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //goToLocationZoom(38.003117, 23.677586, 15);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    Marker marker ;
    private void setMarker(String locality,double lat, double lng)
    {
        if (marker != null)
            marker.remove() ;

        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .position(new LatLng(lat,lng))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.celltower))
                .snippet("Cell Tower is here!") ;

        marker = mGoogleMap.addMarker(options) ;
    }

    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
        ;
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);

    }
    /*
    public void geoLocate(View view)
    {
        //EditText et = (EditText) findViewById(R.id.editText) ;

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location==null){
            Toast.makeText(this, "Cant get current location!!", Toast.LENGTH_LONG).show();
        }
        else
        {
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,15) ;
            mGoogleMap.animateCamera(update);
        }
    }

    @Override
    public void onClick(View v) {
        if (v==bRefresh)
        {
            refresh();
        }
    }
}
