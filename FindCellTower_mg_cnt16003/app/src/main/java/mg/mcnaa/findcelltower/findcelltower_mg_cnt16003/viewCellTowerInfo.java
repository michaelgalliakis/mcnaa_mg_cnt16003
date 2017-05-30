package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class viewCellTowerInfo extends AppCompatActivity {

    private ListView lvAllInfo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cell_tower_info);

        lvAllInfo = (ListView) findViewById(R.id.lvAllInfo);

        CellTowerManager.findAllCellInfo(this,lvAllInfo);
    }
}
