package FindCellTowerApiOperation;

import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CellTowerLocManager {
    public static final String TAG = CellTowerLocManager.class.getSimpleName();
    public static final String BASE_URL = "https://eu1.unwiredlabs.com/v2/";
    public static final String TOKEN = "9f9a8862d44ad0";

    private static CellTowerLocManager instance;

    private Retrofit retrofit ;
    private AppService appService ;
    private CellTowerLocation cellTowerLocation ;

    private CellTowerLocManager() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        appService = retrofit.create(AppService.class);
        cellTowerLocation = new CellTowerLocation() ;

    }

    public void loadCellTowerLocation(String mcc, String mnc, String lac, String cid) {

        PostRequestCellTowLoc prctl = new PostRequestCellTowLoc(TOKEN,mcc,mnc,lac,cid);


        appService.getCellTowerLocation(prctl).enqueue(new Callback<CellTowerLocation>() {
            @Override
            public void onResponse(Call<CellTowerLocation> call, Response<CellTowerLocation> response) {
                if (response.code() == 200) {

                    Log.i(TAG, response.body().toString());

                    Log.i(TAG, response.headers().toString());
                    Log.i(TAG, response.body().toString());
                    Log.i(TAG, response.message().toString());
                    CellTowerLocation ctl = response.body() ;
                    Log.i(TAG, "Cell Tower Location loaded");
                    instance.updateCellTowerLocation(ctl) ;
                }
            }

            @Override
            public void onFailure(Call<CellTowerLocation> call, Throwable t) {
                Log.e(TAG, "Error while posting the log for Measurement: " + t.getMessage());
            }
        });

        /*
        //Debug
        cellTowerLocation = new CellTowerLocation() ;
        cellTowerLocation.setLat("38.0001894");
        cellTowerLocation.setLon("23.6740257");
        */
    }
    private void updateCellTowerLocation(CellTowerLocation ctl) {
        cellTowerLocation = ctl ;
        cellTowerLocation.printValues();
    }

    public CellTowerLocation getCellTowerLocation() {
        return cellTowerLocation;
    }

    public static synchronized CellTowerLocManager getInstance(){
        if (instance == null)
            instance = new CellTowerLocManager() ;

        return instance;
    }
}
