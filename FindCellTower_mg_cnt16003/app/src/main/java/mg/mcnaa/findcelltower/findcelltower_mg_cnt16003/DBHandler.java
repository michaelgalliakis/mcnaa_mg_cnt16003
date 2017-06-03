package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "CellTowerPoints.db" ;
    public static final String TABLE_NAME = "CellTowers" ;
    private static final SimpleDateFormat DATAFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static final String COL_1 = "CellID" ;
    public static final String COL_2 = "CellLac" ;
    public static final String COL_3 = "MCC" ;
    public static final String COL_4 = "MNC" ;
    public static final String COL_5 = "Lat" ;
    public static final String COL_6 = "Lon" ;
    public static final String COL_7 = "Info" ;
    public static final String COL_8 = "Updatedate" ;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    public void deleteAllCellTowers()
    {
        SQLiteDatabase db = this.getWritableDatabase() ;
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(CellID INTEGER,CellLac INTEGER"+
                    ",MCC TEXT, MNC TEXT,Lat REAL,Lon REAL,Info TEXT,Updatedate DATETIME,"+
                    "PRIMARY KEY (CellID,CellLac,MCC,MNC));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db) ;
    }

    public boolean insertData(int cellID, int cellLac, int mcc, int mnc, float lat, float lon, String info)
    {
        SQLiteDatabase db = this.getWritableDatabase() ;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,cellID);
        contentValues.put(COL_2,cellLac);
        contentValues.put(COL_3,mcc);
        contentValues.put(COL_4,mnc);
        contentValues.put(COL_5,lat);
        contentValues.put(COL_6,lon);
        contentValues.put(COL_7,info);
        contentValues.put(COL_8, getDateTime());
        long result = db.insert(TABLE_NAME,null,contentValues) ;

        if (result==-1)
            return false ;
        else
            return true ;
    }

    public boolean insertData(CellTowerManager ctm)
    {
        SQLiteDatabase db = this.getWritableDatabase() ;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,ctm.getCellId());
        contentValues.put(COL_2,ctm.getCellLac());
        contentValues.put(COL_3,ctm.getMcc());
        contentValues.put(COL_4,ctm.getMnc());
        contentValues.put(COL_5,ctm.getLat());
        contentValues.put(COL_6,ctm.getLon());
        contentValues.put(COL_7,ctm.getInfo());
        contentValues.put(COL_8, getDateTime());
        long result = db.insert(TABLE_NAME,null,contentValues) ;

        if (result==-1)
            return false ;
        else
            return true ;
    }

    public CellTowerManager getCellTower(int cellID,int cellLac,String mcc, String mnc)
    {
        SQLiteDatabase db = this.getWritableDatabase() ;

        String[] whereArgs = new String[] {
                String.valueOf(cellID),
                String.valueOf(cellLac),
                mcc,
                mnc };
        String queryString =
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE CellID = ? AND CellLac = ? AND MCC = ? AND MNC = ?";

        CellTowerManager ctm = null;
        Cursor cur = db.rawQuery(queryString,whereArgs);
        if(cur.moveToNext()){
            Date date ;
            try {
                date = DATAFORMAT.parse(cur.getString(7));
            } catch (ParseException e) {
                date = new Date();
            }
            ctm = new CellTowerManager(cur.getInt(0),cur.getInt(1),
                    cur.getString(2),cur.getString(3),cur.getDouble(4),
                    cur.getDouble(5),cur.getString(6),date);
        }
        return ctm ;
    }

    /*
    public boolean updateCellTower(int cellID,int cellLac,int mcc, int mnc,float lat, float lon,String info)
    {
        String[] whereArgs = new String[] {
                String.valueOf(cellID),
                String.valueOf(cellLac),
                String.valueOf(mcc),
                String.valueOf(mnc) };
        String queryString = " CellID = ? AND CellLac = ? AND MCC = ? AND MNC = ?";
        SQLiteDatabase db = this.getWritableDatabase() ;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,cellID);
        contentValues.put(COL_2,cellLac);
        contentValues.put(COL_3,mcc);
        contentValues.put(COL_4,mnc);
        contentValues.put(COL_5,lat);
        contentValues.put(COL_6,lon);
        contentValues.put(COL_7,info);
        contentValues.put(COL_8, getDateTime());
        long result = db.update(TABLE_NAME,contentValues,queryString,whereArgs) ;

        if (result==-1)
            return false ;
        else
            return true ;
    }
    */

    //CellTowerManager(int cellId, int cellLac, String mcc, String mnc, double lat, double lon, String info, Date updatedate)
    public ArrayList<CellTowerManager> getAllCellTowers()
    {
        SQLiteDatabase db = this.getWritableDatabase() ;
        Cursor cur = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        ArrayList<CellTowerManager> alCellToweres = new ArrayList<>();
        while(cur.moveToNext())
        {
            Date date ;
            try {
                date = DATAFORMAT.parse(cur.getString(7));
            } catch (ParseException e) {
                date = new Date();
            }
            alCellToweres.add(new CellTowerManager(cur.getInt(0),cur.getInt(1),
                    cur.getString(2),cur.getString(3),cur.getDouble(4),
                    cur.getDouble(5),cur.getString(6),date)) ;
        }
        cur.close();
        return alCellToweres ;
    }

    private static String getDateTime() {

        Date date = new Date();
        return DATAFORMAT.format(date);
    }

    private static String getDateTime(Date date) {
        return DATAFORMAT.format(date);
    }
}
