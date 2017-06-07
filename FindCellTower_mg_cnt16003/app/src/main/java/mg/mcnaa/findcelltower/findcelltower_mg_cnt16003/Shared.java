package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class Shared {
    public static final int REQUEST_PERMISSION = 10;

    public static boolean firstTimeAppStarts = true;

    public static void showToast(Context context, String message) {
        showToast(context, message, true);
    }

    public static void showToast(Context context, String message, boolean isLong) {
        Toast.makeText(context, message, (isLong) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void fatalError(Activity activity, String errorMessage) {
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
