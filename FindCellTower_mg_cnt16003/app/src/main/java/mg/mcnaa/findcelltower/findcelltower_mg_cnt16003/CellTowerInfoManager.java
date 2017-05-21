package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

public class CellTowerInfoManager {
    private TelephonyManager telephonyManager;
    private GsmCellLocation cellLocation ;
    private String cellId;
    private String cellLac;
    private String mcc = "0" ;
    private String mnc = "0" ;

    public CellTowerInfoManager(TelephonyManager tm) {
        telephonyManager = tm ;
        reload();
    }

    public void reload()
    {
        cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
        cellId= String.valueOf(cellLocation.getCid());
        cellLac = String.valueOf(cellLocation.getLac());

        String networkOperator = telephonyManager.getNetworkOperator();

        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = networkOperator.substring(0, 3);
            mnc = networkOperator.substring(3);
        }
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
