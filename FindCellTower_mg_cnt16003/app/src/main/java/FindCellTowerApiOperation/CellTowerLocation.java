package FindCellTowerApiOperation;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CellTowerLocation {
    public static final String TAG = CellTowerLocation.class.getSimpleName();
    @SerializedName("status")
    @Expose
    private String status = "";
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("accuracy")
    @Expose
    private String accuracy;
    @SerializedName("help")
    @Expose
    private String help;

    public CellTowerLocation() {
        status = "Not Ready";
    }

    public CellTowerLocation(String status, String message, String balance, String lat, String lon, String accuracy, String help) {
        this.status = status;
        this.message = message;
        this.balance = balance;
        this.lat = lat;
        this.lon = lon;
        this.accuracy = accuracy;
        this.help = help;
    }

    public void printValues() //Debug Tool:
    {
        if (status.equals("ok")) {
            Log.i(TAG, status);
            Log.i(TAG, balance);
            Log.i(TAG, lat);
            Log.i(TAG, lon);
            Log.i(TAG, accuracy);
        }
        else {
            Log.i(TAG, status);
            Log.i(TAG, message);
            Log.i(TAG, help);
        }
    }

    public boolean isStatusOK()
    {
        if ("OK".equals(status.toUpperCase()))
            return true ;
        else
            return false ;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }
}
