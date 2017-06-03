package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewCellTowerInfo extends AppCompatActivity implements View.OnClickListener {

    private ListView lvAllInfo ;
    private ImageButton bRefresh ;
    private ImageButton bviewMap ;
    private ImageButton bTEI ;
    private TelephonyManager telephonyManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_view_cell_tower_info);

        bRefresh = (ImageButton) findViewById(R.id.refresh);
        bviewMap = (ImageButton) findViewById(R.id.viewMap);
        bTEI = (ImageButton) findViewById(R.id.tei);
        bRefresh.setOnClickListener(this);
        bviewMap.setOnClickListener(this) ;
        bTEI.setOnClickListener(this) ;

        lvAllInfo = (ListView) findViewById(R.id.lvAllInfo);
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        refreshList();
    }

    private void refreshList()
    {
        try{
            ArrayList<String> alList = CellTowerManager.findAndUpdateLVWithAllCellInfo(telephonyManager);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,alList);
            lvAllInfo.setAdapter(adapter);
        }
        catch(Exception ex)
        {
            Shared.fatalError(this,"Error while refreshing the list!") ;
        }
    }

    @Override
    public void onClick(View v) {
        if (v==bRefresh)
        {
            refreshList();
        }
        else if (v==bTEI)
        {
            Intent nAct = new Intent(ViewCellTowerInfo.this, SplashScreen.class);
            startActivity(nAct);
        }
        else if (v==bviewMap)
        {
            Intent nAct = new Intent(ViewCellTowerInfo.this, Main.class);
            startActivity(nAct);
        }
    }
}
