package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Date;

public class CellTowerManager {
    private static TelephonyManager telephonyManager;
    private static GsmCellLocation cellLocation ;
    private static String networkOperator ;

    private int cellId = -1;
    private int cellLac = -1;
    private String mcc = "-1" ;
    private String mnc = "-1" ;
    private double lat = 0.0;
    private double lon = 0.0;
    private String info = "" ;
    private Date updatedate ;

    public CellTowerManager(int cellId, int cellLac, String mcc, String mnc, double lat, double lon, String info, Date updatedate) {
        this.cellId = cellId;
        this.cellLac = cellLac;
        this.mcc = mcc;
        this.mnc = mnc;
        this.lat = lat;
        this.lon = lon;
        this.info = info;
        this.updatedate = updatedate;
    }

    public CellTowerManager(TelephonyManager tm) {
        if (telephonyManager==null)
            telephonyManager = tm ;
    }

    public boolean reload() //return true if there are new values
    {
        boolean areThereNewValues =false ;

        cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
        networkOperator = telephonyManager.getNetworkOperator();

       /* for (CellInfo cellInfo :telephonyManager.getAllCellInfo())
            Log.i("MIKE", cellInfo.toString());*/
            //if (cellInfo.isRegistered())
              //  info = cellInfo.toString();

        if (cellLocation.getCid()!=cellId)
        {
            cellId= cellLocation.getCid();
            areThereNewValues = true ;
        }
        if (cellLocation.getLac()!=cellLac)
        {
            cellLac = cellLocation.getLac();
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

    public static void findAllCellInfo(Context context,ListView lv)
    {
        /*
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        CellLocation cl = (GsmCellLocation)tm.getCellLocation();
        String no = tm.getNetworkOperator();

        StringBuffer buffer = new StringBuffer() ;
        buffer.append("CellID: "+cellId+"\n") ;
        buffer.append("CellLac: "+cellLac+"\n") ;
        buffer.append("MCC: "+mcc+"\n") ;
        buffer.append("MNC: "+mnc+"\n") ;

        if(telephonyManager!=null)
        {
            for (CellInfo cellInfo :telephonyManager.getAllCellInfo())
                Log.i("MIKE", cellInfo.toString());

        }*/

        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, android.R.id.text1,values);

        lv.setAdapter(adapter);
    }

    public String getAllInfo()
    {
        StringBuffer buffer = new StringBuffer() ;
        buffer.append("CellID: "+cellId+"\n") ;
        buffer.append("CellLac: "+cellLac+"\n") ;
        buffer.append("MCC: "+mcc+"\n") ;
        buffer.append("MNC: "+mnc+"\n") ;
        buffer.append("Lat: "+lat+"\n") ;
        buffer.append("Lon: "+lon+"\n") ;
        buffer.append(info+"\n") ;

        return buffer.toString() ;
    }

    public String getMcc() {
        return mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public int getCellLac() {
        return cellLac;
    }

    public void setCellLac(int cellLac) {
        this.cellLac = cellLac;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }
}
