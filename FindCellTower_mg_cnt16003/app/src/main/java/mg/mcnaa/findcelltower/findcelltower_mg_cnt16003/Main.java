package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import java.util.ArrayList;
import java.util.Date;

import FindCellTowerApiOperation.CellTowerLocManager;

import static java.security.AccessController.getContext;

public class Main extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {
    private CellTowerManager ctiMan ;
    private Geocoder geoCoder ;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;

    private ImageButton bRefresh ;
    private ImageButton bviewAllCellTowers ;
    private ImageButton bZoomCellTower ;
    private ImageButton bZoomGPS ;
    private ImageButton bTEI ;
    private TextView tStatus1 ;
    private TextView tStatus2 ;
    private ProgressBar pbCheckbar ;

    private DBHandler myDB ;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        if (checkPermissions())
        {
            setContentView(R.layout.activity_main);
             /*int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.MAPS_RECEIVE); */

        /*if (this.getResources().getConfiguration().orientation==2)
           setContentView(R.layout.activity_main_land);
        else
            setContentView(R.layout.activity_main);*/

            bRefresh = (ImageButton) findViewById(R.id.refresh);
            bviewAllCellTowers = (ImageButton) findViewById(R.id.allCellTowers);
            bZoomCellTower = (ImageButton) findViewById(R.id.zoomcelltower);
            bZoomGPS = (ImageButton) findViewById(R.id.zoomgps);
            bTEI = (ImageButton) findViewById(R.id.tei);
            tStatus1 = (TextView) findViewById(R.id.status1);
            tStatus2 = (TextView) findViewById(R.id.status2);
            pbCheckbar = (ProgressBar) findViewById(R.id.checkbar);
            pbCheckbar.setProgress(10) ;
            bRefresh.setOnClickListener(this) ;
            bZoomGPS.setOnClickListener(this) ;
            bZoomCellTower.setOnClickListener(this) ;
            bviewAllCellTowers.setOnClickListener(this) ;
            bTEI.setOnClickListener(this) ;
            tStatus1.setText("") ;
            tStatus2.setText("") ;
            alAllMarkers = new ArrayList<>();
            myDB = new DBHandler(this) ;
            //myDB.deleteAllCellTowers();
            if (googleServicesAvailable()) {
                //Toast.makeText(this, "Google Services are Available!", Toast.LENGTH_LONG).show();
                ctiMan = new CellTowerManager((TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE));
                geoCoder= new Geocoder(this);
                initMap();
                timerHandler.postDelayed(timerRunnable, 0);
            } else {
                //No Google Maps Layout
            }
        }
        else
            setContentView(R.layout.no_rights);


    }

    private boolean checkPermissions()
    {
        ArrayList<String> alPermissions = new ArrayList<>();
        alPermissions.add("android.permission.ACCESS_NETWORK_STATE") ;
        alPermissions.add("android.permission.INTERNET") ;
        alPermissions.add("android.permission.READ_PHONE_STATE") ;
        alPermissions.add("android.permission.ACCESS_COARSE_LOCATION") ;
        alPermissions.add("android.permission.ACCESS_FINE_LOCATION") ;
        alPermissions.add("android.permission.WRITE_EXTERNAL_STORAGE") ;
        //alPermissions.add("mg.mcnaa.findcelltower.findcelltower_mg_cnt16003.permission.MAPS_RECEIVE") ;
        //alPermissions.add("com.google.android.providers.gfs.permissions.READ_GSERVICES") ;
        alPermissions.add("android.permission.ACCESS_FINE_LOCATION") ;
        alPermissions.add("android.permission.ACCESS_COARSE_LOCATION") ;

        Log.i("ΜΙΚΕ", Manifest.permission.MAPS_RECEIVE.toString());
        for(String permission : alPermissions)
            if(!(this.checkCallingOrSelfPermission(permission)==PackageManager.PERMISSION_GRANTED)){
                return false ;
        }

        return true ;
    }


    private boolean refreshIsBusy  = false;
    private void refresh()
    {
        tStatus2.setText("Last attempt to refresh ["+ DateFormat.getDateTimeInstance().format(new Date())+"]");
        if (!refreshIsBusy) {
            if (ctiMan.reload())
            {
                refreshIsBusy = true ;
                pbCheckbar.setVisibility(View.VISIBLE);
                CellTowerManager ctm = myDB.getCellTower(ctiMan.getCellId(),ctiMan.getCellLac(),ctiMan.getMcc(),ctiMan.getMnc()) ;
                if (curMarker!=null){
                    if (viewAllCellTowers)
                        setMarker(curMarker.getTitle(),curMarker.getPosition().latitude,curMarker.getPosition().longitude) ;
                    curMarker.remove();
                }

                if (ctm!=null)
                {
                    ctiMan = ctm ;
                    curMarker=setMarker(ctiMan.getAllInfo(), ctiMan.getLat(), ctiMan.getLon(),true);
                    tStatus1.setText("MTower[CellID="+ctiMan.getCellId()+"][CellLac="+ctiMan.getCellLac()+"] \n"+
                            "(MMC,MNC="+ctiMan.getMcc()+"," +ctiMan.getMnc()+")"+
                            "("+ DateFormat.getDateTimeInstance().format(new Date())+")");
                    pbCheckbar.setVisibility(View.GONE);
                    refreshIsBusy = false ;
                }
                else
                {
                    tStatus1.setText("Trying to find a Cell Tower...") ;
                    CellTowerLocManager.getInstance().loadCellTowerLocation(ctiMan.getMcc(),ctiMan.getMnc(),ctiMan.getCellLac(),ctiMan.getCellId());
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            double lat = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLat());
                            double lon = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLon());
                            ctiMan.setLat(lat);
                            ctiMan.setLon(lon);
                            String info = "";
                            try {
                                info = "Locality: "+geoCoder.getFromLocation(lat, lon, 1).get(0).getLocality();
                                info += "\n Address: "+ geoCoder.getFromLocation(lat, lon, 1).get(0).getAddressLine(0);
                                info += "\n PostalCode: "+ geoCoder.getFromLocation(lat, lon, 1).get(0).getPostalCode();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ctiMan.setInfo(info);
                            curMarker= setMarker(ctiMan.getAllInfo(), lat, lon,true);
                            tStatus1.setText("Tower[CellID="+ctiMan.getCellId()+"][CellLac="+ctiMan.getCellLac()+"] \n"+
                                    "(MMC,MNC="+ctiMan.getMcc()+"," +ctiMan.getMnc()+")"+
                                    "("+ DateFormat.getDateTimeInstance().format(new Date())+")");
                            myDB.insertData(ctiMan) ;
                            pbCheckbar.setVisibility(View.GONE);
                            refreshIsBusy = false ;

                        }
                    }, 3000);
                }
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

        if (mGoogleMap!=null)
        {
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window,null) ;

                    TextView tvLocality = (TextView)v.findViewById(R.id.tv_locality) ;
                    //TextView tvLat = (TextView)v.findViewById(R.id.tv_lat) ;
                    //TextView tvLng = (TextView)v.findViewById(R.id.tv_lng) ;
                    //TextView tvSnippet = (TextView)v.findViewById(R.id.tv_snippet) ;

                    LatLng ll = marker.getPosition() ;
                    tvLocality.setText(marker.getTitle());
                    //tvLat.setText("Latitude: "+ll.latitude);
                    //tvLng.setText("Longitude: "+ll.longitude);
                    //tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });

        }

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

    Marker curMarker ;
    private Marker setMarker(String info,double lat, double lng)
    {
        return setMarker(info,lat,lng,false);
    }
    private Marker setMarker(String info,double lat, double lng,boolean isCurrent)
    {
        MarkerOptions options = new MarkerOptions()
                .title(info)
                .position(new LatLng(lat,lng))
                .icon(BitmapDescriptorFactory.fromResource((isCurrent)?R.mipmap.celltowernow:R.mipmap.celltower));

        return mGoogleMap.addMarker(options) ;
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
            if (zoomGPS){
                ll = new LatLng(location.getLatitude(),location.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,15) ;
                mGoogleMap.animateCamera(update);
            }
            /*else{
                ll = new LatLng(Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLat()),
                        Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLon()));
            }*/
        }
    }

    ArrayList<Marker> alAllMarkers ;
    @Override
    public void onClick(View v) {
        if (v==bRefresh)
        {
            refresh();
        }
        else if (v==bZoomCellTower)
        {
            viewAllCellTowersOperation(!viewAllCellTowers);
        }
        else if (v==bZoomGPS)
        {
            zoomOperation(!zoomGPS) ;
        }
        else if (v==bTEI)
        {
        }
        else if (v==bviewAllCellTowers)
        {
            Intent nAct = new Intent(Main.this, viewCellTowerInfo.class);
            //nAct.putExtras(manQuestions.getBundleOfResults()) ;
            startActivity(nAct);
        }
    }

    boolean viewAllCellTowers = false;
    private void viewAllCellTowersOperation(boolean viewAll)
    {
        viewAllCellTowers = viewAll;
        mGoogleMap.clear();
        setMarker(curMarker.getTitle(),curMarker.getPosition().latitude,curMarker.getPosition().longitude,true) ;
        if (viewAllCellTowers)
        {
            bZoomCellTower.setImageResource(R.mipmap.zoomcelltower);
            ArrayList<CellTowerManager> alAllCellsTower = myDB.getAllCellTowers() ;
            for(CellTowerManager ctm : alAllCellsTower){
                alAllMarkers.add(setMarker(ctm.getAllInfo(),ctm.getLat(),ctm.getLon()));
            }
        }
        else
            bZoomCellTower.setImageResource(R.mipmap.zoomcelltowerdis);
    }

    boolean zoomGPS = true ;
    private void zoomOperation(boolean isGPSzoom)
    {
        zoomGPS = isGPSzoom ;
        if (zoomGPS)
            bZoomGPS.setImageResource(R.mipmap.zoomgps);
        else
            bZoomGPS.setImageResource(R.mipmap.zoomgpsdis);
    }
}
