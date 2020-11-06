package org.cri.deliveryrecord;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.cri.deliveryrecord.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
//comment
     /*   String[] deliveryPoint = { "8 Prua", "8 Poppa", "6 Prua", "6 Poppa", "Covid" };

        Spinner spin = (Spinner) findViewById(R.id.sp_DeliveryPointSelection);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, deliveryPoint);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
*/
        ItemDatabaseHandler dbItem = ItemDatabaseHandler.getInstance(this);
        MapDatabaseHandler dbMap = MapDatabaseHandler.getInstance(this);

       importMap(null);
       Log.d("GE>> ", "MainActivity");
    }

     public void importMap(View view){

        new Thread() {
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            TextView labelResult = findViewById(R.id.label_Result);
                            labelResult.setVisibility(View.VISIBLE);
                            labelResult.setText(" - Aggiornamento - ");
                            labelResult.setBackgroundColor(0xFFFFEB3B);
                            Button btnCheck = findViewById(R.id.btn_checkGuest);
                            btnCheck.setEnabled(true);
                        }
                });

                URL url = null;
                try {
                    url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vT7nAaO0NkaGKY7Retpvbz9GPjgMFweRHsHgtX2JcwXemdhrM7D5cy1uKCfSCkkGJKUyCeg34VrCuO6/pub?gid=0&single=true&output=csv");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {

                    HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    MapDatabaseHandler dbMap = MapDatabaseHandler.getInstance();
                    dbMap.setUpdateInProgress(true);
                    String row = null;

                    int [] partTot = {0,0,0,0,0,0};

                    int i=0,j=0;
                    row = reader.readLine();
                    String[] split = row.split(",");
                    String previous = "";
                    if (split[0].compareTo("ID") == 0) {
                        dbMap.deleteAllMap();
                        while ((row = reader.readLine()) != null) {
                            split = row.split(",");
                            dbMap.addMap(new DelPointMap(Integer.parseInt(split[0]), split[1], split[2]));
                            i++;
                            if (split[1].compareTo(previous) != 0)  {
                                j=0;
                            }
                            partTot[Integer.parseInt(split[1])] = ++j;

                            previous = split[1] ;
                        }
                        dbMap.setPartTot(partTot);
                        Log.d("GE>> Mapping importati", "" + i);
                        Log.d("GE>> Mapping Partiali", "" + partTot[0] + " - " + partTot[1] + " - " +
                                partTot[2] + " - " + partTot[3] + " - " + partTot[4] );
                        dbMap.setUpdateInProgress(false);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView labelResult = findViewById(R.id.label_Result);
                                Button btnCheck = findViewById(R.id.btn_checkGuest);
                                btnCheck.setEnabled(true);
                                labelResult.setVisibility(View.INVISIBLE);

                                MapDatabaseHandler dbMap = MapDatabaseHandler.getInstance();
                                int totBySel = dbMap.getCountByDelPoint(1);
                                ItemDatabaseHandler db = ItemDatabaseHandler.getInstance();
                                db.setDeliveryPointSelection(1);
                                int tot = db.getTot();
                                TextView labelTOT = findViewById(R.id.label_TOT);
                                labelTOT.setText("TOT: " + tot + "/" + totBySel);
                            }
                        });
                    }
                } catch (IOException e) {
            Log.e("GE>> ", e.toString() );
            e.printStackTrace();
        }

            }
        }.start();


    }

    public void checkGuest(View view) throws GeneralSecurityException, IOException {

        ItemDatabaseHandler db = ItemDatabaseHandler.getInstance();
        MapDatabaseHandler dbMap = MapDatabaseHandler.getInstance();
        EditText editText_guestId=findViewById(R.id.guestId);
        String value = editText_guestId.getText().toString();
        if (value.compareTo("") == 0) return;

        int idValue=  Integer.parseInt(editText_guestId.getText().toString());
            editText_guestId.selectAll();

        TextView labelResult =findViewById(R.id.label_Result);
        Guest guest = db.getContact(idValue);

        if (guest != null){
            labelResult.setVisibility(View.VISIBLE);
            labelResult.setText("Cons. alle: " + guest.getDeliveryTime());
            labelResult.setBackgroundColor(0xFFF44336);
            //Log.d("GE>>", "giàmmangiato alle: " + guest.getDeliveryTime());

        }else{
            DelPointMap map = dbMap.getMap(idValue);

            if (map != null){
                String delPoint = map.getDeliveryPoint();
                String delPointNote = map.getNote();

                Spinner spinner = (Spinner) findViewById(R.id.sp_DeliveryPointSelection);
                int selection = spinner.getSelectedItemPosition();
                selection++;

                if (map.getDeliveryPoint().compareTo(""+selection) == 0) {
                    Time now = new Time();
                    now.setToNow();
                    int hour = now.hour;
                    int minute = now.minute;
                    int second = now.second;
                    String minuteS = "";
                    if (minute < 9)
                        minuteS = "0" + minute;
                    else
                        minuteS = "" + minute;
                    String secondS = "";
                    if (second < 9)
                        secondS = "0" + second;
                    else
                        secondS = "" + second;

                    db.addContact(new Guest(idValue, "" + hour + ":" + minuteS + ":" + secondS));
                    labelResult.setVisibility(View.VISIBLE);
                    labelResult.setText(" - OK - ");
                    labelResult.setBackgroundColor(0xFF4CAF50);
                    String tot = "TOT: " + db.getTot() + "/" + dbMap.getPartTot()[selection];
                    TextView labelTOT = findViewById(R.id.label_TOT);
                    labelTOT.setText(tot);
                }else {
                    labelResult.setVisibility(View.VISIBLE);
                    labelResult.setText("Errore: " + delPointNote);
                    labelResult.setBackgroundColor(0xFFF44336);
                }
            }else{
                labelResult.setVisibility(View.VISIBLE);
                labelResult.setText("Errore: ID Errato" );
                labelResult.setBackgroundColor(0xFFF44336);
            }
        }
    }


    public void resetDB(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Richiesta Conferma")
                .setMessage("Sei sicuro di voler cancellare tutto? Deve essere fatto solo ogni volta che si comincia una nuova consegna")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ItemDatabaseHandler db = ItemDatabaseHandler.getInstance();
                        db.deleteAllItems();                    }})
                .setNegativeButton("NO", null).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void exportDB(View view) throws IOException {


        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            String[] strPerm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, strPerm, 101);

        }
        Time now = new Time();
        now.setToNow();
        File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"");

        Log.d("GE>>","exportDir");
        File file = new File(exportDir, "ConsegneOspiti-" + now.yearDay + "-"+ now.hour + ".csv");

        CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
        ItemDatabaseHandler db = ItemDatabaseHandler.getInstance();
        List<Guest> contacts = db.getAllContacts();

        for (Guest cn : contacts) {
            String arrStr[] ={""+cn.getID(), cn.getDeliveryTime() };
            csvWrite.writeNext(arrStr);
        }
        csvWrite.close();


    }


    public void getMissingID(View v){
        ItemDatabaseHandler db = ItemDatabaseHandler.getInstance();
        int selection = db.getDeliveryPointSelection();
        List<String> missingIDList = db.getMissingID(selection);
        TextView missingID = findViewById(R.id.label_MissingID);
        missingID.setText(missingIDList.toString());
    }

    public void getDeliveredID(View v){
        ItemDatabaseHandler db = ItemDatabaseHandler.getInstance();
        List<Guest> deliveryIDList = db.getAllContacts();
        TextView deliveredID = findViewById(R.id.label_MissingID);
        deliveredID.setText(deliveryIDList.toString());
    }

 /*       private void getIdDistributionPointMap() throws GeneralSecurityException, IOException {
        //    SpreadsheetService service = new SpreadsheetService("org.cri.gestioneconsegneospiti");

            String APPLICATION_NAME = "Google Sheets API Java Quickstart";
            JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            //String TOKENS_DIRECTORY_PATH = "tokens";
            List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
            //String CREDENTIALS_FILE_PATH = "/credentials.json";

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final String spreadsheetId = "13hXwqbgfbRpDPnMWLb4Emvb1AzaJHiH7HoPiFIaKaFg";
            final String range = "Sheet1!A:B";

            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, null).setApplicationName(APPLICATION_NAME).build();
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
            } else {
                System.out.println("Name, Major");
                for (List row : values) {
                    // Print columns A and E, which correspond to indices 0 and 4.
                    System.out.printf("%s, %s\n", row.get(0), row.get(1));
                }
            }



    }*/




}