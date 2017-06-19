package batezganpitorbank.mobileclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TransferFunds extends AppCompatActivity {

    String uri = "http://" + User.ipAddress + ":8080/Final_WebServer/webresources/com.server.account/";
    String function = "Accounts?custId=";
    ArrayAdapter<String> spinnerAdapter;
    String selectedSourceAcct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_funds);

        Button b = (Button) findViewById(R.id.btn_back_funds);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner_accounts_funds);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        new FundsActivity().execute();
    }

    void Confirm() {


    }


    String tp_f_t_amount;String tp_f_t_dest;String tp_f_t_source;
    class FundsActivity extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {

                String in = uri + function + User.currentUserID;
                System.out.println(in);
                InputStream is = new URL(in).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                JSONArray reader;
                reader = new JSONArray(sb.toString());
                is.close();

                //place raw json data to list

                List<JSONObject> obj;
                obj = new ArrayList<>();

                for (int i = 0; i < reader.length(); i++) {
                    obj.add(reader.getJSONObject(i));
                }


                final List<JSONObject> finalObj = obj;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            for (JSONObject i : finalObj) {
                                spinnerAdapter.add(i.getString("accountId"));
                            }
                        } catch (Exception ex) {
                            System.out.println("Exception : " + ex.getClass() + " /// " + ex.getMessage());
                        }
                    }
                });



            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getClass() + " : " + ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String t) {


            Button b = (Button) findViewById(R.id.btn_confirm_funds);
            b.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try{
                        tp_f_t_amount = ((EditText)findViewById(R.id.text_amount_funds)).getText().toString();
                        tp_f_t_dest = ((EditText)findViewById(R.id.text_dest_funds)).getText().toString();
                        new ConfirmActivity().execute();

                        System.out.println("whhhhhhhattt");
                        Thread.sleep(500); // WAIT FOR BRANCHES TO PROCESS TRANSACTIONS

                    }
                    catch (Exception ex){

                        System.out.println("problem: " + ex.getClass() + " : " + ex.getMessage());
                    }


                }
            });
            Spinner spinner = (Spinner) findViewById(R.id.spinner_accounts_funds);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedSourceAcct = parent.getSelectedItem().toString();
                    tp_f_t_source = ((Spinner)findViewById(R.id.spinner_accounts_funds)).getSelectedItem().toString();


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    class ConfirmActivity extends AsyncTask<Void,Void,String>{


        @Override
        protected String doInBackground(Void... params) {
            System.out.println("SOURCE:"+tp_f_t_source);
            System.out.println("DEST:"+tp_f_t_dest);
            System.out.println("AMOUNT:"+tp_f_t_amount);
            try {// LOAD GEOLOCATION INFORMATION
                String latitude = String.valueOf(10.1234);
                String longitude = String.valueOf(15.0001);

                // CALL SERVICE THAT ADDS GEOLOCATION INFORMATION
                String stringToParse = "{" +
                        "\"latitude\":\"" + latitude + "\"," +
                        "\"longitude\":\"" + longitude + "\"" +
                        "}";


                String urlQuery = "http://" + User.ipAddress + ":8080/Final_WebServer/webresources/com.server.geolocation/geoloc";

                LocalConfig.sendJsonObject(stringToParse, urlQuery);

                JSONArray json_geolocations = LocalConfig.readJsonArrayFromUrl("http://" + LocalConfig.restfulIpAddress + ":8080/Final_WebServer/webresources/com.server.geolocation");
                JSONObject resultGeolocationRow = (JSONObject) json_geolocations.get(json_geolocations.length() - 1);

                // POST TRANSFER_FUND REQUEST MADE BY ACCOUNT_ID
                stringToParse = "{" +
                        "\"transType_Id\":4," +
                        "\"branch_Id\":null," +
                        "\"transStatus\":1," +
                        "\"transPostDate\":\"null\"," +
                        "\"transDesc\":\"Transfer Funds\"," +
                        "\"transAmount\":" + "-" + tp_f_t_amount + "," +
                        "\"accountIdRecipient\":" + tp_f_t_dest + "," +
                        "\"geo_LocationId_Trans\":"+ resultGeolocationRow.get("geoLocationId") + "" +
                        "}";

                urlQuery = "http://" + LocalConfig.restfulIpAddress + ":8080/Final_WebServer/webresources/com.server.transactions/transaction?accountId=" + tp_f_t_source;
                LocalConfig.sendJsonObject(stringToParse, urlQuery);
                // POST TRANSFER_FUND REQUEST MADE BY RECIPIENT
                stringToParse = "{" +
                        "\"transType_Id\":4," +
                        "\"branch_Id\":null," +
                        "\"transStatus\":1," +
                        "\"transPostDate\":\"null\"," +
                        "\"transDesc\":\"Transfer Funds\"," +
                        "\"transAmount\":" + tp_f_t_amount + "," +
                        "\"accountIdRecipient\":null," +
                        "\"geo_LocationId_Trans\":"+ resultGeolocationRow.get("geoLocationId") + "" +
                        "}";
                System.out.println(tp_f_t_dest);
                urlQuery = "http://" + LocalConfig.restfulIpAddress + ":8080/Final_WebServer/webresources/com.server.transactions/transaction?accountId=" + tp_f_t_dest;
                LocalConfig.sendJsonObject(stringToParse, urlQuery);

            }
            catch(Exception ex){
                System.out.println("EXCEPTION: " + ex.getClass() + ex.getMessage());
            }
            return "";
        }

        @Override protected void onPostExecute(String s)
        {

        }
    }
}
