package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public CellTowerManager() {
    }

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

    public boolean reload(TelephonyManager tm) //return true if there are new values
    {
        boolean areThereNewValues = false ;

        cellLocation = (GsmCellLocation)tm.getCellLocation();
        networkOperator = tm.getNetworkOperator();
        //Log.i("Mike",cellId +" "+ cellLocation.getCid());
        if (cellLocation.getCid()!=cellId)
        {
            cellId = cellLocation.getCid();
            areThereNewValues = true ;
        }
        //Log.i("Mike",cellLac +" "+ cellLocation.getLac());
        if (cellLocation.getLac()!=cellLac)
        {
            cellLac = cellLocation.getLac();
            areThereNewValues = true ;
        }
        if (!TextUtils.isEmpty(networkOperator)) {
            //Log.i("Mike",mcc +" "+ networkOperator.substring(0, 3));
            if (!String.valueOf(networkOperator.substring(0, 3)).equals(mcc))
            {
                mcc = networkOperator.substring(0, 3);
                areThereNewValues = true ;
            }
            //Log.i("Mike",mnc +" "+ networkOperator.substring(3));
            if (!String.valueOf(networkOperator.substring(3)).equals(mnc))
            {
                mnc = networkOperator.substring(3);
                areThereNewValues = true ;
            }
        }
        else if (!mcc.equals("") || !mnc.equals(""))
        {
            mcc = "-1" ;
            mnc = "-1" ;
            areThereNewValues = true ;
        }

        return areThereNewValues ;
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

    public String getCellTowerAppID()
    {
        return cellId+cellLac+mcc+lon; //ID
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

    //Static methods:
    public static ArrayList<String> findAndUpdateLVWithAllCellInfo(TelephonyManager telephonyManager)
    {
        ArrayList<String> list = new ArrayList<String>();
        if(telephonyManager!=null)
        {
            list.add("All Cell Towers (crowth =" +telephonyManager.getAllCellInfo().size() +") [Offline Info]") ;
            for (String cellTowerInfo : getAllCellTowerInfo(telephonyManager))
                list.add(cellTowerInfo) ;
        }

        return list ;
    }

    public static ArrayList<String> getAllCellTowerInfo(TelephonyManager tm)
    {
        List<CellInfo> cellInfos = (List<CellInfo>) tm.getAllCellInfo();

        ArrayList<String> alAllCellTowers = new ArrayList<>() ;
        for(CellInfo cellInfo : cellInfos)
            alAllCellTowers.add(getCellInfo(cellInfo));

        return alAllCellTowers ;
    }

    public static String getCellInfo(CellInfo cellInfo)
    {
        StringBuffer buffer = new StringBuffer() ;
        if (cellInfo instanceof CellInfoLte)
        {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            buffer.append("Type: Lte, Registered ="+((cellInfoLte.isRegistered())?"Yes":"No")+"\n");
            buffer.append("- - - - - - - - - - - - - - - - - - - - - - - -\n");
            if (cellInfoLte.isRegistered())
            {
                buffer.append("Mcc ="+cellInfoLte.getCellIdentity().getMcc()+"\n");
                buffer.append("Mnc ="+cellInfoLte.getCellIdentity().getMnc()+"\n");
                buffer.append("Ci ="+cellInfoLte.getCellIdentity().getCi()+"\n");
                buffer.append("Tac ="+cellInfoLte.getCellIdentity().getTac()+"\n");
            }
            buffer.append("Pci ="+cellInfoLte.getCellIdentity().getPci()+"\n");
            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
            buffer.append("AsuLevel ="+cellSignalStrengthLte.getAsuLevel()+"\n");
            buffer.append("Dbm ="+cellSignalStrengthLte.getDbm()+"\n");
            buffer.append("Level ="+cellSignalStrengthLte.getLevel());
            //buffer.append("TimingAdvance ="+cellSignalStrengthLte.getTimingAdvance());

        }
        else if (cellInfo instanceof CellInfoGsm)
        {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            buffer.append("Type: Gsm, Registered ="+((cellInfoGsm.isRegistered())?"Yes":"No")+"\n");
            buffer.append("- - - - - - - - - - - - - - - - - - - - - - - -\n");
            if (cellInfoGsm.isRegistered())
            {
                buffer.append("Mcc ="+cellInfoGsm.getCellIdentity().getMcc()+"\n");
                buffer.append("Mnc ="+cellInfoGsm.getCellIdentity().getMnc()+"\n");
                buffer.append("Cid ="+cellInfoGsm.getCellIdentity().getCid()+"\n");
                buffer.append("Lac ="+cellInfoGsm.getCellIdentity().getLac()+"\n");
            }
            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
            buffer.append("AsuLevel ="+cellSignalStrengthGsm.getAsuLevel()+"\n");
            buffer.append("Dbm ="+cellSignalStrengthGsm.getDbm()+"\n");
            buffer.append("Level ="+cellSignalStrengthGsm.getLevel());
        }
        else if (cellInfo instanceof CellInfoCdma)
        {
            CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
            buffer.append("Type: Cdma, Registered ="+((cellInfoCdma.isRegistered())?"Yes":"No")+"\n");
            buffer.append("- - - - - - - - - - - - - - - - - - - - - - - -\n");
            if (cellInfoCdma.isRegistered())
            {
                buffer.append("SystemId ="+cellInfoCdma.getCellIdentity().getSystemId()+"\n");
                buffer.append("BasestationId ="+cellInfoCdma.getCellIdentity().getBasestationId()+"\n");
                buffer.append("NetworkId ="+cellInfoCdma.getCellIdentity().getNetworkId()+"\n");
                buffer.append("Latitude ="+cellInfoCdma.getCellIdentity().getLatitude()+"\n");
                buffer.append("Longitude ="+cellInfoCdma.getCellIdentity().getLongitude()+"\n");
            }
            CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
            buffer.append("AsuLevel ="+cellSignalStrengthCdma.getAsuLevel()+"\n");
            buffer.append("Dbm ="+cellSignalStrengthCdma.getDbm()+"\n");
            buffer.append("CdmaDbm ="+cellSignalStrengthCdma.getCdmaDbm()+"\n");
            buffer.append("CdmaEcio ="+cellSignalStrengthCdma.getCdmaEcio()+"\n");
            buffer.append("CdmaLevel ="+cellSignalStrengthCdma.getCdmaLevel()+"\n");
            buffer.append("EvdoDbm ="+cellSignalStrengthCdma.getEvdoDbm()+"\n");
            buffer.append("EvdoEcio ="+cellSignalStrengthCdma.getEvdoEcio()+"\n");
            buffer.append("EvdoLevel ="+cellSignalStrengthCdma.getEvdoLevel()+"\n");
            buffer.append("EvdoSnr ="+cellSignalStrengthCdma.getEvdoSnr()+"\n");
            buffer.append("Level ="+cellSignalStrengthCdma.getLevel());
        }
        else if (cellInfo instanceof CellInfoWcdma)
        {
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            buffer.append("Type: Wcdma, Registered ="+((cellInfoWcdma.isRegistered())?"Yes":"No")+"\n");
            buffer.append("- - - - - - - - - - - - - - - - - - - - - - - -\n");
            if (cellInfoWcdma.isRegistered())
            {
                buffer.append("Mcc ="+cellInfoWcdma.getCellIdentity().getMcc()+"\n");
                buffer.append("Mnc ="+cellInfoWcdma.getCellIdentity().getMnc()+"\n");
                buffer.append("Cid ="+cellInfoWcdma.getCellIdentity().getCid()+"\n");
                buffer.append("Lac ="+cellInfoWcdma.getCellIdentity().getLac()+"\n");
                buffer.append("Psc ="+cellInfoWcdma.getCellIdentity().getPsc()+"\n");
            }
            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
            buffer.append("AsuLevel ="+cellSignalStrengthWcdma.getAsuLevel()+"\n");
            buffer.append("Dbm ="+cellSignalStrengthWcdma.getDbm()+"\n");
            buffer.append("Level ="+cellSignalStrengthWcdma.getLevel());
        }
        return buffer.toString();
    }
}
