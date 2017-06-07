package mg.mcnaa.findcelltower.findcelltower_mg_cnt16003;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreen extends AbsRuntimePermission implements View.OnClickListener {


    private ImageButton bExit;
    private ImageButton bviewMap;
    private ImageButton bviewAllCellTowers;
    private ImageButton bRemoveDB;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_splash_screen);

        requestAppPermissions(R.string.msg, Shared.REQUEST_PERMISSION);

        bExit = (ImageButton) findViewById(R.id.ibExit);
        bviewMap = (ImageButton) findViewById(R.id.viewMap);
        bRemoveDB = (ImageButton) findViewById(R.id.ibRemoveDatabase);
        bviewAllCellTowers = (ImageButton) findViewById(R.id.allCellTowers);
        bExit.setOnClickListener(this);
        bviewMap.setOnClickListener(this);
        bRemoveDB.setOnClickListener(this);
        bviewAllCellTowers.setOnClickListener(this);

        tvStatus = (TextView) findViewById(R.id.tvStatus);

        if ((getIntent().getExtras() != null))
            tvStatus.setText(getIntent().getExtras().getString("fatalError", ""));

        Shared.firstTimeAppStarts = false;
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    public void onClick(View v) {
        if (v == bExit) {
            terminateApp();
        } else if (v == bRemoveDB) {
            confirmDialogRemoveDB(getApplicationContext());
        } else if (v == bviewMap) {
            Intent nAct = new Intent(SplashScreen.this, Main.class);
            startActivity(nAct);
        } else if (v == bviewAllCellTowers) {
            Intent nAct = new Intent(SplashScreen.this, ViewCellTowerInfo.class);
            startActivity(nAct);
        }
    }

    public void terminateApp() {
        Intent intMap = new Intent(getApplicationContext(), Main.class);
        intMap.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intMap.putExtra("EXIT", true);
        Intent intVct = new Intent(getApplicationContext(), ViewCellTowerInfo.class);
        intVct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intVct.putExtra("EXIT", true);
        Intent intSpl = new Intent(getApplicationContext(), SplashScreen.class);
        intSpl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intSpl.putExtra("EXIT", true);

        startActivity(intMap);
        startActivity(intVct);
        startActivity(intSpl);

        moveTaskToBack(true);
    }

    private void confirmDialogRemoveDB(final Context context) {

        final AlertDialog alert = new AlertDialog.Builder(
                new ContextThemeWrapper(this, android.R.style.Theme_Dialog))
                .create();
        alert.setTitle("Προειδοποίηση");
        alert.setMessage("Είσαι σίγουρος ότι θέλεις να διαγράψεις όλα τα CellTowers που έχει αναγνωρίσει η εφαρμογή μέχρι τώρα;");
        alert.setIcon(R.mipmap.removedatabase);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        alert.setButton(DialogInterface.BUTTON_POSITIVE, "Ναι",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DBHandler myDB = new DBHandler(context);
                        myDB.deleteAllCellTowers();
                        Toast.makeText(context, "Διαγράφτηκαν τα Cell Towers", Toast.LENGTH_LONG).show();

                        alert.dismiss();
                    }
                });

        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Όχι",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                    }
                });

        alert.show();
    }
}
