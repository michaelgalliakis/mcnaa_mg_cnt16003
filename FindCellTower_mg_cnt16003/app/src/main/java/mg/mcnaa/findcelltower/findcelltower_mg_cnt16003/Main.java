package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.HashMap;

import FindCellTowerApiOperation.CellTowerLocManager;

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
    private TelephonyManager telephonyManager ;
    private DBHandler myDB ;

    private HashMap<String,Marker> hmAllMarkers ;

    Handler timerHandler = new Handler();
    private boolean mIsRunning = true;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mIsRunning) {
                return; // stop when told to stop
            }
            refresh() ;
            timerHandler.postDelayed(this, 5000);
        }
    };
    /*
    void startRepeatingTask() {
        mIsRunning = true;
        timerRunnable.run();
    }
    */
    void stopRepeatingTask() {
        mIsRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }
        if (Shared.checkPermissions(this))
        {
            if (Shared.googleServicesAvailable(this)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                setContentView(R.layout.activity_main);
                telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE) ;
                ctiMan = new CellTowerManager();
                geoCoder= new Geocoder(this);
                initMap();

                bRefresh = (ImageButton) findViewById(R.id.refresh);
                bviewAllCellTowers = (ImageButton) findViewById(R.id.allCellTowers);
                bZoomCellTower = (ImageButton) findViewById(R.id.zoomcelltower);
                bZoomGPS = (ImageButton) findViewById(R.id.zoomgps);
                bTEI = (ImageButton) findViewById(R.id.tei);
                tStatus1 = (TextView) findViewById(R.id.status1);
                tStatus2 = (TextView) findViewById(R.id.status2);
                pbCheckbar = (ProgressBar) findViewById(R.id.checkbar);
                //pbCheckbar.setProgress(10) ;

                bRefresh.setOnClickListener(this) ;
                bZoomGPS.setOnClickListener(this) ;
                bZoomCellTower.setOnClickListener(this) ;
                bviewAllCellTowers.setOnClickListener(this) ;
                bTEI.setOnClickListener(this) ;

                tStatus1.setText("") ;
                tStatus2.setText("") ;
                hmAllMarkers = new HashMap<>();
                myDB = new DBHandler(this) ;

                timerHandler.postDelayed(timerRunnable, 0);
            } else
                Shared.fatalError(this,"Not google services available!");
        }
        else
            Shared.fatalError(this,SplashScreen.STATUS_PER_MESS1);
    }

    private boolean refreshIsBusy  = false;
    private void setRefreshBusy(boolean isBusy)
    {
        refreshIsBusy = isBusy ;

        if (refreshIsBusy)
            pbCheckbar.setVisibility(View.VISIBLE);
        else
            pbCheckbar.setVisibility(View.GONE);
    }
    private boolean refreshFailed = false ;
    private CellTowerManager lastCTM ;
    private void refresh()
    {
        changeStatus2("Τελευταία ανανέωση: ["+ DateFormat.getDateTimeInstance().format(new Date())+"]");
        if (!refreshIsBusy) {
            lastCTM = ctiMan.clone();
            if (ctiMan.reload(telephonyManager)==1)
            {
                changeStatus1("Άλλαξε ο σταθμός βάσης!") ;
                setRefreshBusy(true) ;
                CellTowerManager ctm = myDB.getCellTower(ctiMan.getCellId(),ctiMan.getCellLac(),ctiMan.getMcc(),ctiMan.getMnc()) ;

                if (viewAllCellTowers)
                    setMarker(lastCTM);
                else
                    removeMarker(lastCTM);

                if (ctm!=null)
                {
                    changeStatus1("O σταθμός βάσης βρέθηκε στην DB!") ;
                    ctiMan = ctm ;
                    setMarker(ctiMan,true);
                    changeStatus1("[DB] MTower[CellID="+ctiMan.getCellId()+"][CellLac="+ctiMan.getCellLac()+"] \n"+
                            "(MMC,MNC="+ctiMan.getMcc()+"," +ctiMan.getMnc()+")"+
                            "("+ DateFormat.getDateTimeInstance().format(new Date())+")");
                    setRefreshBusy(false) ;
                }
                else
                {
                    changeStatus1("O σταθμός βάσης δεν βρέθηκε στην DB\nκαι γίνεται προσπάθεια εύρεσης του από το API...") ;
                    if (CellTowerLocManager.getInstance().loadCellTowerLocation(ctiMan.getMcc(),ctiMan.getMnc(),ctiMan.getCellLac(),ctiMan.getCellId()))
                    {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    double lat = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLat());
                                    double lon = Double.parseDouble(CellTowerLocManager.getInstance().getCellTowerLocation().getLon());

                                    ctiMan.setLat(lat);
                                    ctiMan.setLon(lon);

                                    String info = "";
                                    info = "Locality: "+geoCoder.getFromLocation(lat, lon, 1).get(0).getLocality();
                                    info += "\nAddress: "+ geoCoder.getFromLocation(lat, lon, 1).get(0).getAddressLine(0);
                                    info += "\nPostalCode: "+ geoCoder.getFromLocation(lat, lon, 1).get(0).getPostalCode();
                                    ctiMan.setInfo(info);

                                    setMarker(ctiMan,true);
                                    changeStatus1("[API] Tower[CellID="+ctiMan.getCellId()+"][CellLac="+ctiMan.getCellLac()+"] \n"+
                                            "(MMC,MNC="+ctiMan.getMcc()+"," +ctiMan.getMnc()+")"+
                                            "("+ DateFormat.getDateTimeInstance().format(new Date())+")");
                                    myDB.insertData(ctiMan) ;
                                }
                                catch(Exception ex)
                                {
                                    changeStatus1("Πρόβλημα κατά την διάρκεια εύρεσης πληροφοριών\nτοποθεσίας.Ελέγξτε την σύνδεση σας με το internet!",true) ;
                                    refreshFailed = true ;
                                    stopRepeatingTask();
                                    changeStatus2("Σταμάτησε η αυτόματη ανανέωση!",false) ;
                                }
                                setRefreshBusy(false) ;
                            }
                        }, 3000);
                    }
                    else
                    {
                        changeStatus1("Πρόβλημα κατά την διάρκεια επικοινωνίας με το API!",true) ;
                        Shared.showToast(this,"Πρόβλημα κατά την διάρκεια επικοινωνίας με το API!");
                        setRefreshBusy(false) ;
                        refreshFailed = true ;
                        stopRepeatingTask();
                        changeStatus2("Σταμάτησε η αυτόματη ανανέωση!",false) ;
                    }
                }
            }
            else  if (ctiMan.reload(telephonyManager)==-1)
            {
                changeStatus1("Πρόβλημα κατά την διάρκεια επικοινωνίας με το σταθμό βάσης!",true) ;
                Shared.showToast(this,"Πρόβλημα κατά την διάρκεια επικοινωνίας με το σταθμό βάσης!");
                setRefreshBusy(false) ;
                refreshFailed = true ;
                stopRepeatingTask();
                changeStatus2("Σταμάτησε η αυτόματη ανανέωση!",false) ;
            }
        }
        else
            Shared.showToast(this, "Refresh operation is busy");
    }

    private void changeStatus1(String message)
    {
        changeStatus1(message,false);
    }

    private void changeStatus1(String message, boolean redColor)
    {
        tStatus1.setText(message) ;
        if (redColor)
            tStatus1.setTextColor(Color.RED);
        else
            tStatus1.setTextColor(Color.BLUE);
    }

    private void changeStatus2(String message)
    {
        changeStatus2(message,true);
    }

    private void changeStatus2(String message, boolean greenColor)
    {
        tStatus2.setText(message) ;
        if (greenColor)
            tStatus2.setTextColor(Color.GREEN);
        else
            tStatus2.setTextColor(Color.RED);
    }

    @Override
    public void onClick(View v) {
        if (v==bRefresh)
        {
            if (refreshFailed)
            {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            else
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
            Intent nAct = new Intent(Main.this, SplashScreen.class);
            startActivity(nAct);
        }
        else if (v==bviewAllCellTowers)
        {
            Intent nAct = new Intent(Main.this, ViewCellTowerInfo.class);
            //nAct.putExtras(manQuestions.getBundleOfResults()) ;
            startActivity(nAct);
        }
    }
/*
    private Marker setMarker(String info,double lat, double lng)
    {
        return setMarker(info,lat,lng,false);
    }

    private Marker setMarker(String info,double lat, double lng,boolean isCurrent)
    {
        MarkerOptions options = new MarkerOptions()
                .title(info)
                .position(new LatLng(lat,lng))
                .icon(BitmapDescriptorFactory.fromResource((isCurrent)?R.mipmap.celltower2now:R.mipmap.celltower2));

        return mGoogleMap.addMarker(options) ;
    }
*/
    private void setMarker(CellTowerManager ctm)
    {
        setMarker(ctm,false) ;
    }

    private void setMarker(CellTowerManager ctm,boolean isCurrent)
    {
        removeMarker(ctm);
        MarkerOptions options = new MarkerOptions()
                .title(ctm.getAllInfo())
                .position(new LatLng(ctm.getLat(),ctm.getLon()))
                .icon(BitmapDescriptorFactory.fromResource((isCurrent)?R.mipmap.celltower2now:R.mipmap.celltower2));

        hmAllMarkers.put(ctm.getCellTowerAppID(),mGoogleMap.addMarker(options)) ;
    }

    private void removeMarker(CellTowerManager ctm )
    {
        if (hmAllMarkers.get(ctm.getCellTowerAppID())!=null)
            hmAllMarkers.get(ctm.getCellTowerAppID()).remove();
    }
    private void removeAllMarkers()
    {
        mGoogleMap.clear();
        hmAllMarkers.clear();
    }

    boolean viewAllCellTowers = false;
    private void viewAllCellTowersOperation(boolean viewAll)
    {
        viewAllCellTowers = viewAll;
        removeAllMarkers();
        //if (curMarker!=null)
            //setMarker(curMarker.getTitle(),curMarker.getPosition().latitude,curMarker.getPosition().longitude,true) ;
        if (viewAllCellTowers)
        {
            bZoomCellTower.setImageResource(R.mipmap.zoomcelltower);
            ArrayList<CellTowerManager> alAllCellsTower = myDB.getAllCellTowers() ;
            for(CellTowerManager ctm : alAllCellsTower){
                if (!ctm.getCellTowerAppID().equals(ctiMan.getCellTowerAppID()))
                    setMarker(ctm);
                else
                    setMarker(ctiMan,true);
            }
        }
        else
        {
            bZoomCellTower.setImageResource(R.mipmap.zoomcelltowerdis);
            setMarker(ctiMan,true);
        }

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

    //Map:
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    Marker lastOpenned = null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
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

                    ll = new LatLng(ll.latitude+0.005,ll.longitude); //0.005 για
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,15) ;
                    mGoogleMap.animateCamera(update);


                    //tvLat.setText("Latitude: "+ll.latitude);
                    //tvLng.setText("Longitude: "+ll.longitude);
                    //tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    // Check if there is an open info window
                    if (lastOpenned != null) {
                        // Close the info window
                        lastOpenned.hideInfoWindow();

                        // Is the marker the same marker that was already open
                        if (lastOpenned.equals(marker)) {
                            // Nullify the lastOpenned object
                            lastOpenned = null;
                            // Return so that the info window isn't openned again
                            return true;
                        }
                    }

                    // Open the info window for the marker
                    marker.showInfoWindow();
                    // Re-assign the last openned such that we can close it later
                    lastOpenned = marker;

                    // Event was handled by our code do not launch default behaviour.
                    return true;
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

    private LocationRequest mLocationRequest;
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
            Shared.showToast(this, "Cant get current location!");
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
}
