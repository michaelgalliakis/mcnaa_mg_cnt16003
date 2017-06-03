package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.Date;

public class Shared {

    public static boolean firstTimeAppStarts = true ;

    public static void showToast(Context context, String message)
    {
        showToast(context, message,true);
    }

    public static void showToast(Context context, String message, boolean isLong)
    {
        Toast.makeText(context, message, (isLong)?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
    }

    public static boolean checkPermissions(Context context)
    {
        ArrayList<String> alPermissions = new ArrayList<>();
        alPermissions.add("android.permission.ACCESS_NETWORK_STATE") ;
        alPermissions.add("android.permission.INTERNET") ;
        alPermissions.add("android.permission.READ_PHONE_STATE") ;
        alPermissions.add("android.permission.ACCESS_COARSE_LOCATION") ;
        alPermissions.add("android.permission.ACCESS_FINE_LOCATION") ;
        alPermissions.add("android.permission.WRITE_EXTERNAL_STORAGE") ;
        alPermissions.add("android.permission.ACCESS_FINE_LOCATION") ;
        alPermissions.add("android.permission.ACCESS_COARSE_LOCATION") ;

        for(String permission : alPermissions)
            if(!(context.checkCallingOrSelfPermission(permission)== PackageManager.PERMISSION_GRANTED)){
                return false ;
            }

        return true ;
    }

    public static void giaNaDoume(Context context)
    {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.MAPS_RECEIVE)
                != PackageManager.PERMISSION_GRANTED) {

          showToast(context,"Mike ole");
        }
    }

    public static void fatalError(Activity activity, String errorMessage)
    {
        Intent intSpl = new Intent(activity, SplashScreen.class);
        intSpl.putExtra("fatalError", errorMessage);
        activity.startActivity(intSpl);
        activity.finish();
    }

    public static boolean googleServicesAvailable(Activity activity) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(activity);
        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(activity, isAvailable, 0);
            dialog.show();
        } else
            Shared.showToast(activity, "Cant connect to play services");
        return false;
    }
}
