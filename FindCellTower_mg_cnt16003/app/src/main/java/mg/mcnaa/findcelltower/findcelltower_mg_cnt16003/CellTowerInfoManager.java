package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

public class CellTowerInfoManager {
    private TelephonyManager telephonyManager;
    private GsmCellLocation cellLocation ;
    private String networkOperator ;
    private String cellId = "-1";
    private String cellLac = "-1";
    private String mcc = "0" ;
    private String mnc = "0" ;

    public CellTowerInfoManager(TelephonyManager tm) {
        telephonyManager = tm ;
    }

    public boolean reload() //return true if there are new values
    {
        boolean areThereNewValues =false ;

        cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
        networkOperator = telephonyManager.getNetworkOperator();
        if (!(String.valueOf(cellLocation.getCid()).equals(cellId)))
        {
            cellId= String.valueOf(cellLocation.getCid());
            areThereNewValues = true ;
        }
        if (!String.valueOf(cellLocation.getLac()).equals(cellLac))
        {
            cellLac = String.valueOf(cellLocation.getLac());
            areThereNewValues = true ;
        }

        if (!TextUtils.isEmpty(networkOperator)) {
            if (!String.valueOf(networkOperator.substring(0, 3)).equals(mcc))
            {
                mcc = networkOperator.substring(0, 3);
                areThereNewValues = true ;
            }
            if (!String.valueOf(networkOperator.substring(3)).equals(mnc))
            {
                mnc = networkOperator.substring(3);
                areThereNewValues = true ;
            }
        }
        return areThereNewValues ;
    }

    public String getCellId() {
        return cellId;
    }

    public String getCellLac() {
        return cellLac;
    }

    public String getMcc() {
        return mcc;
    }

    public String getMnc() {
        return mnc;
    }
}
