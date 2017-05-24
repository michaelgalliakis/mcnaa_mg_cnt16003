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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.text.DateFormat;
import java.util.Date;

import FindCellTowerApiOperation.CellTowerLocManager;

public class Main extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {
    private CellTowerInfoManager ctiMan ;
    private Geocoder geoCoder ;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;

    private ImageButton bRefresh ;
    private ImageButton bInfo ;
    private ImageButton bZoomCellTower ;
    private ImageButton bZoomGPS ;
    private ImageButton bTEI ;
    private TextView tStatus1 ;
    private TextView tStatus2 ;
    private ProgressBar pbCheckbar ;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            refresh() ;
            timerHandler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bRefresh = (ImageButton) findViewById(R.id.refresh);
        bInfo = (ImageButton) findViewById(R.id.info);
        bZoomCellTower = (ImageButton) findViewById(R.id.zoomcelltower);
        bZoomGPS = (ImageButton) findViewById(R.id.zoomgps);
        bZoomGPS.setTag("Enable");
        bTEI = (ImageButton) findViewById(R.id.tei);
        tStatus1 = (TextView) findViewById(R.id.status1);
        tStatus2 = (TextView) findViewById(R.id.status2);
        pbCheckbar = (ProgressBar) findViewById(R.id.checkbar);
        pbCheckbar.setProgress(10) ;
        bRefresh.setOnClickListener(this) ;
        bZoomGPS.setOnClickListener(this) ;
        bZoomCellTower.setOnClickListener(this) ;
        bTEI.setOnClickListener(this) ;
        tStatus1.setText("") ;
        tStatus2.setText("") ;
        if (googleServicesAvailable()) {
            //Toast.makeText(this, "Google Services are Available!", Toast.LENGTH_LONG).show();
            ctiMan = new CellTowerInfoManager((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE));
            geoCoder= new Geocoder(this);
            initMap();
            timerHandler.postDelayed(timerRunnable, 0);
        } else {
            //No Google Maps Layout
        }
    }

    private void zoomOperation(boolean isGPSzoom)
    {
        if (isGPSzoom)
        {
            bZoomGPS.setTag("Enable");
            bZoomGPS.setImageResource(R.mipmap.zoomgps);
            bZoomCellTower.setImageResource(R.mipmap.zoomcelltowerdis);
        }
        else
        {
            bZoomGPS.setTag("Disable");
            bZoomGPS.setImageResource(R.mipmap.zoomgpsdis);
            bZoomCellTower.setImageResource(R.mipmap.zoomcelltower);
        }
    }

    private boolean refreshIsBusy  = false;
    private void refresh()
    {
        tStatus2.setText("Last attempt to refresh ["+ DateFormat.getDateTimeInstance().format(new Date())+"]");
        if (!refreshIsBusy) {
            if (ctiMan.reload())
            {
                refreshIsBusy = true ;
                tStatus1.setText("Trying to find a Cell Tower...") ;
                pbCheckbar.setVisibility(View.VISIBLE);
                CellTowerLocManager.getInstance().loadCellTowerLocation(ctiMan.getMcc(),ctiMan.getMnc(),ctiMan.getCellLac(),ctiMan.getCellId());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        double lat = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLat());
                        double lon = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLon());
                        String locality = "";
                        try {
                            locality = geoCoder.getFromLocation(lat, lon, 1).get(0).getLocality();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        setMarker(locality, lat, lon);
                        tStatus1.setText("Tower[CellID="+ctiMan.getCellId()+"][CellLac="+ctiMan.getCellLac()+"] \n"+
                                "(MMC,MNC="+ctiMan.getMcc()+"," +ctiMan.getMnc()+")"+
                                                "("+ DateFormat.getDateTimeInstance().format(new Date())+")");
                        pbCheckbar.setVisibility(View.GONE);
                        refreshIsBusy = false ;

                    }
                }, 3000);
            }
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
            LatLng ll ;
            if (bZoomGPS.getTag().equals("Enable")){
                ll = new LatLng(location.getLatitude(),location.getLongitude());
            }
            else{
                ll = new LatLng(Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLat()),
                        Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLon()));
            }
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
        else if (v==bZoomCellTower)
        {
            zoomOperation(false) ;
        }
        else if (v==bZoomGPS)
        {
            zoomOperation(true) ;
        }
        else if (v==bTEI)
        {
        }
    }
}
